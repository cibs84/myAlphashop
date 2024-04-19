package com.xantrix.webapp;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class MessageConfig 
{
	@Bean(name = "validator")
	LocalValidatorFactoryBean validator() // Specifica la sorgente dei mesaggi degli errori
	{
		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
		bean.setValidationMessageSource(messageSource());

		return bean;
	}
	
	@Bean
	LocaleResolver localeResolver() // Consente di specificare la lingua da utilizzare per i messaggi di errore
	{
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
		//sessionLocaleResolver.setDefaultLocale(LocaleContextHolder.getLocale()); // Utilizza la lingua impostata di default dal sistema
		sessionLocaleResolver.setDefaultLocale(new Locale("it")); // Forza ad utilizzare la lingua italiana
			
		return sessionLocaleResolver;
	}

	@Bean
	MessageSource messageSource() // Genera il MessageSource utilizzato nel metodo validator()
	{
		ResourceBundleMessageSource resource = new ResourceBundleMessageSource();
		resource.setBasename("messages"); // Prefisso per creare i file con i mesaggi di errore (es. messages_it.properties)
		resource.setUseCodeAsDefaultMessage(true);

		return resource;
	}
}
