package com.alphashop.jwt_auth_service.security;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alphashop.jwt_auth_service.security.constants.PublicRoutes;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    public JwtCookieAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
    								@NonNull HttpServletResponse response, 
    								@NonNull FilterChain filterChain) throws ServletException, IOException {
        
    	String path = request.getRequestURI();

        // Escludi le rotte pubbliche e le richieste preflight
        if (this.isPublicPath(path) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
	        filterChain.doFilter(request, response);
	        return;
        }
    	
        String token = getTokenFromCookies(request);

        // Se non c’è token o è scaduto, lascia che Security gestisca il 401
        if (token == null || token.isBlank() || jwtTokenUtil.isTokenExpired(token)) {
        	filterChain.doFilter(request, response);
            return;
        }
        
        try {
        	String username = jwtTokenUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception exception) {
        	// Se il token non è valido, puliamo il contesto e passiamo avanti
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromCookies(HttpServletRequest request) {
    	if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
    
    private boolean isPublicPath(String path) {
    	return path.contains(PublicRoutes.LOGIN) ||
	            path.contains(PublicRoutes.REFRESH) ||
	            path.contains(PublicRoutes.LOGOUT) ||
	            path.contains(PublicRoutes.PUBLIC_ROUTES);
    }
}

