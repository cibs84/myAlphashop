package com.alphashop.articles_web_service.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class JWTWebSecurityConfig {

	@Autowired
	private JwtTokenAuthorizationOncePerRequestFilter jwtAuthenticationTokenFilter;
	
	@Bean
	static PasswordEncoder passwordEncoderBean() {
		return new BCryptPasswordEncoder();
	}


	private static final String[] USER_MATCHER = { "/api/articles/find/**",
												   "/api/categories/find/**",
												   "/api/vat/find/**"};

	private static final String[] ADMIN_MATCHER = { "/api/articles/create/**",
												    "/api/articles/update/**",
												    "/api/articles/delete/**"};
	

	@Bean
    UserDetailsService userDetailsService() {
        return username -> null; // Disabilita l'autenticazione basata su UserDetailsService
    }
	
	@Bean
	@SneakyThrows
	SecurityFilterChain securityFilterChain(HttpSecurity http) {
	    http
	        .csrf(csrf -> csrf.disable())
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
	        .authorizeHttpRequests(authz -> 
	        {
	            authz
	                .requestMatchers(ADMIN_MATCHER).hasRole("ADMIN")
	                .requestMatchers(USER_MATCHER).hasRole("USER")
	                .anyRequest().authenticated();
	        });

	    return http.build();
	}

	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		  List<String> allowedHeaders = new ArrayList<String>();
		  allowedHeaders.add("Authorization");
		  allowedHeaders.add("Content-Type");
		  allowedHeaders.add("Accept");
		  allowedHeaders.add("x-requested-with");
		  allowedHeaders.add("Cache-Control");
	
		  
	      CorsConfiguration configuration = new CorsConfiguration();
	      configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200",
														"http://localhost:4300"));
	      configuration.setAllowedMethods(Arrays.asList("GET","POST","OPTIONS","DELETE","PUT"));
	      configuration.setMaxAge((long) 3600);
	      configuration.setAllowedHeaders(allowedHeaders);
	      configuration.setAllowCredentials(true);
	      
	      
	      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	      source.registerCorsConfiguration("/**", configuration);
	      
	      return source;
	 }
}
