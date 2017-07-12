package com.orange.paddock.suma.provider.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.paddock.commons.log.PdkLogUtils;

public class NorthSubscriptionLogger extends AbstractLogger {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(NORTH_SUBSCRIPTION_LOGGER);
	
	public void write(LogFields logFields) {
		StringBuilder message = new StringBuilder();
		message.append(PdkLogUtils.formatValue(logFields.getInternalId()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getRequestTimestamp()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getResponseTimestamp()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);

		message.append(PdkLogUtils.formatValue(logFields.getServiceId()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getOnBehalfOf()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getEndUserId()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getMsisdn()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getOrangeApiToken()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getIse2()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getMco()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getDescription()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);

		message.append(PdkLogUtils.formatValue(logFields.getCategoryCode()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getAmount()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getTaxedAmount()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getCurrency()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getIsAdult()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);

		message.append(PdkLogUtils.formatValue(logFields.getSubscriptionId()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getReturnedSubscriptionId()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);

		message.append(PdkLogUtils.formatValue(logFields.getHttpResponseCode()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getReturnedErrorCode()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getReturnedErrorMessage()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getReturnedErrorDescription()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getInternalErrorCode()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getInternalErrorDescription()));

		LOGGER.info(message.toString());
	}

}
