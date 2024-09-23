package com.alphashop.user_management_service.exceptions;

public class NotDeletableException extends Exception {
	
	private static final long serialVersionUID = 9016521721110018196L;
	
	private String errorMessage = String.format("Article not deletable!");

	public NotDeletableException() {
		super();
	}

	public NotDeletableException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
