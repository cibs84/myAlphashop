package com.alphashop.jwt_auth_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alphashop.jwt_auth_service.dtos.JwtTokenRequest;
import com.alphashop.jwt_auth_service.dtos.UserInfoResponse;
import com.alphashop.jwt_auth_service.exceptions.JwtAuthenticationException;
import com.alphashop.jwt_auth_service.security.JwtTokenUtil;
import com.alphashop.jwt_auth_service.security.constants.PublicRoutes;

import lombok.extern.java.Log;

@RestController
@Log
public class JwtAuthenticationRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier("CustomUserDetailsService")
    private UserDetailsService userDetailsService;

    @PostMapping(PublicRoutes.LOGIN) // /api/authentication/login
    public ResponseEntity<Void> login(
            @RequestBody JwtTokenRequest authenticationRequest, HttpServletResponse response) {

        log.info("Authentication and Tokens Generation");

        Authentication authentication = authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        String token = jwtTokenUtil.generateToken(claims, userDetails, false);
        String refreshToken = jwtTokenUtil.generateToken(claims, userDetails, true);

        // Imposta i cookie HttpOnly
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false) // Mettere a false se in ambiente di sviluppo senza HTTPS
                .sameSite("Lax")
                .path("/")
                .maxAge(30 * 60) // 30 min => (min * sec)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(PublicRoutes.REFRESH)
                .maxAge(15 * 24 * 60 * 60) // 15 giorni => (day * hour * min * sec)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping(PublicRoutes.REFRESH) // /api/authentication/refresh
    public ResponseEntity<Void> refreshAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

        if (refreshToken == null || jwtTokenUtil.isTokenExpired(refreshToken)) {
            throw new JwtAuthenticationException("Expired or invalid refresh token", null);
        }

        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Map<String, Object> claims = new HashMap<>();
        String newToken = jwtTokenUtil.generateToken(claims, userDetails, false);
        String newRefreshToken = jwtTokenUtil.generateToken(claims, userDetails, true);

        // Aggiorna i cookie
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", newToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(30 * 60) // 30 min => (min * sec)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(PublicRoutes.REFRESH)
                .maxAge(15 * 24 * 60 * 60) // 15 giorni => (day * hour * min * sec)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok().build();
    }

//    @GetMapping("api/authentication/me")
//    public ResponseEntity<UserInfoResponse> getUserInfoFromToken(@CookieValue(value = "jwt", required = false) String token) {
//    	List<String> roles = jwtTokenUtil.getRolesFromToken(token);
//    	String username = jwtTokenUtil.getUsernameFromToken(token);
//        
//        return ResponseEntity.ok(new UserInfoResponse(username, roles));
//    }
    
    @GetMapping("api/authentication/me")
    public ResponseEntity<UserInfoResponse> getUserInfo(Authentication authentication){
    	if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
    	
    	String username = authentication.getName();
    	List<String> roles = authentication.getAuthorities()
    			.stream()
    			.map(granted -> granted.getAuthority())
    			.collect(Collectors.toList());
    	
    	return ResponseEntity.ok(new UserInfoResponse(username, roles));
    }

    @PostMapping(PublicRoutes.LOGOUT)
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Invalida i cookie rimuovendoli
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0) // Elimina subito il cookie
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(PublicRoutes.REFRESH)
                .maxAge(0) // Elimina subito il cookie
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok().build();
    }

    private Authentication authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            log.warning("DISABLED USER");
            throw new JwtAuthenticationException("DISABLED USER", e);
        } catch (BadCredentialsException e) {
            log.warning("BAD CREDENTIALS");
            throw new JwtAuthenticationException("BAD CREDENTIALS", e);
        }
    }
}
