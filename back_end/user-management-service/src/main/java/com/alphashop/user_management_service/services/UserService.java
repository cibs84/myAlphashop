package com.alphashop.user_management_service.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alphashop.user_management_service.common.PaginatedResponseList;
import com.alphashop.user_management_service.dtos.UserDto;
import com.alphashop.user_management_service.exceptions.NotFoundException;
import com.alphashop.user_management_service.models.User;
import com.alphashop.user_management_service.repositories.UserRepository;

import jakarta.annotation.Nullable;
import lombok.extern.java.Log;

@Service
@Log
@Transactional(readOnly = true)
public class UserService {
	
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	
	public UserService(UserRepository userRepository,
						ModelMapper modelMapper) {
		
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
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
	public UserDto create(UserDto userDto) {

		User user = modelMapper.map(userDto, User.class);
		user = userRepository.save(user);
		userDto = modelMapper.map(user, UserDto.class);
		
		return userDto;
	}

	public UserDto getByUserId(String userId) throws NotFoundException {
		boolean throwExceptionIfNotFound = true;
		return getByUserId(userId, throwExceptionIfNotFound);
	}
	
	public UserDto getByUserId(String userId, Boolean throwExceptionIfNotFound) throws NotFoundException {
		
		Optional<User> user = userRepository.findByUserId(userId);
		UserDto userDto = null;
		
		throwExceptionIfNotFound = throwExceptionIfNotFound == null ? true : throwExceptionIfNotFound;
		
		if (user.isEmpty() && throwExceptionIfNotFound) {
			String errMsg = "User with userId '%s' not found".formatted(userId);
			log.warning(errMsg);
			
			throw new NotFoundException(errMsg);
		} else if (user.isPresent()) {

			userDto = modelMapper.map(user.get(), UserDto.class);
		}
		
		return userDto;
	}
}
