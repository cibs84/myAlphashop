package com.alphashop.user_management_service.dtos;

import java.util.ArrayList;
import java.util.List;

import com.alphashop.user_management_service.models.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto implements Cloneable {
	
	private String id;
	
	@Size(min = 5, max = 80, message = "{Size.UserDto.userId.Validation}")
	@NotBlank(message = "{NotBlank.User.userId.Validation}")
	private String userId;
	
	@Size(min = 8, max = 80, message = "{Size.UserDto.password.Validation}")
	private String password;
	
	private Boolean active = true;
	
	@NotNull(message = "{NotNull.UserDto.roles.Validation}")
	private List<String> roles;
	
	@Override
	// Performs deep copy of this UserDto instance. Returns a UserDto clone deep copied
	public UserDto clone() throws CloneNotSupportedException {

		UserDto userDto = new UserDto();
		userDto.setUserId(getUserId());
		userDto.setPassword(getPassword());
		userDto.setActive(getActive());
		userDto.setId(getId());

		// SET ROLES
		ArrayList<String> list = new ArrayList<>();
		getRoles().stream().map(role -> list.add(role));
		userDto.setRoles(list);

		return userDto;
	}
}
