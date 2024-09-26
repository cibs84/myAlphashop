//package com.alphashop.user_management_service;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.alphashop.user_management_service.dtos.UserDto;
//import com.alphashop.user_management_service.models.User;
//import com.alphashop.user_management_service.repositories.UserRepository;
//
//import jakarta.annotation.PostConstruct;
//
//@Component
//public class DataInitializer {
//
//    @Autowired
//    private UserRepository userRepository;
//    
//    @Autowired
//    ModelMapper modelMapper;
//
//    @PostConstruct
//    public void initializeData() throws CloneNotSupportedException {
//        
//    	// DELETE ALL USER
//    	userRepository.deleteAll();
//
//    	// CREATE USER
//        UserDto user_1 = new UserDto();
//        user_1.setUserId("user_1");
//        user_1.setPassword("pass1234");
//        user_1.setActive(true);
//        user_1.setRoles(Arrays.asList("USER"));
//    	// CREATE USER
//        UserDto user_2 = user_1.clone();
//        user_2.setUserId("user_2");
//        // CREATE USER
//        UserDto user_3 = user_1.clone();
//        user_3.setUserId("user_3");
//        user_3.setRoles(Arrays.asList("USER", "ADMIN"));
//        
//        List<User> users = Arrays.asList(modelMapper.map(user_1, User.class), 
//							        		modelMapper.map(user_2, User.class), 
//							        		modelMapper.map(user_3, User.class));
//        
//        // SAVE NEW USERS
//        userRepository.saveAll(users);
//    }
//}
