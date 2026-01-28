package com.alphashop.articles_web_service.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alphashop.articles_web_service.exceptions.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;

@Component
@Log
public class JwtTokenAuthorizationOncePerRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${security.header}")
    private String tokenHeader = "";

    public JwtTokenAuthorizationOncePerRequestFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    // Routes that the filter should ignore
    private static final List<String> SKIP_FILTER_URLS = Arrays.asList(
            "/api/public",
            "/v3/api-docs",
            "/swagger-ui",
            "/favicon.ico"
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        
        // If the path starts with one of those in the list, the filter is not performed.
        return SKIP_FILTER_URLS.stream().anyMatch(path::startsWith);
    }
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
    								@NonNull HttpServletResponse response, 
    								@NonNull FilterChain chain)
            throws ServletException, IOException {

        log.info(String.format("Authentication Request For '%s'", request.getRequestURL()));

        String jwtToken = getTokenFromCookies(request);

        if (jwtToken != null) {
            try {
                jwtTokenUtil.validateToken(jwtToken);
	            Claims claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
	            String username = claims.getSubject();
	            List<String> roles = claims.get("authorities", List.class);
	
	            log.info("User: " + username + " - Roles: " + roles);
	
	            List<SimpleGrantedAuthority> authorities = roles.stream()
	                    .map(role -> new SimpleGrantedAuthority(role))
	                    .collect(Collectors.toList());
	            
	            // Create an authentication object that conytains the extracted roles from JWT payload
	            // and set it in the SecurityContext
	            UsernamePasswordAuthenticationToken authentication =
	                    new UsernamePasswordAuthenticationToken(username, null, authorities);
	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	
	            SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                handleJwtException(response, e);
                return;
            }
        } else {
        	handleJwtException(response, null);
        	return;
        }

        // Filters chain continues..
        chain.doFilter(request, response);
    }
    
    private String getTokenFromCookies(HttpServletRequest request) {
    	if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) { // Assicurati che il nome del cookie sia corretto
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    
    // Handle all JWT exceptions thrown by the filter
    private void handleJwtException(HttpServletResponse response, Throwable e) throws IOException {
    	log.warning("Unauthorized Access");
    	
    	String code = "UNAUTHORIZED";

        if (e instanceof ExpiredJwtException) {
            code = "TOKEN_EXPIRED";
        } else if (
            e instanceof SignatureException ||
            e instanceof MalformedJwtException ||
            e instanceof UnsupportedJwtException
        ) {
            code = "INVALID_TOKEN";
        } else if (e == null) {
            code = "UNAUTHORIZED"; // token mancante
        }

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setDate(new Date());
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setCode(code);

        // Write the response error
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
    }
}
