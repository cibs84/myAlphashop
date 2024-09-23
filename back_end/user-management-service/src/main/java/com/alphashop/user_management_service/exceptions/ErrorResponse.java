package com.alphashop.user_management_service.exceptions;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.validation.ObjectError;

import lombok.Data;

@Data
public class ErrorResponse {

	private Date date = new Date();
	private int code;
	private String message;
	private Map<String, List<String>> errorValidationMap;
}
