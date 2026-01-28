package com.alphashop.jwt_auth_service.exceptions;

import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ErrorResponse {

	private Date date = new Date();
	private int status;
	private String code;
	private Map<String, List<String>> errorValidationMap;
}
