package com.orange.paddock.suma.consumer.ccgw.model;

import java.math.BigDecimal;

public class SumaSubscriptionRequest {

	private String transactionId;
	private String providerId;
	private String saleProviderId;
	private String subscriber;
	private String contentName;
	private String contentType;
	private Boolean adultFlag;
	private BigDecimal amount;
	private BigDecimal taxedAmount;
	private String currency;

	public SumaSubscriptionRequest() {
	}

	public SumaSubscriptionRequest(String transactionId, String providerId, String saleProviderId, String subscriber, String contentName,
			String contentType, Boolean adultFlag, BigDecimal amount, BigDecimal taxedAmount, String currency) {
		super();
		this.transactionId = transactionId;
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

	public Boolean getAdultFlag() {
		return adultFlag;
	}

	public void setAdultFlag(Boolean adultFlag) {
		this.adultFlag = adultFlag;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getTaxedAmount() {
		return taxedAmount;
	}

	public void setTaxedAmount(BigDecimal taxedAmount) {
		this.taxedAmount = taxedAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public String toString() {
		return "SumaSubscriptionRequest [transactionId=" + transactionId + ", providerId=" + providerId + ", saleProviderId=" + saleProviderId
				+ ", subscriber=" + subscriber + ", contentName=" + contentName + ", contentType=" + contentType + ", adultFlag=" + adultFlag
				+ ", amount=" + amount + ", taxedAmount=" + taxedAmount + ", currency=" + currency + "]";
	}

}
