package com.orange.paddock.suma.consumer.ccgw.interceptors;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;

public class CcgwSoapFaultInInterceptor extends AbstractSoapInterceptor {
	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(CcgwSoapFaultInInterceptor.class);
	private Integer httpResponseCode;
	
	public CcgwSoapFaultInInterceptor() {
		super(Phase.PRE_LOGICAL);
	}

	public void handleMessage(SoapMessage message) throws Fault {
		TECHNICAL_LOGGER.debug("UNSUB INTERCEPTOR FAULT " + message.get(Message.RESPONSE_CODE));
		httpResponseCode = (Integer) message.get(Message.RESPONSE_CODE);
	}

	public Integer getHttpResponseCode() {
		return httpResponseCode;
	}

	public void setHttpResponseCode(Integer httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}

}
