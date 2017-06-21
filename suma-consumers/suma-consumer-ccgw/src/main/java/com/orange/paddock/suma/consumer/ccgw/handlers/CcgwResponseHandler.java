package com.orange.paddock.suma.consumer.ccgw.handlers;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CcgwResponseHandler implements SOAPHandler<SOAPMessageContext> {
	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(CcgwResponseHandler.class);

	public CcgwResponseHandler() {
	}

	@Override
	public void close(MessageContext arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		TECHNICAL_LOGGER.debug("handle faallt");
		return false;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext messageContext) {
		Boolean isRequest = (Boolean) messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		TECHNICAL_LOGGER.debug("isRequest ?  " + isRequest);
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}
