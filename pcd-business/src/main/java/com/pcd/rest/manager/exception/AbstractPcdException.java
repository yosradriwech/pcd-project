package com.pcd.rest.manager.exception;

public class AbstractPcdException extends Exception {

	private static final long serialVersionUID = 1L;

	protected String errorCode;
	protected String errorDescription;
	protected int httpStatusCode;
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
	
	public String getErrorDescription() {
		return errorDescription;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

}
