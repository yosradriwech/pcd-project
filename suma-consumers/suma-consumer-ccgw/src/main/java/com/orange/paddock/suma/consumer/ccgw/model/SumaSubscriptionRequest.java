package com.orange.paddock.suma.consumer.ccgw.model;

public class SumaSubscriptionRequest {

	private String providerId;
	private String saleProviderId;
	private String subscriber;
	private String contentName;
	private String contentType;
	private String adultFlag;
	private Double amount;
	private Double taxedAmount;
	private String currency;
	
	public SumaSubscriptionRequest() {
		super();
	}
	
	public SumaSubscriptionRequest(String providerId, String saleProviderId, String subscriber, String contentName, String contentType,
			String adultFlag, Double amount, Double taxedAmount, String currency) {
		super();
		this.providerId = providerId;
		this.saleProviderId = saleProviderId;
		this.subscriber = subscriber;
		this.contentName = contentName;
		this.contentType = contentType;
		this.adultFlag = adultFlag;
		this.amount = amount;
		this.taxedAmount = taxedAmount;
		this.currency = currency;
	}
	
	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public String getSaleProviderId() {
		return saleProviderId;
	}
	public void setSaleProviderId(String saleProviderId) {
		this.saleProviderId = saleProviderId;
	}
	public String getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}
	public String getContentName() {
		return contentName;
	}
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getAdultFlag() {
		return adultFlag;
	}
	public void setAdultFlag(String adultFlag) {
		this.adultFlag = adultFlag;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getTaxedAmount() {
		return taxedAmount;
	}
	public void setTaxedAmount(Double taxedAmount) {
		this.taxedAmount = taxedAmount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@Override
	public String toString() {
		return "SumaSubscriptionRequest [providerId=" + providerId + ", saleProviderId=" + saleProviderId + ", subscriber=" + subscriber
				+ ", contentName=" + contentName + ", contentType=" + contentType + ", adultFlag=" + adultFlag + ", amount=" + amount
				+ ", taxedAmount=" + taxedAmount + ", currency=" + currency + "]";
	}
	
	
}
