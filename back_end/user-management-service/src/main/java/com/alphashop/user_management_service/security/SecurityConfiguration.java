package com.alphashop.user_management_service.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.SneakyThrows;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
	
	private static final String REALM = "REAME";
	
	@Bean
	BCryptPasswordEncoder bcryptEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@SneakyThrows
	SecurityFilterChain securityFilterChain(HttpSecurity http) {
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.httpBasic(e -> e.realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint()))
			.authorizeHttpRequests(authz -> {
				
				authz
				// Regole per USER (Scrittura solo ADMIN, Lettura USER/ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("USER", "ADMIN")
				
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
			    "http://127.0.0.1:8084"
	    ));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT"));
		configuration.setMaxAge((long) 3600);
		configuration.setAllowedHeaders(allowedHeaders);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		
		return source;
	}
	
	@Bean
	AuthEntryPoint getBasicAuthEntryPoint() {
		return new AuthEntryPoint();
	}
}
