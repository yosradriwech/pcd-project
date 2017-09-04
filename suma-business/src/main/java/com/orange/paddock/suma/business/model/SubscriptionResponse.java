package com.orange.paddock.suma.business.model;

public class SubscriptionResponse {
	
	private String transactionId;
	private String ccgwSubscriptionId;
	private String msisdn;
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String subscriptionId) {
		this.transactionId = subscriptionId;
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
