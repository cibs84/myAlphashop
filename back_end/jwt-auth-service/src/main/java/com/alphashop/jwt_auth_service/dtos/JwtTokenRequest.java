package com.alphashop.jwt_auth_service.dtos;

import java.io.Serializable;

import lombok.Data;

@Data
public class JwtTokenRequest implements Serializable 
{

	private static final long serialVersionUID = -5616176897013108345L;

	private String username;
	private String password;

}
