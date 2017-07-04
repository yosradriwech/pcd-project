package com.orange.paddock.suma.business.exception;

import org.springframework.http.HttpStatus;

public class SumaInternalErrorException extends AbstractSumaException{

	private static final long serialVersionUID = 1L;

	public SumaInternalErrorException() {
		
		super("Internal error");
		internalErrorCode = INTERNAL_SUMA_0100_CODE;
		errorCode = SUMA_ERROR_CODE_00002;
		httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();

	}
}
