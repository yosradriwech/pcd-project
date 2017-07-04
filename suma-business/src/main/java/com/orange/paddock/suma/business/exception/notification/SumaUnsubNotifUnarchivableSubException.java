package com.orange.paddock.suma.business.exception.notification;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class SumaUnsubNotifUnarchivableSubException extends AbstractSumaException {

	private static final long serialVersionUID = 1L;

	public SumaUnsubNotifUnarchivableSubException() {
		super("Unsubscription notification: subscription cannot be archived");
		internalErrorCode = INTERNAL_SUMA_0005_CODE;
		errorDescription = "Unsubscription notification error: trying to archive a subscription that has been revoked";
	}
}
