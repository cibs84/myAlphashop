package com.alphashop.user_management_service.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alphashop.user_management_service.common.PaginatedResponseList;
import com.alphashop.user_management_service.dtos.UserDto;
import com.alphashop.user_management_service.exceptions.NotFoundException;
import com.alphashop.user_management_service.models.User;
import com.alphashop.user_management_service.repositories.UserRepository;

import lombok.extern.java.Log;

@Service
@Log
@Transactional(readOnly = true)
public class UserService {
	
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final BCryptPasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository,
						ModelMapper modelMapper,
						BCryptPasswordEncoder passwordEncoder) {
		
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
		this.passwordEncoder = passwordEncoder;
	}
	
	public PaginatedResponseList<User, UserDto> getAll(Optional<Integer> currentPage, 
													   Optional<Integer> pageSize) throws NotFoundException {
		
		Pageable usersPagination = PageRequest.of(currentPage.filter(n -> n > -1).orElse(1),
				pageSize.filter(n -> n > 0).orElse(10));
		
		Page<User> userPagList = new PageImpl<User>(userRepository.findAll(), usersPagination, 0);
		
		if (userPagList.isEmpty()) {
			String errMsg = "No users were found";
			
			log.warning(errMsg);
			
			throw new NotFoundException(errMsg);
		}
		
		// Converts Page<User> to List<UserDto>
		List<UserDto> userDtoList = userPagList.stream().map(user -> modelMapper.map(user, UserDto.class))
				.collect(Collectors.toList());
		
		return new PaginatedResponseList<User, UserDto>(userPagList, userDtoList);
	}

	@Transactional
	// Used by create and update UserController methods
	public UserDto create(UserDto userDto) { 
		
		System.out.println("RAW PASSWORD 'userDtov : " + userDto.getPassword());
		
		String hashedPassword = hashPassword(userDto.getPassword());
		
		System.out.println("HASHED PASSWORD : " + hashedPassword);
		
		userDto.setPassword(hashedPassword);

		User user = modelMapper.map(userDto, User.class);
		user = userRepository.save(user);
		userDto = modelMapper.map(user, UserDto.class);
		
		return userDto;
	}

	public UserDto getByUserId(String userId) throws NotFoundException {
		
		Optional<User> user = userRepository.findByUserId(userId);
		
		UserDto userDto = null;
		if (user.isPresent()) {
			userDto = modelMapper.map(user.get(), UserDto.class);
		}
		
		return userDto;
	}
	
	public void delete(UserDto userDto) {
		userRepository.delete(modelMapper.map(userDto, User.class));
	}
	
	private String hashPassword (String password) {
		return passwordEncoder.encode(password);
	}
}
