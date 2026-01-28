package com.alphashop.user_management_service.exceptions;

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
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RestController
public class RestExceptionHandler {
	
	@ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        
    	log.error("Unexpected system error: ", ex);
        
        ErrorResponse response = new ErrorResponse();
        response.setDate(new Date());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setCode("INTERNAL_SERVER_ERROR");
        response.setErrorValidationMap(null);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	@ExceptionHandler(NotFoundException.class)
	public final ResponseEntity<ErrorResponse> exceptionNotFoundHandler(NotFoundException ex) {
		log.warn("Resource not found: {}", ex.getMessage());
		return buildError(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND");
	}
	
	@ExceptionHandler({
	    MissingPathVariableException.class, 
	    NoResourceFoundException.class
	})
	public final ResponseEntity<ErrorResponse> handleMissingPath(Exception ex) {
	    log.warn("Malformed request or missing resource: {}", ex.getMessage());
	    
		 // We use ITEM_NOT_FOUND because for the frontend,
		 // a missing variable in the path is equivalent to a resource not found
	    return buildError(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND");
	}
	
	@ExceptionHandler(ItemAlreadyExistsException.class)
	public final ResponseEntity<ErrorResponse> exceptionItemAlreadyExistsHandler(ItemAlreadyExistsException ex) {
		log.warn("Conflict detected: {}", ex.getMessage());
		return buildError(HttpStatus.CONFLICT, "ITEM_ALREADY_EXISTS");
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
		
		log.warn("Validation failed for request: {}", errorValidationMap);
		
		return new ResponseEntity<ErrorResponse>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
	
	@ExceptionHandler(NotDeletableException.class)
	public final ResponseEntity<ErrorResponse> exceptionNotDeletableHandler(NotDeletableException ex) {
		log.warn("Forbidden operation: {}", ex.getMessage());
		return buildError(HttpStatus.FORBIDDEN, "NOT_DELETABLE");
	}
	
	private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String code) {
        ErrorResponse response = new ErrorResponse();
        response.setDate(new Date());
        response.setStatus(status.value());
        response.setCode(code);
        return ResponseEntity.status(status).body(response);
    }
}
