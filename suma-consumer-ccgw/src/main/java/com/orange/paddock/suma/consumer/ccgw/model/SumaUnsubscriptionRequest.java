package com.orange.paddock.suma.consumer.ccgw.model;

import java.util.Objects;

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((providerId == null) ? 0 : providerId.hashCode());
		result = prime * result + ((subscriber == null) ? 0 : subscriber.hashCode());
		result = prime * result + ((subscriptionId == null) ? 0 : subscriptionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object unsubRequestObjToCompareTo) {

		if (unsubRequestObjToCompareTo == null)
			return false;
		if (getClass() != unsubRequestObjToCompareTo.getClass())
			return false;

		SumaUnsubscriptionRequest unsubRequestToCompareTo = (SumaUnsubscriptionRequest) unsubRequestObjToCompareTo;
		if (providerId == null) {
			if (unsubRequestToCompareTo.providerId != null)
				return false;
		} else if (!providerId.equals(unsubRequestToCompareTo.providerId))
			return false;
		if (subscriber == null) {
			if (unsubRequestToCompareTo.subscriber != null)
				return false;
		} else if (!subscriber.equals(unsubRequestToCompareTo.subscriber))
			return false;
		if (subscriptionId == null) {
			if (unsubRequestToCompareTo.subscriptionId != null)
				return false;
		} else if (!subscriptionId.equals(unsubRequestToCompareTo.subscriptionId))
			return false;
		return Objects.equals(providerId, unsubRequestToCompareTo.getProviderId())
				&& Objects.equals(subscriber, unsubRequestToCompareTo.getSubscriber())
				&& Objects.equals(subscriptionId, unsubRequestToCompareTo.getSubscriptionId());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SumaUnsubscriptionRequest [providerId=").append(providerId).append(", subscriptionId=").append(subscriptionId)
				.append(", subscriber=").append(subscriber).append("]");
		return sb.toString();
	}

}
