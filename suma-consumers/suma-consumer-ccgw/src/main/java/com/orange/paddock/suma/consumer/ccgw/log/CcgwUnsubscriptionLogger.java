package com.orange.paddock.suma.consumer.ccgw.log;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.paddock.commons.log.PdkLogUtils;

public class CcgwUnsubscriptionLogger {

	private final static Logger LOGGER = LoggerFactory.getLogger("ccgw-subscription-logger");
	
	public enum CcgwUnsubscriptionFields {
		INTERNAL_ID,
		REQUEST_TIMESTAMP,
		RESPONSE_TIMESTAMP,
		TIMESTAMP,
		PROVIDER_ID,
		PROVIDER_PASS,
		SUBSCRIPTION_ID,
		SUBSCRIBER,
		VAS_SIGNATURE,
		CCGW_HTTP_RESPONSE_CODE,
		CCGW_RESPONSE_STATUS,
		CCGW_ERROR_CODE,
		CCGW_ERROR_PARAMS	
	}
	
	public static void write(Map<CcgwUnsubscriptionFields, String> logs) {
		StringBuilder logLine = new StringBuilder();
		int i = 0;
		for(CcgwUnsubscriptionFields field : CcgwUnsubscriptionFields.values()) {
			logLine.append(PdkLogUtils.formatValue(logs.get(field)));
					
			if(i<CcgwUnsubscriptionFields.values().length-1) {
				logLine.append(PdkLogUtils.LOG_FIELD_SEPARATOR);
			}
			i++;
		}
		LOGGER.info(logLine.toString());
	}
}
