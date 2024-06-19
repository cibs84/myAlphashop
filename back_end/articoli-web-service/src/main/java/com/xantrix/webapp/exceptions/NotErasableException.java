package com.xantrix.webapp.exceptions;

public class NotErasableException extends Exception {
	
	private static final long serialVersionUID = 9016521721110018196L;
	
	private String errorMessage = String.format("Articolo non eliminabile!");

	public NotErasableException() {
		super();
	}

	public NotErasableException(String errorMessage) {
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
