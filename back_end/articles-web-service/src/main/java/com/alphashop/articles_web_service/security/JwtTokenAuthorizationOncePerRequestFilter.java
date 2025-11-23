package com.alphashop.articles_web_service.security;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
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
    	
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setDate(new Date());
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());

        // Map to associate exceptions with custom messages
        Map<Class<? extends Throwable>, String> errorMessages = Map.of(
                ExpiredJwtException.class, "Token expired.",
                SignatureException.class, "Invalid token signature.",
                MalformedJwtException.class, "Invalid token format.",
                UnsupportedJwtException.class, "Unsupported token type."
        );

        if (e == null) {
            errorResponse.setMessage("Authentication error: No JWT token found.");
        } else {
            errorResponse.setMessage(errorMessages.getOrDefault(e.getClass(), e.getMessage()));
        }

        // Write the response error
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
