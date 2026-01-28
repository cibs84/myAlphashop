package com.alphashop.jwt_auth_service.security;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.java.Log;

@Component
@Log
public class JwtTokenUtil implements Serializable {

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_CREATED = "iat";
	
	private static final long serialVersionUID = -3301605591108950415L;

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

	private SecretKey getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
	
	private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build() 
                    .parseSignedClaims(token)    
                    .getPayload();    
        } catch (Exception ex) {
            log.warning("Errore nel parsing del token: " + ex.getMessage());
            return null;
        }
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
        final Date createdDate = new Date(); 
        final Date expirationDate = isRefreshToken ? calculateExpirationRefreshTokenDate(createdDate) : calculateExpirationTokenDate(createdDate);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .claim("authorities", userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuedAt(createdDate)
                .expiration(expirationDate)
                .signWith(getSigningKey())
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
