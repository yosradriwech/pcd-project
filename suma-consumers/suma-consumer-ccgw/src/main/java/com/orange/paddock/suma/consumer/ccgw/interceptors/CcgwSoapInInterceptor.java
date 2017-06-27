package com.orange.paddock.suma.consumer.ccgw.interceptors;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class CcgwSoapInInterceptor extends AbstractPhaseInterceptor<Message> {

	private Integer httpResponseCode;

	public CcgwSoapInInterceptor() {
		super(Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) throws Fault{
		httpResponseCode = (Integer) message.get(Message.RESPONSE_CODE);
	}

	public Integer getHttpResponseCode() {
		return httpResponseCode;
	}

	public void setHttpResponseCode(Integer httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}

}
