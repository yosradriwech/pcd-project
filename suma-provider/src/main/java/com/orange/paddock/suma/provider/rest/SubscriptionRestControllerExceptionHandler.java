package com.orange.paddock.suma.provider.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.provider.log.LogFields;
import com.orange.paddock.suma.provider.log.LoggerManager;
import com.orange.paddock.suma.provider.rest.model.SumaError;

@ControllerAdvice
public class SubscriptionRestControllerExceptionHandler {
	
	@Autowired
	private LoggerManager loggerManager;
	
	@Autowired
	private LogFields logFields;
	
	@ExceptionHandler(value = AbstractSumaException.class)
	public ResponseEntity<SumaError> handleInteralException(HttpServletRequest req, AbstractSumaException e) throws Exception {
		
		SumaError error = new SumaError();
		error.setErrorDescription(e.getErrorDescription());
		error.setInternalErrorCode(e.getInternalErrorCode());
		error.setErrorCode(e.getErrorCode());
		error.setHttpStatusCode(e.getHttpStatusCode());
		
		logFields.setHttpResponseCode(String.valueOf(e.getHttpStatusCode()));
		logFields.setInternalErrorCode(e.getInternalErrorCode());
		logFields.setInternalErrorDescription(e.getErrorDescription());
		logFields.setReturnedErrorCode(e.getErrorCode());
		
		loggerManager.write(logFields);
		
		// LOG EXCEPTION HERE
		
		return new ResponseEntity<SumaError>(error, HttpStatus.BAD_REQUEST);
	}

}
