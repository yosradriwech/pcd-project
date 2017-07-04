package com.orange.paddock.suma.business.exception.notification;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class SumaUnsubNotifStillActiveException extends AbstractSumaException{
	
	private static final long serialVersionUID = 1L;

	public SumaUnsubNotifStillActiveException() {
		
		super("Unsubscription notification: subscription is still active");
		internalErrorCode = INTERNAL_SUMA_0007_CODE;
		errorDescription = "Unsubscription notification error: trying to confirm a subscription that is still active";
	}

}
