package com.orange.paddock.suma.dao.mongodb.document;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Subscription {

	@Id
	private String id;

	@Indexed
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
	private BigDecimal amount;
	private BigDecimal taxedAmount;
	private String currency;
	private boolean isAdult;
	private String status;

	public Subscription() {}

	public Subscription(String id, String subscriptionId, Date creationDate, Date activationDate, Date deActivationDate, String transactionId,
			String serviceId, String onBehalfOf, String endUserId, String description, String categoryCode, BigDecimal amount, BigDecimal taxedAmount,
			String currency, boolean isAdult, String status) {
		super();
		this.id = id;
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
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@Override
	public String toString() {
		return "Subscription [id=" + id + ", subscriptionId=" + subscriptionId + ", creationDate=" + creationDate + ", activationDate="
				+ activationDate + ", deActivationDate=" + deActivationDate + ", transactionId=" + transactionId + ", serviceId=" + serviceId
				+ ", onBehalfOf=" + onBehalfOf + ", endUserId=" + endUserId + ", description=" + description + ", categoryCode=" + categoryCode
				+ ", amount=" + amount + ", taxedAmount=" + taxedAmount + ", currency=" + currency + ", isAdult=" + isAdult + ", status=" + status
				+ "]";
	}

	

}
