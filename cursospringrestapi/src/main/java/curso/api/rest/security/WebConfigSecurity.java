package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import curso.api.rest.service.ImplementacaoUserDetailsService;

//mapeia urls, endereço , autoriza ou bloqueia acessos a urls
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private  ImplementacaoUserDetailsService implementacaoUserDetailsService;

	
	//configura as solicitacoes de acesso por http
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		//ativvando a protecao contra usuarios que nao estao validados por token
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		
		//ativa a permissao para a pagina innicial dio sistema
		.disable().authorizeRequests().antMatchers("/").permitAll()
		
		.antMatchers("/index").permitAll()
		//url de logout
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		
		//mapeia urlde logout e invalida usuario
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		//filtra requisicoes de login para autenticacao
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
				UsernamePasswordAuthenticationFilter.class)
		
		//filtra demais requisicoes para verificar a presença do token jwwt
		.addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		//service que ira consultar o usuario no BD
		auth.userDetailsService(implementacaoUserDetailsService)
		//padrao de codificacao de senha do user
		.passwordEncoder(new BCryptPasswordEncoder());
	
	}
	
	
}
