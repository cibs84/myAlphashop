package com.alphashop.articles_web_service.security;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
	private String id;
	private String userId;
	private String password;
	private Boolean active = true;
	private List<String> roles;
}
