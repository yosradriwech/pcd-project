package com.pcd.rest.provider.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.pcd.rest.manager.exception.AbstractPcdException;

@ControllerAdvice(annotations = RestController.class)

public class SubscriptionRestControllerExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRestControllerExceptionHandler.class);
	
	@ExceptionHandler(value = AbstractPcdException.class)
	public ResponseEntity<PcdError> handleSumaException(HttpServletRequest req, AbstractPcdException e) {
		
		PcdError error = new PcdError();
		error.setCode(String.valueOf(e.getErrorCode()));
		error.setMessage(e.getMessage());
		error.setDescription(e.getErrorDescription());

		return new ResponseEntity<PcdError>(error, HttpStatus.valueOf(e.getHttpStatusCode()));
	}

}
