package com.alphashop.articles_web_service.exceptions;

public class ItemAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 5584960734228774089L;
	private String errorMessage = "Item already exists";

	public ItemAlreadyExistsException() {
		super();
	}

	public ItemAlreadyExistsException(String errorMessage) {
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
