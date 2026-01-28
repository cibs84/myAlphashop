package com.alphashop.user_management_service.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alphashop.user_management_service.common.PaginatedResponseList;
import com.alphashop.user_management_service.dtos.UserDto;
import com.alphashop.user_management_service.exceptions.BindingException;
import com.alphashop.user_management_service.exceptions.ItemAlreadyExistsException;
import com.alphashop.user_management_service.exceptions.NotFoundException;
import com.alphashop.user_management_service.models.User;
import com.alphashop.user_management_service.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Valid;
import lombok.extern.java.Log;

@Log
@Controller
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder pwdEncoder;
	
	
	@GetMapping("/find")
	public ResponseEntity<PaginatedResponseList<User, UserDto>> findAll(
			@RequestParam(name = "currentPage", required = false) Optional<Integer> currentPage,
			@RequestParam(name = "pageSize", required = false) Optional<Integer> pageSize) throws NotFoundException {
		 
		PaginatedResponseList<User, UserDto> userPagList = userService.getAll(currentPage, pageSize);

		log.info("******** Get all Users ********");
		
		return new ResponseEntity<PaginatedResponseList<User, UserDto>>(userPagList, HttpStatus.OK);
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<UserDto> findUserId(@PathVariable(required = false) String userId) throws NotFoundException {
		
		if (userId == null) {
			String errMsg = "Insert a valid userId";
			log.warning(errMsg);
			
			throw new NotFoundException(errMsg);
		}
		
		UserDto userDto = userService.getByUserId(userId);
		
		if (userDto == null) {
			String errMsg = "The User with userId '%s' was not found".formatted(userId);
			log.warning(errMsg);

			throw new NotFoundException(errMsg);
		}
		
		log.info("******** Get user with userId %s ********".formatted(userId));

		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) throws ItemAlreadyExistsException, BindingException, NotFoundException {
		
		// Check if the user to be created already exists
		UserDto user = userService.getByUserId(userDto.getUserId());
		if (user != null) {
			String errMsg = "User '%s' already exists".formatted(userDto.getUserId());
			log.warning(errMsg);
			
			throw new ItemAlreadyExistsException(errMsg);
		}
				
		UserDto newUserDto = userService.create(userDto);
		
		log.info("******** User with userId '%s' was created ********".formatted(newUserDto.getUserId()));

		return new ResponseEntity<UserDto>(newUserDto, HttpStatus.CREATED);
	}
	
	@PutMapping("/{userId}")
	public ResponseEntity<UserDto> update(@Valid @RequestBody UserDto userDto) throws BindingException, NotFoundException{
		
		UserDto userDb = userService.getByUserId(userDto.getUserId());
		if ( userDb == null) {
			String errMsg = "User '%s' doesn't exists".formatted(userDto.getUserId());
			log.warning(errMsg);
			
			throw new NotFoundException(errMsg);
		}
		
		userDto.setId(userDb.getId());
		userDto.setPassword(pwdEncoder.encode(userDb.getPassword()));
		userDto = userService.create(userDto);
		
		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
	}
	
	@DeleteMapping("/{userId}")
	public ResponseEntity<ObjectNode> delete(@PathVariable String userId) throws NotFoundException {
		
		UserDto userDto = userService.getByUserId(userId);
		
		if (userDto == null) {
			String errMsg = String.format("User to be deleted '%s' was not found", userId);
			log.warning(errMsg);
			
			throw new NotFoundException(errMsg);
		}
		
		userService.delete(userDto);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode responseNode = mapper.createObjectNode();
		
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", String.format("Deleting user '%s' performed successfully", userId));
		
		return new ResponseEntity<ObjectNode>(responseNode, HttpStatus.OK);
	}
}


