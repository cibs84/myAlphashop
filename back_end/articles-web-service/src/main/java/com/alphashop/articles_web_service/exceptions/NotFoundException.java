package com.alphashop.articles_web_service.exceptions;

public class NotFoundException extends Exception {

	private static final long serialVersionUID = 2270349322050673409L;
	private String errorMessage = "L'elemento ricercato non è stato trovato!";

	public NotFoundException() {
		super();
	}

	public NotFoundException(String errorMessage) {
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
