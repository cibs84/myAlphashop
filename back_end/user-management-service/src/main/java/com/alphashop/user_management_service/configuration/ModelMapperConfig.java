package com.alphashop.user_management_service.configuration;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    ModelMapper modelMapperBean() {
    	
    	ModelMapper modelMapper = new ModelMapper();
    	
    	modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    	
    	return modelMapper;
    }
}
