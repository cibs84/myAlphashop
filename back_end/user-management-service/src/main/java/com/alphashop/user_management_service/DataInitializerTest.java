package com.alphashop.user_management_service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.alphashop.user_management_service.repositories.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
@Profile("test")
public class DataInitializerTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    ModelMapper modelMapper;

    @PostConstruct
    public void initializeDataTest() {
        
    	// DELETE ALL USER
    	userRepository.deleteAll();
    }
}
