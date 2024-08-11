package com.alphashop.exceptions;

import java.util.Date;

import lombok.Data;

@Data
public class ErrorResponse {

	private Date date = new Date();
	private int code;
	private String message;
}
