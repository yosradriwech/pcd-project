package com.orange.paddock.suma.business.exception;

import org.springframework.http.HttpStatus;

public class SumaDuplicateSubscriptionRequestException extends AbstractSumaException{

	private static final long serialVersionUID = 1L;

	public SumaDuplicateSubscriptionRequestException() {
		super("Duplicate subscription request");
		internalErrorCode = INTERNAL_SUMA_0008_CODE;
		errorCode = SUMA_ERROR_CODE_00005;
		httpStatusCode = HttpStatus.CONFLICT.value();
		errorDescription = "Duplicate subscription request";
	}

	
}
