package com.orange.paddock.suma.provider.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.SumaInternalErrorException;
import com.orange.paddock.suma.provider.rest.model.SumaError;

@ControllerAdvice
public class SubscriptionRestControllerExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRestControllerExceptionHandler.class);
	
	@ExceptionHandler(value = AbstractSumaException.class)
	public ResponseEntity<SumaError> handleSumaException(HttpServletRequest req, AbstractSumaException e) {
		
		SumaError error = new SumaError();
		error.setCode(String.valueOf(e.getErrorCode()));
		error.setMessage(e.getMessage());
		error.setDescription(e.getErrorDescription());

		// TODO LOG EXCEPTION HERE
		
		return new ResponseEntity<SumaError>(error, HttpStatus.valueOf(e.getHttpStatusCode()));
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<SumaError> handleInternalException(HttpServletRequest req, Exception e) {
		
		LOGGER.error("Unknown internal error", e);
		
		SumaInternalErrorException ex = new SumaInternalErrorException();
		
		SumaError error = new SumaError();
		error.setCode(String.valueOf(ex.getErrorCode()));
		error.setMessage(ex.getMessage());
		error.setDescription(ex.getErrorDescription());

		// TODO LOG EXCEPTION HERE
		
		return new ResponseEntity<SumaError>(error, HttpStatus.valueOf(ex.getHttpStatusCode()));
	}
}
