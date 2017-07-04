package com.orange.paddock.suma.business.exception;

public class SUMABadRequestException extends AbstractSumaException{

	private static final long serialVersionUID = 7272913764021488940L;

	public SUMABadRequestException() {
		super("Integration Error (PDK_SUMA_0001)");
		internalErrorCode = "PDK_SUMA_0001";
		description = "Integration Error";
		oneApiErrorCode = "SVC0001";
		oneApiErrorDescription = "A service error occurred. Error code is %1";
		oneApiErrorVariables.add("PDK_SUMA_0001");
		httpCode = HTTP_CODE_BAD_REQUEST;
	}
	
	public SUMABadRequestException(String message){
		super(message);
		internalErrorCode = "PDK_SUMA_0001";
		description = "Integration Error";
		oneApiErrorCode = "SVC0001";
		oneApiErrorDescription = "A service error occurred. Error code is %1";
		oneApiErrorVariables.add("PDK_SUMA_0001");
		httpCode = HTTP_CODE_BAD_REQUEST;
	}

}
