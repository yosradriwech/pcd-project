package com.orange.paddock.suma.business.exception;

public class SumaAlreadyActiveSubscriptionException extends AbstractSumaException{

	private static final long serialVersionUID = 3296019363872559128L;
	
	public SumaAlreadyActiveSubscriptionException() {
		super("Subscription already Active");
		internalErrorCode = INTERNAL_SUMA_0006_CODE;
		
		errorDescription = "Subscription notification error: trying to active a subscription that is already active";
	}
	
	public SumaAlreadyActiveSubscriptionException(String subscriptionId){
		super("Subscription already Active");
		internalErrorCode = INTERNAL_SUMA_0006_CODE;
		
		errorDescription = new StringBuilder().append("Subscription notification error: trying to active a subscription that is already active: ").append(subscriptionId).toString();
	}
	
}
