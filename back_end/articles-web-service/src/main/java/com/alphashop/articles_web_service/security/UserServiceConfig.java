package com.alphashop.articles_web_service.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties("user-service")
@Getter
@Setter
public class UserServiceConfig {
	
	private String serverUrl;
	private String username;
	private String password;
	
}
