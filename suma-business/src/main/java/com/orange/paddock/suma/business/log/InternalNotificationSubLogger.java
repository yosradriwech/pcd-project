package com.orange.paddock.suma.business.log;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.paddock.commons.log.PdkLogUtils;

public class InternalNotificationSubLogger {
	
	private final static Logger LOGGER = LoggerFactory.getLogger("internal-notification-sub-logger");
	
	public enum NotifSubFields {
		INTERNAL_ID,
		START_PROCESS_TIMESTAMP,
		END_PROCESS_TIMESTAMP,
		SUBSCRIPTION_ID,
		INTERNAL_ERROR_CODE,
		INTERNAL_ERROR_DESCRIPTION
	}
	
	public static void write(Map<NotifSubFields, String> logs) {
		StringBuilder logLine = new StringBuilder();
		int i = 0;

		for (NotifSubFields field : NotifSubFields.values()) {
			if (Objects.isNull(logs.get(field)) || logs.get(field).equals("null")) {
				logs.replace(field, "");
			}
			logLine.append(PdkLogUtils.formatValue(logs.get(field)));

			if (i < NotifSubFields.values().length - 1) {
				logLine.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
			}
			i++;
		}
		LOGGER.info(logLine.toString());
	}

}
