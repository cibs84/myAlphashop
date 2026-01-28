package com.alphashop.user_management_service.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "users")
@Data
public class User {

	@Id
	private String id;

	@Indexed(unique = true)
	private String userId;

	private String password;

	private boolean active;

	private List<String> roles;
}
