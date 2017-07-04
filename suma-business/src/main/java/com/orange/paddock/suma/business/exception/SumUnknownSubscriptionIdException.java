package com.orange.paddock.suma.business.exception;

import org.springframework.http.HttpStatus;

public class SumUnknownSubscriptionIdException extends AbstractSumaException {

	private static final long serialVersionUID = 1L;

	public SumUnknownSubscriptionIdException() {
		super("Unknown subscription Identifier");

		internalErrorCode = INTERNAL_SUMA_0002_CODE;
		errorCode = SUMA_ERROR_CODE_00004;

		errorDescription = "Unknown subscription identifier";
		httpStatusCode = HttpStatus.NOT_FOUND.value();

	}

	public SumUnknownSubscriptionIdException(String subscriptionId) {
		super("Unknown subscription Identifier");
		internalErrorCode = INTERNAL_SUMA_0002_CODE;
		errorCode = SUMA_ERROR_CODE_00004;
		
		errorDescription = new StringBuilder().append("Unknown subscription: ").append(subscriptionId).toString();
		httpStatusCode = HttpStatus.NOT_FOUND.value();
	}

}
