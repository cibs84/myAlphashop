package com.alphashop.user_management_service;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.alphashop.user_management_service.dtos.UserDto;
import com.alphashop.user_management_service.models.User;
import com.alphashop.user_management_service.repositories.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
@Profile("!test") // All profiles except 'test'
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    ModelMapper modelMapper;
    
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeData() throws CloneNotSupportedException {
        
    	// DELETE ALL USER
    	userRepository.deleteAll();

    	// CREATE 'UserAdmin'
        UserDto userAdmin = new UserDto();
        userAdmin.setUserId("userAdmin");
        userAdmin.setPassword(passwordEncoder.encode("pass1234"));
        userAdmin.setActive(true);
        userAdmin.setRoles(Arrays.asList("USER", "ADMIN"));
    	        
        // CREATE 'UserRead'
        UserDto userRead = userAdmin.clone();
        userRead.setUserId("userRead");
        userRead.setRoles(Arrays.asList("USER"));
        
        List<User> users = Arrays.asList(modelMapper.map(userAdmin, User.class), 
							        		modelMapper.map(userRead, User.class));
        
        // SAVE NEW USERS
        userRepository.saveAll(users);
    }
}
