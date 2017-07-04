package com.orange.paddock.suma.business.exception.wt;

import org.springframework.http.HttpStatus;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class SumaWtApiIntegrationException extends AbstractSumaException{
		
	private static final long serialVersionUID = 1L;

	public SumaWtApiIntegrationException() {
		
		super("WT-API integration error");
		internalErrorCode = INTERNAL_SUMA_3001_CODE;
		errorCode = SUMA_ERROR_CODE_00002;
		errorDescription="WT-API integration error : PDK_SUMA_3001";
		httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
	}

}
