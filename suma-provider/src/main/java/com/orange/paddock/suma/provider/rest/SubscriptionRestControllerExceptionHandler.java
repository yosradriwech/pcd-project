package com.orange.paddock.suma.provider.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.provider.rest.model.SumaError;

@ControllerAdvice
public class SubscriptionRestControllerExceptionHandler {

	@ExceptionHandler(value = AbstractSumaException.class)
	public ResponseEntity<SumaError> handleInteralException(HttpServletRequest req, AbstractSumaException e) throws Exception {
		
		SumaError error = new SumaError();
		error.setErrorDescription(e.getErrorDescription());
		error.setInternalErrorCode(e.getInternalErrorCode());
		error.setErrorCode(e.getErrorCode());
		error.setHttpStatusCode(e.getHttpStatusCode());

		// TODO LOG EXCEPTION HERE
		
		return new ResponseEntity<SumaError>(error, HttpStatus.BAD_REQUEST);
	}

}
