package com.orange.paddock.suma.provider.rest.model;

public class SumaError {
	
	protected String internalErrorCode;
	protected String errorCode;
	protected String errorDescription;
	protected int httpStatusCode;
	
	public String getInternalErrorCode() {
		return internalErrorCode;
	}
	public void setInternalErrorCode(String internalErrorCode) {
		this.internalErrorCode = internalErrorCode;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

}
