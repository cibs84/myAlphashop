package com.alphashop.user_management_service.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

import jakarta.validation.Valid;
import lombok.extern.java.Log;

@Log
@Controller
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ResourceBundleMessageSource errMessageSource;
	
	@GetMapping("/find/all")
	public ResponseEntity<PaginatedResponseList<User, UserDto>> findAll(
			@RequestParam(name = "currentPage", required = false) Optional<Integer> currentPage,
			@RequestParam(name = "pageSize", required = false) Optional<Integer> pageSize) throws NotFoundException {
		
		log.info("******** Get all Users ********");
		
		 
		PaginatedResponseList<User, UserDto> userPagList = userService.getAll(currentPage, pageSize);
		
		return new ResponseEntity<PaginatedResponseList<User, UserDto>>(userPagList, HttpStatus.OK);
	}
	
	@GetMapping("/find/userid/{userId}")
	public ResponseEntity<UserDto> findUserId(@PathVariable String userId) throws NotFoundException {
		
		if (userId == null) {
			throw new NotFoundException("Insert a valid userId");
		}
		
		log.info("******** Get user with userId %s ********".formatted(userId));
		
		UserDto userDto = userService.getByUserId(userId);
		
		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
	}
	
	@PostMapping("/create")
	public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto,
											BindingResult bindingResult) throws ItemAlreadyExistsException, BindingException, NotFoundException {
		
		// Check article data validity
		if (bindingResult.hasErrors()) {
			String msgErr = errMessageSource.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			
			log.warning(msgErr);
			
			List<ObjectError> errorValidationList = bindingResult.getAllErrors();
			
			throw new BindingException(msgErr, errorValidationList);
		}
		
		// Check if the user to be created already exists
		UserDto user = userService.getByUserId(userDto.getUserId());
		if (user != null) {
			
			String errMsg = "User '%s' already exists".formatted(userDto.getUserId());
			log.warning(errMsg);
			
			throw new ItemAlreadyExistsException(errMsg);
		}
		
		UserDto newUserDto = userService.create(userDto);
		
		
		
		return new ResponseEntity<UserDto>(newUserDto, HttpStatus.CREATED);
	}
}


