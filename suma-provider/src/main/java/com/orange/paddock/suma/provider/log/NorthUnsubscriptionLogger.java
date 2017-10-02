package com.orange.paddock.suma.provider.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.paddock.commons.log.PdkLogUtils;

public class NorthUnsubscriptionLogger {

	private final static Logger LOGGER = LoggerFactory.getLogger("north-unsubscription-logger");

	private String internalId;
	private String requestTimestamp;
	private String responseTimestamp;
	private String subscriptionId;
	private String idempotency;
	private String httpResponseCode;
	private String returnedErrorCode;
	private String returnedErrorMessage;
	private String returnedErrorDescription;
	private String internalErrorCode = "PDK_SUMA_OK";
	private String internalErrorDescription;
	private String sentSubscriptionId;


	public void write() {
		StringBuilder message = new StringBuilder();
		message.append(PdkLogUtils.formatValue(internalId));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(requestTimestamp));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(responseTimestamp));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(subscriptionId));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(idempotency));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(httpResponseCode));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(returnedErrorCode));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(returnedErrorMessage));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(returnedErrorDescription));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(internalErrorCode));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(internalErrorDescription));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(sentSubscriptionId));	

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

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public void setIdempotency(String idempotency) {
		this.idempotency = idempotency;
	}

	public void setHttpResponseCode(String httpResponseCode) {
		this.httpResponseCode = httpResponseCode;
	}

	public void setReturnedErrorCode(String returnedErrorCode) {
		this.returnedErrorCode = returnedErrorCode;
	}

	public void setReturnedErrorMessage(String returnedErrorMessage) {
		this.returnedErrorMessage = returnedErrorMessage;
	}

	public void setReturnedErrorDescription(String returnedErrorDescription) {
		this.returnedErrorDescription = returnedErrorDescription;
	}

	public void setInternalErrorCode(String internalErrorCode) {
		this.internalErrorCode = internalErrorCode;
	}

	public void setInternalErrorDescription(String internalErrorDescription) {
		this.internalErrorDescription = internalErrorDescription;
	}
	public String getSentSubscriptionId() {
		return sentSubscriptionId;
	}

	public void setSentSubscriptionId(String sentSubscriptionId) {
		this.sentSubscriptionId = sentSubscriptionId;
	}
}
