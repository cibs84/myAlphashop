package com.alphashop.articles_web_service.security;

import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

@Log
public class AuthEntryPoint extends BasicAuthenticationEntryPoint {
	
	private final static String REALM = "REAME";
	
	@Override
	@SneakyThrows
	public void commence(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final AuthenticationException authException) {
		
		String errMsg = "Unauthorized Access";
		
		log.warning("Security Error: " + authException.getMessage());
		
		// Authentication failed, send error response
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setHeader("WWW.Authenticate", "Basic realm=" + getRealmName() + "");
		
		PrintWriter writer = response.getWriter();
		writer.println(errMsg);
	}
	
	@Override
	public void afterPropertiesSet()
	{
		setRealmName(REALM);
		super.afterPropertiesSet();
	}
}