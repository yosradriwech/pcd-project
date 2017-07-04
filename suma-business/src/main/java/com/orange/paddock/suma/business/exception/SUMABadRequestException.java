package com.orange.paddock.suma.business.exception;

import org.springframework.http.HttpStatus;

public class SUMABadRequestException extends AbstractSumaException {

	private static final long serialVersionUID = 1L;

	public SUMABadRequestException() {
		super("Bad request received");
		internalErrorCode = INTERNAL_SUMA_0001_CODE;
		errorCode = SUMA_ERROR_CODE_00003;
		errorDescription = "Invalid or missing parameter";
		httpStatusCode = HttpStatus.BAD_REQUEST.value();

	}

	public SUMABadRequestException(String invalidParameter) {
		super("Bad request received");
		internalErrorCode = INTERNAL_SUMA_0001_CODE;
		errorCode = SUMA_ERROR_CODE_00003;
		errorDescription = new StringBuilder().append("Invalid or missing parameter :").append(invalidParameter).toString();
		httpStatusCode = HttpStatus.BAD_REQUEST.value();
	}

}
