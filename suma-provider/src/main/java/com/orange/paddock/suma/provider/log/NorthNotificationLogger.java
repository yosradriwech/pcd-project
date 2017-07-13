package com.orange.paddock.suma.provider.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.paddock.commons.log.PdkLogUtils;

public class NorthNotificationLogger {
	
	private final static Logger LOGGER = LoggerFactory.getLogger("north-notification-logger");
	
	private String internalId;
	private String requestTimestamp;
	private String responseTimestamp;
	private String notificationType;
	private String requestId;
	private String subscriptionId;
	private String subscriber;
	private String activationDate;
	private String assentForActivation;
	private String httpResponseCode;
	private String returnedResponseStatus;
	
	public void write() {
		
		StringBuilder message = new StringBuilder();
		message.append(PdkLogUtils.formatValue(internalId));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(requestTimestamp));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(responseTimestamp));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(notificationType));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(requestId));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);

		message.append(PdkLogUtils.formatValue(subscriptionId));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(subscriber));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(activationDate));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(assentForActivation));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(httpResponseCode));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(returnedResponseStatus));

		LOGGER.info(message.toString());
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	public void setRequestTimestamp(String requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}

	public void setResponseTimestamp(String responseTimestamp) {
		this.responseTimestamp = responseTimestamp;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}

	public void setActivationDate(String activationDate) {
		this.activationDate = activationDate;
	}

	public void setAssentForActivation(String assentForActivation) {
		this.assentForActivation = assentForActivation;
	}

	public void setHttpResponseCode(String httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}

	public void setReturnedResponseStatus(String returnedResponseStatus) {
		this.returnedResponseStatus = returnedResponseStatus;
	}

}
