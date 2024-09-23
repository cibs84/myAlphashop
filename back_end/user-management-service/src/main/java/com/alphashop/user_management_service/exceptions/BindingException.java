package com.alphashop.user_management_service.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.ObjectError;

public class BindingException extends Exception
{

	private static final long serialVersionUID = -1646083143194195402L;
	
	private String message;
	private List<ObjectError> errorValidationList;
	
	public BindingException()
	{
		super();
	}
	
	public BindingException(String message)
	{
		super(message);
		this.message = message;
		this.errorValidationList = new ArrayList<ObjectError>();
	}
	
	public BindingException(String message, List<ObjectError> errorValidationList)
	{
		super();
		this.message = message;
		this.errorValidationList = errorValidationList;
	}
	
	public BindingException(List<ObjectError> errorValidationList)
	{
		super();
		this.errorValidationList = errorValidationList;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public List<ObjectError> getErrorValidationList() {
		return errorValidationList;
	}

	public void setErrorValidationList(List<ObjectError> errorValidationList) {
		this.errorValidationList = errorValidationList;
	}

}
