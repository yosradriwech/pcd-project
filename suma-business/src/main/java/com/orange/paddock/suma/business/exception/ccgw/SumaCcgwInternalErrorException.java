package com.orange.paddock.suma.business.exception.ccgw;

import org.springframework.http.HttpStatus;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;

public class SumaCcgwInternalErrorException extends AbstractSumaException{
	
	private static final long serialVersionUID = 1L;

	public SumaCcgwInternalErrorException() {	
		super("CCGW internal error");
		internalErrorCode = INTERNAL_SUMA_1000_CODE;
		errorCode = SUMA_ERROR_CODE_00002;
		errorDescription="CCGW internal error : PDK_SUMA_1000";
		httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();

	}

	public SumaCcgwInternalErrorException(CcgwClientException e) {
		super("CCGW internal error");
		internalErrorCode = INTERNAL_SUMA_1000_CODE;
		errorCode = SUMA_ERROR_CODE_00002;
		errorDescription = String.join(", ", e.getCcgwFaultStatusErrorParams());
		httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
	}

}
