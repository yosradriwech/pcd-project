package com.orange.paddock.suma.business.exception;

import org.springframework.http.HttpStatus;

public class SumaBadRequestException extends AbstractSumaException {

	private static final long serialVersionUID = 1L;

	public SumaBadRequestException() {
		super("Bad request");
		internalErrorCode = INTERNAL_SUMA_0001_CODE;
		errorCode = SUMA_ERROR_CODE_00003;
		errorDescription = "Invalid or missing parameter";
		httpStatusCode = HttpStatus.BAD_REQUEST.value();

	}

	public SumaBadRequestException(String invalidParameter) {
		super("Bad request");
		internalErrorCode = INTERNAL_SUMA_0001_CODE;
		errorCode = SUMA_ERROR_CODE_00003;
		errorDescription = new StringBuilder().append("Invalid or missing parameter :").append(invalidParameter).toString();
		httpStatusCode = HttpStatus.BAD_REQUEST.value();
	}

}
