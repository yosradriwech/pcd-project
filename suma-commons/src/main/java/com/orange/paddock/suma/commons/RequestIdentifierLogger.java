package com.orange.paddock.suma.commons;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class RequestIdentifierLogger {
	
	private String pdkInternalId;

	public RequestIdentifierLogger() {}

	public RequestIdentifierLogger(String pdkInternalId) {
		this.pdkInternalId = pdkInternalId;
	}

	public String getPdkInternalId() {
		return pdkInternalId;
	}

	public void setPdkInternalId(String pdkInternalId) {
		this.pdkInternalId = pdkInternalId;
	}
	
}