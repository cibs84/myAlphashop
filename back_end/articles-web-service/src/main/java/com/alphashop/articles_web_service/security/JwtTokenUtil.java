package com.alphashop.articles_web_service.security;

import java.io.Serializable;
import java.time.Clock;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.java.Log;

@Log
@Component
public class JwtTokenUtil implements Serializable {

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_CREATED = "iat";
	private static final long serialVersionUID = -3301605591108950415L;
	private Clock clock = Clock.systemUTC();

	@Autowired
	private JwtConfig jwtConfig;


	public String getUsernameFromToken(String token) throws Exception 
	{
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getIssuedAtDateFromToken(String token) throws Exception 
	{
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public Date getExpirationDateFromToken(String token) throws Exception  
	{
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws Exception {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	protected Claims getAllClaimsFromToken(String token) {
		return Jwts.parser()
				  .verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
				  .build()
				  .parseSignedClaims(token).getPayload();
	}

	public Boolean isTokenExpired(String token) throws Exception {
		final Date expiration = getExpirationDateFromToken(token);
		if (expiration.before(Date.from(clock.instant()))) {
			return true;
		}
		return false;
	}
	
	public Boolean validateToken(String token) {
		Jwts.parser()
			.verifyWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
			.build()
			.parse(token);
		
		return true;
	}

}
