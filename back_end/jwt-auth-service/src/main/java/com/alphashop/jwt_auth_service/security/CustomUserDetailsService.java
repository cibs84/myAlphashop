package com.alphashop.jwt_auth_service.security;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.java.Log;

@Log
@Service("CustomUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserServiceConfig userServiceConfig;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.warning("Loads loadUserByUsername() in CustomUserDetailsService");
		String errMsg = "";
		
		if (username == null || username.length() < 5) {
			errMsg = "Insert a valid Username!";
			log.warning(errMsg);
			throw new UsernameNotFoundException(errMsg);
		}
		
		User user = httpGetUserByUserId(username);
		
		if (user == null) {
			errMsg = String.format("User %s not found!", username);
			
			log.warning(errMsg);
			
			throw new UsernameNotFoundException(errMsg);
		}
		
		UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(user.getUserId());
		userBuilder.disabled(!user.getActive()).password(user.getPassword());
		
		String[] roles = user.getRoles()
				.stream().map(role -> "ROLE_" + role).toArray(String[]::new);
		
		userBuilder.authorities(roles);
		
		UserDetails userDetails = userBuilder.build();
		
		return userDetails;
	}

	private User httpGetUserByUserId(String userId) {
		
		URI url = null;
		
		try {
			url = new URI(userServiceConfig.getServerUrl() + userId);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(
				new BasicAuthenticationInterceptor(userServiceConfig.getUsername(), userServiceConfig.getPassword()));
		
		User user = null;
		
		try {
			ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);
			
			if (response.getStatusCode().is2xxSuccessful()) {
				user = response.getBody();
			}
		} catch (HttpClientErrorException e) {
            String ErrMsg = String.format("HTTP Error: %s", e.getStatusCode());
            log.warning(ErrMsg);
        } catch (ResourceAccessException e) {
            String ErrMsg = String.format("Unable to access the resource: %s", e.getMessage());
            log.warning(ErrMsg);
        } catch (Exception e) {
            String ErrMsg = "Connection to authentication service failed or service absent!";
            log.warning(ErrMsg);
        }
		
		return user;
	}
}
