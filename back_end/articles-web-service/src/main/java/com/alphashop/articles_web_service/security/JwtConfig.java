package com.alphashop.articles_web_service.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("security")
@Data
public class JwtConfig
{
	private String header;
	private String secret;
}
