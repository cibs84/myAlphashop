package com.alphashop.articles_web_service.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                // Regole per ARTICLE (Scrittura solo ADMIN, Lettura USER/ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/articles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/articles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/articles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/articles/**").hasAnyRole("USER", "ADMIN")
                
                // Regole per CATEGORY (Scrittura solo ADMIN, Lettura USER/ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/categories/**").hasAnyRole("USER", "ADMIN")
                
                // Regole per VAT (Scrittura solo ADMIN, Lettura USER/ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/vat/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/vat/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/vat/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/vat/**").hasAnyRole("USER", "ADMIN")

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
	      configuration.setAllowedOrigins(Arrays.asList(
					"http://localhost:4200", // Frontend Angular dev
				    "http://localhost:4300", 
				    "http://localhost:8084", // Porta esposta da Nginx container
				    "http://127.0.0.1:8084"	// Opzionale, per sicurezza
		  ));
	      configuration.setAllowedMethods(Arrays.asList("GET","POST","OPTIONS","DELETE","PUT"));
	      configuration.setMaxAge((long) 3600);
	      configuration.setAllowedHeaders(allowedHeaders);
	      configuration.setAllowCredentials(true);
	      
	      
	      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	      source.registerCorsConfiguration("/**", configuration);
	      
	      return source;
	 }
}
