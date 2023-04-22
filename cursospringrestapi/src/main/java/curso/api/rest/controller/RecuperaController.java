package curso.api.rest.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.ObjetoErro;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ServiceEnviaEmail;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ServiceEnviaEmail serviceEnviaEmail;

	@ResponseBody
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoErro> recuperar(@RequestBody Usuario login) throws MessagingException {

		ObjetoErro objetoError = new ObjetoErro();

		Usuario user = usuarioRepository.findUserByLogin(login.getLogin());

		if (user == null) {
			objetoError.setCode("404"); // nao encontrado
			objetoError.setError("usuario não encontrado");
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova = dateFormat.format(Calendar.getInstance().getTime());
			String senhaCriptografada = new BCryptPasswordEncoder().encode(senhaNova);
			usuarioRepository.updateSenha(senhaCriptografada, user.getId());
			serviceEnviaEmail.eviarEmail("Recuperação de senha", user.getLogin(), "Sua nova seha é : " + senhaNova);

			// rotina de nevio de email
			objetoError.setCode("200"); // encontrado
			objetoError.setError("Acesso ennviado para seu e-mail");
		}

		return new ResponseEntity<ObjetoErro>(objetoError, HttpStatus.OK);

	}

}
