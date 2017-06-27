package com.orange.paddock.suma.consumer.ccgw.interceptors;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

public class CcgwSoapFaultInInterceptor extends AbstractSoapInterceptor {

	private Integer httpResponseCode;
	
	public CcgwSoapFaultInInterceptor() {
		super(Phase.PRE_LOGICAL);
	}

	public void handleMessage(SoapMessage message) throws Fault {
		httpResponseCode = (Integer) message.get(Message.RESPONSE_CODE);
	}

	public Integer getHttpResponseCode() {
		return httpResponseCode;
	}

	public void setHttpResponseCode(Integer httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}

}
