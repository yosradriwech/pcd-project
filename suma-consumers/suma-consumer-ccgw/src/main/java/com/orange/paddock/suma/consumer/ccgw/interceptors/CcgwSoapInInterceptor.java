package com.orange.paddock.suma.consumer.ccgw.interceptors;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CcgwSoapInInterceptor extends AbstractPhaseInterceptor<Message> {
	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(CcgwSoapInInterceptor.class);
	private Integer httpResponseCode;

	public CcgwSoapInInterceptor() {
		super(Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) throws Fault{
		TECHNICAL_LOGGER.debug("UNSUB INTERCEPTOR " + message.get(Message.RESPONSE_CODE));
		httpResponseCode = (Integer) message.get(Message.RESPONSE_CODE);
	}

	public Integer getHttpResponseCode() {
		return httpResponseCode;
	}

	public void setHttpResponseCode(Integer httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}

}
