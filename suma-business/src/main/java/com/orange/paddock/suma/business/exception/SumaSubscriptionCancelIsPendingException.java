package com.orange.paddock.suma.business.exception;

import org.springframework.http.HttpStatus;

public class SumaSubscriptionCancelIsPendingException extends AbstractSumaException{

	private static final long serialVersionUID = 1L;

	public SumaSubscriptionCancelIsPendingException() {
		super("Subscription cancellation is pending");
		internalErrorCode = INTERNAL_SUMA_0009_CODE;
		errorCode = SUMA_ERROR_CODE_00006;
		httpStatusCode = HttpStatus.CONFLICT.value();
		errorDescription = "Subscription cancellation is pending";
	}

}
