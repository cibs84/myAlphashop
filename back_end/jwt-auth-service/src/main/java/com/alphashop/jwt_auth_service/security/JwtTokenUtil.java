package com.alphashop.jwt_auth_service.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import lombok.extern.java.Log;

@Component
@Log
public class JwtTokenUtil implements Serializable {

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_CREATED = "iat";
	private static final long serialVersionUID = -3301605591108950415L;
	private Clock clock = DefaultClock.INSTANCE;

	@Autowired
	private JwtConfig jwtConfig;


	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}
	
	public List<String> getRolesFromToken(String token) {
		List<String> resp = getClaimFromToken(token, claims -> {
	        Object authorities = claims.get("authorities");
	        if (authorities instanceof List) {
	            return ((List<?>) authorities).stream()
	                                          .map(Object::toString)
	                                          .collect(Collectors.toList());
	        }
	        return new ArrayList<>();
	    });
		
	    return resp;
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		
		if (claims != null) {
			log.info(String.format("Token Issue:  %s", claims.getIssuedAt().toString()));
			log.info(String.format("Token Expiration:  %s", claims.getExpiration().toString()));
			
			return claimsResolver.apply(claims);
		}
		
		return null;
	}

	private Claims getAllClaimsFromToken(String token) {
		Claims retVal = null;
		
		try {
			retVal = Jwts.parser()
					.setSigningKey(jwtConfig.getSecret().getBytes())
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception ex) {
			log.warning(ex.getMessage());
		}
		
		return retVal;
	}

	public Boolean isTokenExpired(String token) {
	
		final Date expiration = getExpirationDateFromToken(token);
		
		boolean isExpired = (expiration == null) ? true : false;
		
		if (isExpired) {
			log.warning("Expired or Invalid Token!");
		} else {
			log.info("Token still valid!");
		}
		
		return isExpired;
	}

	public String generateToken(Map<String, Object> claims, UserDetails userDetails, boolean isRefreshToken) {
		final Date createdDate = clock.now();
		final Date expirationDate = isRefreshToken ? calculateExpirationRefreshTokenDate(createdDate) : calculateExpirationTokenDate(createdDate);
		
		final String secret = jwtConfig.getSecret();

		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userDetails.getUsername())
				.claim("authorities", userDetails.getAuthorities()
						.stream()
							.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(createdDate)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, secret.getBytes())
				.compact();
	}

	public Boolean canTokenBeRefreshed(String token) {
		return (isTokenExpired(token));
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private Date calculateExpirationTokenDate(Date createdDate) {
		return new Date(createdDate.getTime() + jwtConfig.getExpToken() * 1000);
	}
	
	private Date calculateExpirationRefreshTokenDate(Date createdDate) {
		return new Date(createdDate.getTime() + jwtConfig.getExpRefreshToken() * 1000);
	}
}
