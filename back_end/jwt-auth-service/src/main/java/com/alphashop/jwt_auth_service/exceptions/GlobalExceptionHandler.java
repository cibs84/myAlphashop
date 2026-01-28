package com.alphashop.jwt_auth_service.exceptions;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler({ JwtAuthenticationException.class })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(JwtAuthenticationException e) {
        
    	ErrorResponse response = new ErrorResponse();
 		response.setDate(new Date());
 		response.setStatus(HttpStatus.UNAUTHORIZED.value());
 		response.setCode("INVALID_CREDENTIALS");
    	
    	// Restituisce una risposta 401 (Unauthorized) con il messaggio dell'eccezione
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    // Viene lanciato se fallisce @Valid impostato nel controller
 	@ExceptionHandler(MethodArgumentNotValidException.class)
     public final ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        
 		BindingResult bindingResult = ex.getBindingResult();

         List<ObjectError> errorValidationList = bindingResult.getAllErrors();
         
 		// Collects validation errors into a map where the key is the field name 
 		// and the value is a list of error messages.
 		Map<String, List<String>> errorValidationMap = errorValidationList.stream()
 			    .map(FieldError.class::cast)  // Cast to FieldError directly (assuming it's the type)
 			    .collect(Collectors.groupingBy(FieldError::getField,
 			            Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
 		
 		ErrorResponse response = new ErrorResponse();
 		response.setDate(new Date());
 		response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
 		response.setCode("VALIDATION_ERROR");
 		response.setErrorValidationMap(errorValidationMap);
 		
// 		log.warn("Validation failed for request: {}", errorValidationMap);
 		
 		return new ResponseEntity<ErrorResponse>(response, HttpStatus.UNPROCESSABLE_ENTITY);
     }
}