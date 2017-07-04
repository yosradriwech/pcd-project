package com.orange.paddock.suma.business.exception.notification;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class SumaAlreadyActiveSubNotificationException extends AbstractSumaException{
	
	private static final long serialVersionUID = 1L;

	public SumaAlreadyActiveSubNotificationException() {
		
		super("Subscription notification: subscription is already active");
		internalErrorCode = INTERNAL_SUMA_0006_CODE;
		errorDescription = "Subscription notification error: trying to active a subscription that is already active";
	}

}
