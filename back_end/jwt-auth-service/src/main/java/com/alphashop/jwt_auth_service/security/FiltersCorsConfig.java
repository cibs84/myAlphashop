package com.alphashop.jwt_auth_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FiltersCorsConfig implements WebMvcConfigurer {
    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                    .addMapping("/**")
                    .allowedOrigins("http://localhost:4200", "http://localhost:4300")
                    .allowedMethods("PUT","DELETE","GET","POST","OPTIONS","HEAD","PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true) // Permette i cookie
                    .maxAge(3600);
            }
        };
    }
}
