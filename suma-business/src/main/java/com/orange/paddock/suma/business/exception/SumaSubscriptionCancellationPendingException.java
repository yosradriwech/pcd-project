package com.orange.paddock.suma.business.exception;

import org.springframework.http.HttpStatus;

public class SumaSubscriptionCancellationPendingException extends AbstractSumaException{

	private static final long serialVersionUID = 1L;

	public SumaSubscriptionCancellationPendingException() {
		super("Subscription cancellation is pending");
		internalErrorCode = INTERNAL_SUMA_0010_CODE;
		errorCode = SUMA_ERROR_CODE_00006;
		httpStatusCode = HttpStatus.CONFLICT.value();
		errorDescription = "Subscription cancellation is pending";
	}

}
