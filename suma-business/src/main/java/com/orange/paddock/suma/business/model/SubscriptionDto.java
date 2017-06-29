package com.orange.paddock.suma.business.model;

import java.math.BigDecimal;
import java.util.Date;

public class SubscriptionDto {
	
	private String id;

	private String subscriptionId;

	private Date creationDate;

	private Date activationDate;
	private Date deActivationDate;
	private String transactionId;
	private String serviceId;
	private String onBehalfOf;
	private String endUserId;
	private String description;
	private String categoryCode;
	private BigDecimal amount;
	private BigDecimal taxedAmount;
	private String currency;
	private boolean isAdult;
	private String status;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public boolean isAdult() {
		return isAdult;
	}
	public void setAdult(boolean isAdult) {
		this.isAdult = isAdult;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
