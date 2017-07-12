package com.orange.paddock.suma.provider.log;

import java.util.List;

import com.orange.paddock.commons.date.PdkDateUtils;
import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class LoggerManager {
	
	private List<AbstractLogger> loggers;
	
	public void write(LogFields logFields, AbstractSumaException ex) {
		
		logFields.setInternalErrorCode(ex.getInternalErrorCode());
		logFields.setInternalErrorDescription(ex.getErrorDescription());
		logFields.setHttpResponseCode(String.valueOf(ex.getHttpStatusCode()));
		logFields.setReturnedErrorCode(ex.getErrorCode());
		write(logFields);
	}

	public void write(LogFields logFields) {
		logFields.setResponseTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		for (AbstractLogger logger : loggers) {
			logger.write(logFields);
		}
	}

	public List<AbstractLogger> getLoggers() {
		return loggers;
	}

	public void setLoggers(List<AbstractLogger> loggers) {
		this.loggers = loggers;
	}

}
