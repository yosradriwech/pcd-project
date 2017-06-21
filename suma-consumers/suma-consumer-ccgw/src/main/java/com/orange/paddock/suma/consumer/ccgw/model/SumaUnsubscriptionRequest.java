package com.orange.paddock.suma.consumer.ccgw.model;

public class SumaUnsubscriptionRequest {

	private String providerId;
	private String subscriptionId;
	private String subscriber;

	public SumaUnsubscriptionRequest() {
	}

	public SumaUnsubscriptionRequest(String providerId, String subscriptionId, String subscriber) {
		super();
		this.providerId = providerId;
		this.subscriptionId = subscriptionId;
		this.subscriber = subscriber;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}

	@Override
	public String toString() {
		return "SumaUnsubscriptionRequest [providerId=" + providerId + ", subscriptionId=" + subscriptionId + ", subscriber=" + subscriber + "]";
	}

}
