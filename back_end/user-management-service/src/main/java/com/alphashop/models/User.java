package com.alphashop.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Document(collection = "users")
@Data
public class User {
	
	@Id
	private String id;
	
	@Indexed(unique = true)
	@Size(min = 5, max = 80, message = "{Size.User.userId.Validation")
	@NotBlank(message = "{NotBlank.User.userId.Validation")
	private String userId;
	
	@Size(min = 8, max = 80, message = "{Size.User.password.Validation}")
	private String password;
	
	private Boolean active = true;
	
	@NotNull(message = "{NotNull.User.roles.Validation}")
	private List<String> roles;
	
}
