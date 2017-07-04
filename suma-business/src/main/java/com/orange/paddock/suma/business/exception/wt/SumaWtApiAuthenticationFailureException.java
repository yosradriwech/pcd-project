package com.orange.paddock.suma.business.exception.wt;

import org.springframework.http.HttpStatus;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class SumaWtApiAuthenticationFailureException extends AbstractSumaException {

	private static final long serialVersionUID = 1L;

	public SumaWtApiAuthenticationFailureException() {
		super("WT-API Authentication Failure");
		internalErrorCode = INTERNAL_SUMA_3002_CODE;
		errorCode = SUMA_ERROR_CODE_00002;
		errorDescription = "Orange API Token or ISE2 is invalid";
		httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
	}

}
