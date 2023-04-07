package curso.api.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.TelefoneRepository;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ImplementacaoUserDetailsService;

@CrossOrigin
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}

	// -----------------------------------------------------------------------------

	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuario() throws InterruptedException {

		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();

		// Thread.sleep(6000);segura o codigo 6 segundos simulando um processo lento

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);

	}

	// ----------------------------------------------------------------------------
	// EndPoint buscar usuario por nome
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) throws InterruptedException {

		List<Usuario> list = (List<Usuario>) usuarioRepository.findUserByNome(nome);

		// Thread.sleep(6000);segura o codigo 6 segundos simulando um processo lento

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);

	}

	// ----------------------------------------------------------------------------
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

		/**
		 * //consumindo api externa - cep URL url = new
		 * URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/"); URLConnection
		 * connection = url.openConnection(); InputStream is =
		 * connection.getInputStream(); BufferedReader br = new BufferedReader(new
		 * InputStreamReader(is, "UTF-8"));
		 * 
		 * String cep = ""; StringBuilder jsonCep = new StringBuilder();
		 * 
		 * while((cep = br.readLine()) != null) { jsonCep.append(cep); }
		 * 
		 * //pega os atributos e converter para esse user aux Usuario userAux = new
		 * Gson().fromJson(jsonCep.toString(), Usuario.class);
		 * 
		 * usuario.setCep(userAux.getCep());
		 * usuario.setLogradouro(userAux.getLogradouro());
		 * usuario.setComplemento(userAux.getComplemento());
		 * usuario.setBairro(userAux.getBairro());
		 * usuario.setLocalidade(userAux.getLocalidade());
		 * usuario.setUf(userAux.getUf()); //consumindo api externa - cep
		 * 
		 **/

		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);

	}

	// ---------------------------------------------------------------------------------
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();

		if (!userTemporario.getSenha().equals(usuario.getSenha())) { // se forem diferentes
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}

		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);

	}

	// ---------------------------------------------------------------------------------
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable("id") Long id) {

		usuarioRepository.deleteById(id);

		return "ok";

	}

	// -----------------------------Telefone--------------------------------------
	@DeleteMapping(value = "/removerTelefone/{id}", produces = "application/text")
	public String deleteTelefone(@PathVariable("id") Long id) {

		telefoneRepository.deleteById(id);

		return "ok";

	}

}
