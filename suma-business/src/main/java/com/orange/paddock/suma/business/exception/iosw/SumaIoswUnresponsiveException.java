package com.orange.paddock.suma.business.exception.iosw;

import org.springframework.http.HttpStatus;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class SumaIoswUnresponsiveException extends AbstractSumaException{

	private static final long serialVersionUID = 1L;

	public SumaIoswUnresponsiveException() {
		
		super("IOSW is not responsive");
		internalErrorCode = INTERNAL_SUMA_2001_CODE;
		errorCode = SUMA_ERROR_CODE_00002;
		errorDescription="IOSW is not responsive : PDK_SUMA_2001";
		httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
	}
	
}
