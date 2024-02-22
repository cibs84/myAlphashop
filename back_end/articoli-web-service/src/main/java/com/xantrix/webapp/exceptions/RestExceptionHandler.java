package com.xantrix.webapp.exceptions;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(NotFoundException.class)
	public final ResponseEntity<ErrorResponse> exceptionNotFoundHandler(Exception ex){
		ErrorResponse response = new ErrorResponse();
		response.setData(new Date());
		response.setCodice(HttpStatus.NOT_FOUND.value());
		response.setMessaggio(ex.getMessage());
		
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ItemAlreadyExistsException.class)
	public final ResponseEntity<ErrorResponse> exceptionItemAlreadyExistsHandler(Exception ex){
		ErrorResponse response = new ErrorResponse();
		response.setData(new Date());
		response.setCodice(HttpStatus.NOT_ACCEPTABLE.value());
		response.setMessaggio(ex.getMessage());
		
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_ACCEPTABLE);
	}
}
