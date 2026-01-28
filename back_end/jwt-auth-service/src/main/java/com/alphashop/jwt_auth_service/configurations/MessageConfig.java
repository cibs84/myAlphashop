package com.alphashop.jwt_auth_service.configurations;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import org.springframework.validation.Validator;

@Configuration
public class MessageConfig implements WebMvcConfigurer
{
	@Bean(name = "validator")
	LocalValidatorFactoryBean validator() // Specifies the source of error messages
	{
		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
		bean.setValidationMessageSource(messageSource());

		return bean;
	}
	
	@Bean
	LocaleResolver localeResolver() // Allows you to specify the language to be used for error messages
	{
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
//		sessionLocaleResolver.setDefaultLocale(LocaleContextHolder.getLocale()); // Use the language set by default by the system
		sessionLocaleResolver.setDefaultLocale(new Locale("en")); // Force to use the English language (file: messages_en.properties)
			
		return sessionLocaleResolver;
	}

	@Bean
	MessageSource messageSource() // Generates the MessageSource used in the validator() method
	{
		ResourceBundleMessageSource resource = new ResourceBundleMessageSource();
		resource.setBasename("messages"); // Prefix to create files with error messages (e.g., messages_en.properties)
		resource.setUseCodeAsDefaultMessage(true);

		return resource;
	}
	
	@Override
    public Validator getValidator() {
        return validator(); // Ritorna il bean che hai già definito
    }
}
