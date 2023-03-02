package curso.api.rest.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import curso.api.rest.model.Usuario;


//obrigamos a autenticar a url
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter{

	//configurando o gerenciador de autenticacao
	protected JWTLoginFilter(String url, AuthenticationManager authenticationManager) {
		//obriga a autenticar a url
		super(new AntPathRequestMatcher(url));
		
		//gerencaidor de autenticacao
		setAuthenticationManager(authenticationManager);

		
	}

	
	//retorna o usuario ao processar a autenticacao
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
		//está pegando o token para validar
		Usuario user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
		
		//Retorna o usuario login e senha e acessos		
		return getAuthenticationManager()
				.authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getSenha()));
	}
	
	
	//gera o token pra autenticar e retornar para o navegador
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName());
	
	}

}
