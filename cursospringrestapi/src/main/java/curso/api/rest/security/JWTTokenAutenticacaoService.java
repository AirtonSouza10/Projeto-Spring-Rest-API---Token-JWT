package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	//tempo de validade do token
	private static final long EXPIRATION_TIME = 172800000;
	
	//uma senha unica para compor a autenticacao
	private static final String SECRET = "TaTu9b37@123";
	
	//padrao de token
	private static final String TOKEN_PREFIX = "Bearer";
	
	
	private static final String HEADER_STRING = "Authorization";
	
	
	//Gerando token de autenticacao e adiciona ao cabeçalho e resposta http
	public void addAuthentication(HttpServletResponse response, String username) throws IOException{
		
		//montagem do token
		String JWT = Jwts.builder() // chama o gerador de token
				.setSubject(username) //add o user
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //tempo de expiração
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); //compactacao e algoritmode geracao de senha
		
		//junta o token com o prefixo
		String token = TOKEN_PREFIX + " " +JWT; //Bearer 23-5ewtw3079ew7t3
		
		//adiciona no cabeçalho http
		response.addHeader(HEADER_STRING, token); //Authorization: 
		
		//atualizar token vencido
		ApplicationContextLoad.getApplicationContext()
		.getBean(UsuarioRepository.class).atualizaTokenUser(JWT, username);
		
		//liberandop resposta pra portas diferentes que usam a API
		liberacaoCors(response);
		
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
		
		
	}
	
	
	//retorna o usuario validado com token ou caso não seja valido retorna null
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
		
		//pega o token enviado no cabecalho http
		String token = request.getHeader(HEADER_STRING);
		
		try {
			
		
		if (token != null) {
			
			String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
			
			//faz a validacao do token do usuario na requisicao
			String user = Jwts.parser().setSigningKey(SECRET)  //vem com prefixo
					.parseClaimsJws(tokenLimpo) //retira-se o berare
					.getBody().getSubject();  //retirou o prefixo bearer e retorna só o user
			
			if(user != null) {
				
				Usuario usuario = ApplicationContextLoad.getApplicationContext()
						.getBean(UsuarioRepository.class).findUserByLogin(user);
						
				//retorna o usuario logado
				if(usuario != null) {
					
					if(tokenLimpo.equalsIgnoreCase(usuario.getToken())) {					
					
						return new UsernamePasswordAuthenticationToken(
								usuario.getLogin(), 
								usuario.getSenha(), 
								usuario.getAuthorities());
					}
					
				}
				
			}
			
		}//fim da condicao token
		
		
		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			try {
				response.getOutputStream().print("Seu TOKENN está expirado, faça o login ou informe um novo token para a autenticação");
			} catch (IOException e1) {			}
		}
		
		liberacaoCors(response);
		
		return null; // nao autoria\ado
	}


	private void liberacaoCors(HttpServletResponse response) {
		if(response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");			
		}
	}	
		
		
	
	
	
	
}
