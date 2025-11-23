package com.alphashop.jwt_auth_service.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alphashop.jwt_auth_service.security.constants.PublicRoutes;

import lombok.SneakyThrows;


@Configuration
@EnableWebSecurity
public class JWTWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("CustomUserDetailsService")
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter;
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoderBean());
    }

    @Bean
    static PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    @SneakyThrows
    protected void configure(HttpSecurity httpSecurity) {
        httpSecurity
                .cors(withDefaults()) // usa la configurazione CORS globale definita nel FiltersCorsConfig
                .csrf(csrf -> csrf.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(requests -> requests
                        .antMatchers(HttpMethod.POST, PublicRoutes.LOGIN).permitAll()
                        .antMatchers(HttpMethod.POST, PublicRoutes.REFRESH).permitAll()
                        .antMatchers(HttpMethod.POST, PublicRoutes.LOGOUT).permitAll()
                        .antMatchers(HttpMethod.GET, PublicRoutes.PUBLIC_ROUTES).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(handling -> handling.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .addFilterBefore(jwtCookieAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
    
    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**");
    }
}

