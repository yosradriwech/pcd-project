package com.orange.paddock.suma.business.exception;

public class SumaSubscriptionIsStillActiveException extends AbstractSumaException {

	private static final long serialVersionUID = -3611844599266507331L;
	
	public SumaSubscriptionIsStillActiveException() {
		super("Unsubscription already revoked");
		internalErrorCode = INTERNAL_SUMA_0007_CODE;
		
		errorDescription = "Unsubscription notification error: trying to confirm a subscription that is still active";
	}
	
	public SumaSubscriptionIsStillActiveException(String subscriptionId) {
		super("Unsubscription already revoked");
		internalErrorCode = INTERNAL_SUMA_0007_CODE;
		
		errorDescription = new StringBuilder().append("Unsubscription notification error: trying to confirm a subscription that is still active: ").append(subscriptionId).toString();
	}
	

}
