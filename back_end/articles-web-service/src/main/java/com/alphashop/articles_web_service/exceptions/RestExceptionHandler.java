package com.alphashop.articles_web_service.exceptions;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.java.Log;

@Log
@ControllerAdvice
@RestController
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(NotFoundException.class)
	public final ResponseEntity<ErrorResponse> exceptionNotFoundHandler(Exception ex) {
		ErrorResponse response = new ErrorResponse();
		response.setDate(new Date());
		response.setStatus(HttpStatus.NOT_FOUND.value());
		response.setMessage(ex.getMessage());
		
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ItemAlreadyExistsException.class)
	public final ResponseEntity<ErrorResponse> exceptionItemAlreadyExistsHandler(Exception ex) {
		ErrorResponse response = new ErrorResponse();
		response.setDate(new Date());
		response.setStatus(HttpStatus.CONFLICT.value());
		response.setMessage(ex.getMessage());

		return new ResponseEntity<ErrorResponse>(response, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(BindingException.class)
	public final ResponseEntity<ErrorResponse> exceptionBindingHandler(BindingException ex)
	{
		ErrorResponse response = new ErrorResponse();
		response.setDate(new Date());
		response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
		response.setMessage("Validation error");
		
		// Collects validation errors into a map where the key is the field name 
		// and the value is a list of error messages.
		Map<String, List<String>> errorValidationMap = ex.getErrorValidationList().stream()
			    .map(FieldError.class::cast)  // Cast to FieldError directly (assuming it's the type)
			    .collect(Collectors.groupingBy(FieldError::getField,
			            Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
		
		response.setErrorValidationMap(errorValidationMap);
		
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(NotDeletableException.class)
	public final ResponseEntity<ErrorResponse> exceptionNotDeletableHandler(Exception ex) {
		ErrorResponse response = new ErrorResponse();
		response.setDate(new Date());
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setMessage(ex.getMessage());

		return new ResponseEntity<ErrorResponse>(response, HttpStatus.FORBIDDEN);
	}
	
}
