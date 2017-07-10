package com.orange.paddock.suma.business.exception;

import org.springframework.http.HttpStatus;

public class SumaAlreadyRevokedSubException extends AbstractSumaException{

	private static final long serialVersionUID = 15007119550461543L;

	public SumaAlreadyRevokedSubException() {
		super("Subscription already revoked");
		internalErrorCode = INTERNAL_SUMA_0003_CODE;
		errorCode = SUMA_ERROR_CODE_00004;
		
		errorDescription = "Subscription already revoked";
		httpStatusCode = HttpStatus.NOT_FOUND.value();

	}

	public SumaAlreadyRevokedSubException(String subscriptionId) {
		super("Subscription already revoked");
		internalErrorCode = INTERNAL_SUMA_0003_CODE;
		errorCode = SUMA_ERROR_CODE_00004;
		
		errorDescription = new StringBuilder().append("Subscription already revoked :").append(subscriptionId).toString();
		httpStatusCode = HttpStatus.NOT_FOUND.value();
	}

}
