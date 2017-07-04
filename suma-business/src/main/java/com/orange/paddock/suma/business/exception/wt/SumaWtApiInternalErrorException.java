package com.orange.paddock.suma.business.exception.wt;

import org.springframework.http.HttpStatus;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class SumaWtApiInternalErrorException extends AbstractSumaException{
	
	private static final long serialVersionUID = 1L;

	public SumaWtApiInternalErrorException() {
		
		super("WT-API internal error");
		internalErrorCode = INTERNAL_SUMA_3000_CODE;
		errorCode = SUMA_ERROR_CODE_00002;
		errorDescription="WT-API internal error : PDK_SUMA_3000";
		httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
	}

}
