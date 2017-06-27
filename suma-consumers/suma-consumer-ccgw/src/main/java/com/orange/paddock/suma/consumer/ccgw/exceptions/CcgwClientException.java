package com.orange.paddock.suma.consumer.ccgw.exceptions;

import java.util.ArrayList;
import java.util.List;

public class CcgwClientException extends Exception {

	private static final long serialVersionUID = 1L;

	// Information below retrieved from CCGW client response
	private int ccgwFaultHttpStatusCode;
	private String ccgwFaultStatusCode;
	private List<String> ccgwFaultStatusErrorParams;

	// Information below retrieved from SOAP fault
	private String soapFaultMessage;
	private String soapFaultCode;
	private int soapFaultHttpStatusCode;

	public CcgwClientException(String message) {
		super(message);
		this.ccgwFaultStatusCode = "0";
		this.ccgwFaultStatusErrorParams = new ArrayList<String>();
	}

	public CcgwClientException() {
		super("An unexpected error occured while calling CCGW client");
		this.ccgwFaultStatusCode = "0";
		this.ccgwFaultStatusErrorParams = new ArrayList<String>();
	}

	public CcgwClientException(int ccgwFaultHttpStatusCode, String ccgwFaultStatusCode, List<String> ccgwFaultStatusErrorParams) {
		super();
		this.ccgwFaultHttpStatusCode = ccgwFaultHttpStatusCode;
		this.ccgwFaultStatusCode = ccgwFaultStatusCode;
		this.ccgwFaultStatusErrorParams = ccgwFaultStatusErrorParams;
	}

	public int getCcgwFaultHttpStatusCode() {
		return ccgwFaultHttpStatusCode;
	}

	public void setCcgwFaultHttpStatusCode(int ccgwFaultHttpStatusCode) {
		this.ccgwFaultHttpStatusCode = ccgwFaultHttpStatusCode;
	}

	public String getCcgwFaultStatusCode() {
		return ccgwFaultStatusCode;
	}

	public void setCcgwFaultStatusCode(String ccgwFaultStatusCode) {
		this.ccgwFaultStatusCode = ccgwFaultStatusCode;
	}

	public List<String> getCcgwFaultStatusErrorParams() {
		return ccgwFaultStatusErrorParams;
	}

	public void setCcgwFaultStatusErrorParams(List<String> ccgwFaultStatusErrorParams) {
		this.ccgwFaultStatusErrorParams = ccgwFaultStatusErrorParams;
	}

	public String getSoapFaultMessage() {
		return soapFaultMessage;
	}

	public void setSoapFaultMessage(String soapFaultMessage) {
		this.soapFaultMessage = soapFaultMessage;
	}

	public String getSoapFaultCode() {
		return soapFaultCode;
	}

	public void setSoapFaultCode(String soapFaultCode) {
		this.soapFaultCode = soapFaultCode;
	}

	public int getSoapFaultHttpStatusCode() {
		return soapFaultHttpStatusCode;
	}

	public void setSoapFaultHttpStatusCode(int soapFaultHttpStatusCode) {
		this.soapFaultHttpStatusCode = soapFaultHttpStatusCode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CcgwClientException [ccgwFaultHttpStatusCode=").append(ccgwFaultHttpStatusCode).append(", ccgwFaultStatusCode=")
				.append(ccgwFaultStatusCode).append(", ccgwFaultStatusErrorParams=").append(ccgwFaultStatusErrorParams).append(", soapFaultMessage=")
				.append(soapFaultMessage).append(", soapFaultCode=").append(soapFaultCode).append(", soapFaultHttpStatusCode=")
				.append(soapFaultHttpStatusCode).append("]");
		return sb.toString();
	}
}
