package curso.api.rest.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import curso.api.rest.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);

	@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> findUserByNome(String nome);

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "update usuario set token = ?1 where login = ?2")
	void atualizaTokenUser(String token, String login);

	@Query(nativeQuery = true, value = "select constraint_name from information_schema.constraint_column_usage"
			+ " where table_name = 'usuario_role' and column_name = 'role_id' and constraint_name <> 'unique_role_user';")
	String consultaConstraintRole();

	/*
	 * @Modifying
	 * 
	 * @Query(nativeQuery = true, value =
	 * "alter table usuario_role DROP CONSTRAINT ?1;") void
	 * removerConstraintRole(String constraint);
	 */

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "insert into usuario_role (usuario_id, role_id) "
			+ "values (?1, (select id from role where nome_role = 'ROLE_USER'));")
	void insereAcessoRolePadrao(Long id);

	@Transactional
	@Modifying
	@Query(value = "update usuario set senha = ?1 where id = ?2", nativeQuery = true)
	void updateSenha(String senha, Long CodUser);

	default Page<Usuario> findUserByNomePage(String nome, PageRequest pageRequest) {

		Usuario usuario = new Usuario();
		usuario.setNome(nome);

		// configura para pesquisar por nome e pagiar
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny().withMatcher("nome",
				ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		// junta as configs
		Example<Usuario> example = Example.of(usuario, exampleMatcher);
		// paginação
		Page<Usuario> retorno = findAll(example, pageRequest);

		return retorno;

	}

}
