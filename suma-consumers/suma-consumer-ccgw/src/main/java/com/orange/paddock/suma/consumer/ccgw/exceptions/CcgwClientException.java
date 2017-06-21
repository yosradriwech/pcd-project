package com.orange.paddock.suma.consumer.ccgw.exceptions;

import java.util.List;

public class CcgwClientException extends Exception {

	private static final long serialVersionUID = 1L;

	private String code;
	private List<String> errorParams;

	public CcgwClientException(String message) {
		super(message);
	}

	public CcgwClientException() {
		super("An unexpected error occured");
		this.code="0";
	}

	public CcgwClientException(String code, List<String> errorParams) {
		super();
		this.code = code;
		this.errorParams = errorParams;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<String> getErrorParams() {
		return errorParams;
	}

	public void setErrorParams(List<String> errorParams) {
		this.errorParams = errorParams;
	}

	@Override
	public String toString() {
		return "CcgwClientException [code=" + code + ", errorParams=" + String.join(", ", errorParams) + "]";
	}

}
