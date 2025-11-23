package com.alphashop.jwt_auth_service.exceptions;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException 
{
	 
	private static final long serialVersionUID = 5978387939943664344L;

	public JwtAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public JwtAuthenticationException(String message) {
		super(message);
	}
}
