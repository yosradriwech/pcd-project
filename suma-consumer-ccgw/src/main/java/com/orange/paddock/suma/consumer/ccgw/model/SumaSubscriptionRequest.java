package com.orange.paddock.suma.consumer.ccgw.model;

import java.math.BigDecimal;
import java.util.Objects;

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

	public SumaSubscriptionRequest() {}

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adultFlag == null) ? 0 : adultFlag.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((contentName == null) ? 0 : contentName.hashCode());
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((providerId == null) ? 0 : providerId.hashCode());
		result = prime * result + ((saleProviderId == null) ? 0 : saleProviderId.hashCode());
		result = prime * result + ((subscriber == null) ? 0 : subscriber.hashCode());
		result = prime * result + ((taxedAmount == null) ? 0 : taxedAmount.hashCode());
		result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object subRequestObjToCompareTo) {

		if (subRequestObjToCompareTo == null)
			return false;
		if (getClass() != subRequestObjToCompareTo.getClass())
			return false;

		SumaSubscriptionRequest subRequestToCompareTo = (SumaSubscriptionRequest) subRequestObjToCompareTo;
		if (adultFlag == null) {
			if (subRequestToCompareTo.adultFlag != null)
				return false;
		} else if (!adultFlag.equals(subRequestToCompareTo.adultFlag))
			return false;
		if (amount == null) {
			if (subRequestToCompareTo.amount != null)
				return false;
		} else if (!amount.equals(subRequestToCompareTo.amount))
			return false;
		if (contentName == null) {
			if (subRequestToCompareTo.contentName != null)
				return false;
		} else if (!contentName.equals(subRequestToCompareTo.contentName))
			return false;
		if (contentType == null) {
			if (subRequestToCompareTo.contentType != null)
				return false;
		} else if (!contentType.equals(subRequestToCompareTo.contentType))
			return false;
		if (currency == null) {
			if (subRequestToCompareTo.currency != null)
				return false;
		} else if (!currency.equals(subRequestToCompareTo.currency))
			return false;
		if (providerId == null) {
			if (subRequestToCompareTo.providerId != null)
				return false;
		} else if (!providerId.equals(subRequestToCompareTo.providerId))
			return false;
		if (saleProviderId == null) {
			if (subRequestToCompareTo.saleProviderId != null)
				return false;
		} else if (!saleProviderId.equals(subRequestToCompareTo.saleProviderId))
			return false;
		if (subscriber == null) {
			if (subRequestToCompareTo.subscriber != null)
				return false;
		} else if (!subscriber.equals(subRequestToCompareTo.subscriber))
			return false;
		if (taxedAmount == null) {
			if (subRequestToCompareTo.taxedAmount != null)
				return false;
		} else if (!taxedAmount.equals(subRequestToCompareTo.taxedAmount))
			return false;
		if (transactionId == null) {
			if (subRequestToCompareTo.transactionId != null)
				return false;
		} else if (!transactionId.equals(subRequestToCompareTo.transactionId))
			return false;

		return Objects.equals(transactionId, subRequestToCompareTo.transactionId)
				&& Objects.equals(providerId, subRequestToCompareTo.getProviderId())
				&& Objects.equals(saleProviderId, subRequestToCompareTo.getSaleProviderId())
				&& Objects.equals(subscriber, subRequestToCompareTo.getSubscriber())
				&& Objects.equals(contentName, subRequestToCompareTo.getContentName())
				&& Objects.equals(contentType, subRequestToCompareTo.getContentType())
				&& Objects.equals(adultFlag, subRequestToCompareTo.getAdultFlag())
				&& Objects.equals(amount, subRequestToCompareTo.getAmount())
				&& Objects.equals(taxedAmount, subRequestToCompareTo.getTaxedAmount())
				&& Objects.equals(currency, subRequestToCompareTo.getCurrency());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SumaSubscriptionRequest [transactionId=").append(transactionId).append(", providerId=").append(providerId)
				.append(", saleProviderId=").append(saleProviderId).append(", subscriber=").append(subscriber).append(", contentName=")
				.append(contentName).append(", contentType=").append(contentType).append(", adultFlag=").append(adultFlag).append(", amount=")
				.append(amount).append(", taxedAmount=").append(taxedAmount).append(", currency=").append(currency).append("]");
		return sb.toString();

	}
}
