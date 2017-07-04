package com.orange.paddock.suma.business.exception.iosw;

import org.springframework.http.HttpStatus;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class SumaIoswInternalErrorException extends AbstractSumaException{

	private static final long serialVersionUID = 1L;

	public SumaIoswInternalErrorException() {
		
		super("IOSW internal error");
		internalErrorCode = INTERNAL_SUMA_2000_CODE;
		errorCode = SUMA_ERROR_CODE_00002;
		errorDescription="IOSW internal error : PDK_SUMA_2000";
		httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
	}
}
