package com.orange.paddock.suma.business.model;

public class SubscriptionResponse {
	
	private String subscriptionId;
	private String ccgwSubscriptionId;
	private String msisdn;
	
	public String getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public String getCcgwSubscriptionId() {
		return ccgwSubscriptionId;
	}
	public void setCcgwSubscriptionId(String ccgwSubscriptionId) {
		this.ccgwSubscriptionId = ccgwSubscriptionId;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
}
