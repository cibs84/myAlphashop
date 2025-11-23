package com.alphashop.jwt_auth_service.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("security")
@Data
public class JwtConfig
{
	private String header;
	private String prefix;
	private int expToken;
	private int expRefreshToken;
	private String secret;
}
