package com.orange.paddock.suma.consumer.ccgw.log;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.paddock.commons.log.PdkLogUtils;

public class CcgwSubscriptionLogger {

	private final static Logger LOGGER = LoggerFactory.getLogger("ccgw-subscription-logger");

	public enum CcgwSubscriptionFields {
		INTERNAL_ID, REQUEST_TIMESTAMP, RESPONSE_TIMESTAMP, TIMESTAMP, PROVIDER_ID, PROVIDER_PASS, SUBSCRIPTION_ID, SALE_PROVIDER_ID, TRANSACTION_ID, SUBSCRIBER, AUTHENTICATION_METHOD, AUTHORIZATION_TYPE, CONTENT_NAME, CONTENT_TYPE, RATING_LEVEL, ADULT_FLAG, SUBSCRIPTION_MODEL, SALE_MODEL, AMOUNT, TAXED_AMOUNT, CURRENCY, VAS_SIGNATURE, CCGW_HTTP_RESPONSE_CODE, CCGW_RESPONSE_STATUS, CCGW_ERROR_CODE, CCGW_ERROR_PARAMS
	}

	public static void write(Map<CcgwSubscriptionFields, String> logs) {
		StringBuilder logLine = new StringBuilder();
		int i = 0;

		for (CcgwSubscriptionFields field : CcgwSubscriptionFields.values()) {
			if (Objects.isNull(logs.get(field)) || logs.get(field).equals("null")) {
				logs.replace(field, "");
			}
			logLine.append(PdkLogUtils.formatValue(logs.get(field)));

			if (i < CcgwSubscriptionFields.values().length - 1) {
				logLine.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
			}
			i++;
		}
		LOGGER.info(logLine.toString());
	}
}
