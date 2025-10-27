package es.us.dp1.lx_xy_24_25.your_game_name.configuration;

import static org.springframework.security.config.Customizer.withDefaults;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.AuthEntryPointJwt;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.AuthTokenFilter;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Autowired
	DataSource dataSource;

	private static final String ADMIN = "ADMIN";
	private static final String PLAYER = "PLAYER";
	

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		http
			.cors(withDefaults())		
			.csrf(AbstractHttpConfigurer::disable)		
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))			
			.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()))
			.exceptionHandling((exepciontHandling) -> exepciontHandling.authenticationEntryPoint(unauthorizedHandler))			
			.authorizeHttpRequests(authorizeRequests ->	authorizeRequests
			.requestMatchers("/",
                    "/index.html",
                    "/assets/**",
                    "/*.ico",
                    "/*.png",
                    "/*.jpg",
                    "/*.jpeg",
                    "/*.gif",
                    "/*.svg",
                    "/error",
                    "/default-avatar.png",
                    "/napoleonColores.png"
                ).permitAll()
			.requestMatchers("/api/v1/developers").permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/resources/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/webjars/**")).permitAll() 
			.requestMatchers(AntPathRequestMatcher.antMatcher("/static/**")).permitAll() 
			.requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-resources/**")).permitAll()						
			.requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
			.requestMatchers(HttpMethod.GET,"/api/v1/gamesessions").authenticated()
			.requestMatchers(HttpMethod.POST,"/api/v1/gamesessions").hasAuthority(PLAYER)	
			.requestMatchers(AntPathRequestMatcher.antMatcher("/oups")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/auth/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui.html")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**")).permitAll()												
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/developers")).permitAll()												
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/plan")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/users/**")).authenticated()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/users/**")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/users/makeOnline")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/users/makeOffline")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/pieces/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/fights/**")).permitAll()

			.requestMatchers(HttpMethod.GET,"/api/v1/cards").permitAll()
			.requestMatchers(HttpMethod.POST,"/api/v1/cards").hasAuthority(ADMIN)	
			.requestMatchers(HttpMethod.PUT,"/api/v1/cards").hasAuthority(ADMIN)
			.requestMatchers(HttpMethod.GET,"/api/v1/cards/card").permitAll()	
			.requestMatchers(HttpMethod.POST,"/api/v1/decks").permitAll()	
			.requestMatchers(HttpMethod.GET,"/api/v1/decks").permitAll()	
			.requestMatchers(HttpMethod.PUT,"/api/v1/decks").permitAll()
			.requestMatchers(HttpMethod.GET,"/api/v1/decks/deck").permitAll()
			.requestMatchers(HttpMethod.GET,"/api/v1/decks/takeOneCard").hasAuthority(PLAYER)
			.requestMatchers(HttpMethod.GET,"/api/v1/decks/shuffle").permitAll()
			.requestMatchers(HttpMethod.GET,"/api/v1/decks/reshuffle").permitAll()
			.requestMatchers(HttpMethod.POST,"/api/v1/players").permitAll()
			.requestMatchers(HttpMethod.PUT,"/api/v1/players/player").permitAll()
			.requestMatchers(HttpMethod.GET,"/api/v1/players/player").permitAll()
			.requestMatchers(HttpMethod.GET,"/api/v1/players").permitAll()
			.requestMatchers(HttpMethod.DELETE,"/api/v1/players/player").permitAll()
			.requestMatchers(HttpMethod.PUT,"/api/v1/players/receiveCard").permitAll()
			.requestMatchers(HttpMethod.PUT,"/api/v1/players/putCardInBag").permitAll()
			.requestMatchers(HttpMethod.PUT,"/api/v1/players/discardCard").permitAll()
			.requestMatchers(HttpMethod.PUT,"/api/v1/decks/reshuffle").permitAll()
			.requestMatchers(HttpMethod.PUT,"/api/v1/decks/discard").permitAll()
			.requestMatchers(HttpMethod.PUT,"/api/v1/decks/poblate").permitAll()
			.requestMatchers(HttpMethod.GET, "/api/v1/genres/**\", \"/api/v1/platforms/**\", \"/api/v1/sagas/**").hasAnyAuthority(ADMIN, PLAYER)
			.requestMatchers(HttpMethod.POST, "/api/v1/genres/**", "/api/v1/platforms/**", "/api/v1/sagas/**").hasAuthority(ADMIN)
			.requestMatchers(HttpMethod.PUT, "/api/v1/genres/**", "/api/v1/platforms/**", "/api/v1/sagas/**").hasAuthority(ADMIN)
			.requestMatchers(HttpMethod.DELETE, "/api/v1/genres/**", "/api/v1/platforms/**", "/api/v1/sagas/**").hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/friendship/**")).hasAuthority(PLAYER)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/chat/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/messages")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/squares/**")).permitAll()                    
			.requestMatchers(AntPathRequestMatcher.antMatcher("/socket-chat/**")).permitAll()                    
			.anyRequest().authenticated())					
			
			.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);		
		return http.build();
	}

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}	


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	
}
