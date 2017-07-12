package com.orange.paddock.suma.provider.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.paddock.commons.log.PdkLogUtils;

public class NorthUnsubscriptionLogger extends AbstractLogger {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(NORTH_UNSUBSCRIPTION_LOGGER);
	
	public void write(LogFields logFields) {
		StringBuilder message = new StringBuilder();
		message.append(PdkLogUtils.formatValue(logFields.getInternalId()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getRequestTimestamp()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getResponseTimestamp()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getSubscriptionId()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getIdempotency()));
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
