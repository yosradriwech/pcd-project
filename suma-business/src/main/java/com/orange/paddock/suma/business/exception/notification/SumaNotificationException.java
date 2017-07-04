package com.orange.paddock.suma.business.exception.notification;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public class SumaNotificationException extends AbstractSumaException {

	private static final long serialVersionUID = 1L;

	public SumaNotificationException() {
		super("Notification error");
		internalErrorCode = INTERNAL_SUMA_0004_CODE;
		errorDescription = "Notification error: unknown received subscription identifier";
	}
}
