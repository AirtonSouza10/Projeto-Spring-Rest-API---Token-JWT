package curso.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;


@Service
public class ImplementacaoUserDetailsService  implements UserDetailsService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//consultar no BD o usuario
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		if(usuario == null) {
			throw new UsernameNotFoundException("usuário não foi encontrado");
		}
		
		return new User(usuario.getLogin(), usuario.getPassword(), usuario.getAuthorities());
	}

	public void insereAcessoPadrao(Long id) {
		//descobre a constraint de restricao
		String constraint = usuarioRepository.consultaConstraintRole();
		
		if(constraint != null) {
		
		/*
		//remove a constraint
		usuarioRepository.removerConstraintRole(constraint);
		*/
		jdbcTemplate.execute("alter table usuario_role DROP CONSTRAINT" + constraint);
		
		}
		
		//insere acesso padrao
		usuarioRepository.insereAcessoRolePadrao(id);
		
	}

}
