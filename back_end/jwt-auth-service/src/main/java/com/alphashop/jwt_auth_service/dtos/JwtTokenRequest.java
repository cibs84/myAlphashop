package com.alphashop.jwt_auth_service.dtos;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtTokenRequest implements Serializable 
{

	private static final long serialVersionUID = -5616176897013108345L;

	@NotBlank(message = "{validation.required}")
	private String username;
	
	@NotBlank(message = "{validation.required}")
	@Length(min = 6, message = "{validation.minLength}")
	private String password;

}
