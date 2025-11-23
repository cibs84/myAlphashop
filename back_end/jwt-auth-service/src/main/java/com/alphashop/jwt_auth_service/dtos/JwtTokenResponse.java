package com.alphashop.jwt_auth_service.dtos;

import java.io.Serializable;

import lombok.Data;

@Data
public class JwtTokenResponse implements Serializable {

	private static final long serialVersionUID = 8317676219297719109L;

	private final String token;
}