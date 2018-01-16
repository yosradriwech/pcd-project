package com.orange.paddock.suma.business.exception.ccgw;

import org.springframework.http.HttpStatus;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class SumaCcgwIntegrationErrorException extends AbstractSumaException {

	private static final long serialVersionUID = 1L;

	public SumaCcgwIntegrationErrorException(String description) {
		super("CCGW integration error");
		internalErrorCode = INTERNAL_SUMA_1002_CODE;
		errorCode = SUMA_ERROR_CODE_00002;
//		errorDescription = "Susbcription generated on CCGW side, waiting for notification";
		
		errorDescription = "Backend error: ["+description+"]";
		httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
	}
}
