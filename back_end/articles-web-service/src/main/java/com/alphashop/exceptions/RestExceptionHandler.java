package com.alphashop.exceptions;

import java.util.Date;

import org.springframework.http.HttpHeaders;
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
	public final ResponseEntity<ErrorResponse> exceptionNotFoundHandler(Exception ex) {
		ErrorResponse response = new ErrorResponse();
		response.setDate(new Date());
		response.setCode(HttpStatus.NOT_FOUND.value());
		response.setMessage(ex.getMessage());

		return new ResponseEntity<ErrorResponse>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ItemAlreadyExistsException.class)
	public final ResponseEntity<ErrorResponse> exceptionItemAlreadyExistsHandler(Exception ex) {
		ErrorResponse response = new ErrorResponse();
		response.setDate(new Date());
		response.setCode(HttpStatus.CONFLICT.value());
		response.setMessage(ex.getMessage());

		return new ResponseEntity<ErrorResponse>(response, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(BindingException.class)
	public final ResponseEntity<ErrorResponse> exceptionBindingHandler(Exception ex)
	{
		ErrorResponse response = new ErrorResponse();
		response.setDate(new Date());
		response.setCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
		response.setMessage(ex.getMessage());
		
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(NotDeletableException.class)
	public final ResponseEntity<ErrorResponse> exceptionNotDeletableHandler(Exception ex) {
		ErrorResponse response = new ErrorResponse();
		response.setDate(new Date());
		response.setCode(HttpStatus.FORBIDDEN.value());
		response.setMessage(ex.getMessage());

		return new ResponseEntity<ErrorResponse>(response, HttpStatus.FORBIDDEN);
	}
}
