package com.orange.paddock.suma.provider.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.paddock.commons.log.PdkLogUtils;

public class NorthGetSubUnsubStatusLogger {

	private final static Logger LOGGER = LoggerFactory.getLogger("north-get-status-logger");

	private String internalId;
	private String requestTimestamp;
	private String responseTimestamp;
	private String subscriptionId;
	private String creationDate;
	private String transactionId;
	private String serviceId;
	private String onBehalfOf;
	private String endUserId;
	private String description;
	private String categoryCode;
	private String amount;
	private String taxedAmount;
	private String currency;
	private String isAdult;
	private String status;
	private String httpResponseCode;
	private String returnedErrorCode;
	private String returnedErrorMessage;
	private String returnedErrorDescription;
	private String internalErrorCode = "PDK_SUMA_OK";
	private String internalErrorDescription;
	private String returnedEncodedSubscriptionId;

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
		message.append(PdkLogUtils.formatValue(creationDate));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);

		message.append(PdkLogUtils.formatValue(transactionId));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(serviceId));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(onBehalfOf));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(endUserId));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(description));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(categoryCode));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(amount));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(taxedAmount));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(currency));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(isAdult));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(status));
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
		message.append(PdkLogUtils.formatValue(returnedEncodedSubscriptionId));
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

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public void setOnBehalfOf(String onBehalfOf) {
		this.onBehalfOf = onBehalfOf;
	}

	public void setEndUserId(String endUserId) {
		this.endUserId = endUserId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public void setTaxedAmount(String taxedAmount) {
		this.taxedAmount = taxedAmount;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setIsAdult(String isAdult) {
		this.isAdult = isAdult;
	}

	public void setStatus(String status) {
		this.status = status;
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
	public void setReturnedEncodedSubscriptionId(String returnedEncodedSubscriptionId) {
		this.returnedEncodedSubscriptionId = returnedEncodedSubscriptionId;
	}

}
