package com.orange.paddock.suma.provider.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.paddock.commons.log.PdkLogUtils;

public class NorthNotificationLogger extends AbstractLogger {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(NORTH_NOTIFICATION_LOGGER);
	
	public void write(LogFields logFields) {
		
		StringBuilder message = new StringBuilder();
		message.append(PdkLogUtils.formatValue(logFields.getInternalId()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getRequestTimestamp()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getResponseTimestamp()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getNotificationType()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getRequestId()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);

		message.append(PdkLogUtils.formatValue(logFields.getSubscriptionId()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getSubscriber()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getActivationDate()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getAssentForActivation()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getHttpResponseCode()));
		message.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
		message.append(PdkLogUtils.formatValue(logFields.getReturnedResponseStatus()));

		LOGGER.info(message.toString());
	}

}
