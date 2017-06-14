package com.orange.paddock.suma.dao.mongodb.document;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Subscription {

	@Indexed
	@Id
	private String subscriptionId;

	@CreatedDate
	private Date creationDate;

	private Date activationDate;
	private Date deActivationDate;
	private String transactionId;
	private String serviceId;
	private String onBehalfOf;
	private String endUserId;
	private String description;
	private String categoryCode;
	private Double amount;
	private Double taxedAmount;
	private String currency;
	private boolean isAdult;
	private String status;

	public Subscription() {
	}

	public Subscription(String subscriptionId, Date creationDate, Date activationDate, Date deActivationDate, String transactionId, String serviceId,
			String onBehalfOf, String endUserId, String description, String categoryCode, Double amount, Double taxedAmount, String currency, boolean isAdult) {
		super();
		this.subscriptionId = subscriptionId;
		this.creationDate = creationDate;
		this.activationDate = activationDate;
		this.deActivationDate = deActivationDate;
		this.transactionId = transactionId;
		this.serviceId = serviceId;
		this.onBehalfOf = onBehalfOf;
		this.endUserId = endUserId;
		this.description = description;
		this.categoryCode = categoryCode;
		this.amount = amount;
		this.taxedAmount = taxedAmount;
		this.currency = currency;
		this.isAdult = isAdult;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getDeActivationDate() {
		return deActivationDate;
	}

	public void setDeActivationDate(Date deActivationDate) {
		this.deActivationDate = deActivationDate;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getOnBehalfOf() {
		return onBehalfOf;
	}

	public void setOnBehalfOf(String onBehalfOf) {
		this.onBehalfOf = onBehalfOf;
	}

	public String getEndUserId() {
		return endUserId;
	}

	public void setEndUserId(String endUserId) {
		this.endUserId = endUserId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
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

	public boolean isAdult() {
		return isAdult;
	}

	public void setAdult(boolean isAdult) {
		this.isAdult = isAdult;
	}

	@Override
	public String toString() {
		return "Subscription [subscriptionId=" + subscriptionId + ", creationDate=" + creationDate + ", activationDate=" + activationDate
				+ ", deActivationDate=" + deActivationDate + ", transactionId=" + transactionId + ", serviceId=" + serviceId + ", onBehalfOf=" + onBehalfOf
				+ ", endUserId=" + endUserId + ", description=" + description + ", categoryCode=" + categoryCode + ", amount=" + amount + ", taxedAmount="
				+ taxedAmount + ", currency=" + currency + ", isAdult=" + isAdult + "]";
	};

}
