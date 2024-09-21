package com.alphashop.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.SneakyThrows;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
	
	private static final String REALM = "REAME";
	
	// AUTHENICATION
	@Bean
	UserDetailsService userDetailsService() {
		
		UserDetails user = User
				.withUsername("Mario")
				.password(new BCryptPasswordEncoder().encode("pass123"))
				.roles("USER")
				.build();
		
		UserDetails admin = User
				.withUsername("Admin")
				.password(new BCryptPasswordEncoder().encode("admin"))
				.roles("USER", "ADMIN")
				.build();

		return new InMemoryUserDetailsManager(user, admin);
	}
	
	@Bean
	BCryptPasswordEncoder bcryptEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	// AUTHORIZATION
	private static final String[] USER_MATCHER = { "/api/articles",
												   "/api/articles/find/**",
												   "/api/categories/find/**",
												   "/api/vat/find/**"
	};
	private static final String[] ADMIN_MATCHER = { "/api/articles/create/**",
												   "/api/articles/update/**",
												   "/api/articles/delete/**"
	};
	
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
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200/"));
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
