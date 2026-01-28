package com.alphashop.jwt_auth_service.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alphashop.jwt_auth_service.security.constants.PublicRoutes;


@Configuration
@EnableWebSecurity
public class JWTWebSecurityConfig {

    @Autowired
    @Qualifier("CustomUserDetailsService")
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter;

    // 2. Il PasswordEncoder rimane un Bean
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 3. Nuovo modo per esportare l'AuthenticationManager
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 4. Configurazione principale tramite SecurityFilterChain
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth // Nota: authorizeHttpRequests invece di authorizeRequests
                .requestMatchers(HttpMethod.POST, PublicRoutes.LOGIN).permitAll() // requestMatchers invece di antMatchers
                .requestMatchers(HttpMethod.POST, PublicRoutes.REFRESH).permitAll()
                .requestMatchers(HttpMethod.POST, PublicRoutes.LOGOUT).permitAll()
                .requestMatchers(HttpMethod.GET, PublicRoutes.PUBLIC_ROUTES).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Sostituisce WebSecurity.ignoring()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
            .addFilterBefore(jwtCookieAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}