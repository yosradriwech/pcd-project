package com.orange.paddock.suma.business.exception.ccgw;

import org.springframework.http.HttpStatus;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class SumaCcgwUnresponsiveException extends AbstractSumaException{

	private static final long serialVersionUID = 1L;

	public SumaCcgwUnresponsiveException() {
		
		super("CCGW is unresponsive");
		internalErrorCode = INTERNAL_SUMA_1001_CODE;
		errorCode = SUMA_ERROR_CODE_00002;
		errorDescription="CCGW is unresponsive : PDK_SUMA_1001";
		httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();

	}
}
