package com.alphashop.jwt_auth_service.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {
	String username;
	List<String> roles;
}
