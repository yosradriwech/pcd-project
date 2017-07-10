package com.orange.paddock.suma.business.exception;

public class SumaAlreadyRevokedUnSubException extends AbstractSumaException {

	private static final long serialVersionUID = 3215931049489448202L;
	
	public SumaAlreadyRevokedUnSubException() {
		super("Unsubscription already revoked");
		internalErrorCode = INTERNAL_SUMA_0005_CODE;
		
		errorDescription = "Unsubscription notification error: trying to archive a subscription that has been revoked";
	}
	
	public SumaAlreadyRevokedUnSubException(String subscriptionId) {
		super("Unsubscription already revoked");
		internalErrorCode = INTERNAL_SUMA_0005_CODE;
		
		errorDescription = new StringBuilder().append("Unsubscription notification error: trying to archive a subscription that has been revoked: ").append(subscriptionId).toString();
	}

}
