package com.orange.paddock.suma.dao.mongodb.document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
@CompoundIndex(name = "subId_userId", def = "{'subscriptionId' : 1, 'endUserId' : 1}", unique = true)
public class Subscription {

	@Id
	private String id;

	@Field("subscriptionId")
	private String subscriptionId;

	@CreatedDate
	private Date creationDate;

	private Date activationDate;
	private Date deActivationDate;
	private String transactionId;
	private String serviceId;
	private String onBehalfOf;

	@Field("endUserId")
	private String endUserId;

	private String description;
	private String categoryCode;
	private BigDecimal amount;
	private BigDecimal taxedAmount;
	private String currency;
	private boolean isAdult;
	private String status;

	public Subscription() {
	}

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activationDate == null) ? 0 : activationDate.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((categoryCode == null) ? 0 : categoryCode.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((deActivationDate == null) ? 0 : deActivationDate.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((endUserId == null) ? 0 : endUserId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isAdult ? 1231 : 1237);
		result = prime * result + ((onBehalfOf == null) ? 0 : onBehalfOf.hashCode());
		result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((subscriptionId == null) ? 0 : subscriptionId.hashCode());
		result = prime * result + ((taxedAmount == null) ? 0 : taxedAmount.hashCode());
		result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object subscriptionObjToCompareTo) {

		if (subscriptionObjToCompareTo == null)
			return false;
		if (getClass() != subscriptionObjToCompareTo.getClass())
			return false;

		Subscription subscriptionToCompareTo = (Subscription) subscriptionObjToCompareTo;
		if (activationDate == null) {
			if (subscriptionToCompareTo.activationDate != null)
				return false;
		} else if (!activationDate.equals(subscriptionToCompareTo.activationDate))
			return false;
		if (amount == null) {
			if (subscriptionToCompareTo.amount != null)
				return false;
		} else if (!amount.equals(subscriptionToCompareTo.amount))
			return false;
		if (categoryCode == null) {
			if (subscriptionToCompareTo.categoryCode != null)
				return false;
		} else if (!categoryCode.equals(subscriptionToCompareTo.categoryCode))
			return false;
		if (creationDate == null) {
			if (subscriptionToCompareTo.creationDate != null)
				return false;
		} else if (!creationDate.equals(subscriptionToCompareTo.creationDate))
			return false;
		if (currency == null) {
			if (subscriptionToCompareTo.currency != null)
				return false;
		} else if (!currency.equals(subscriptionToCompareTo.currency))
			return false;
		if (deActivationDate == null) {
			if (subscriptionToCompareTo.deActivationDate != null)
				return false;
		} else if (!deActivationDate.equals(subscriptionToCompareTo.deActivationDate))
			return false;
		if (description == null) {
			if (subscriptionToCompareTo.description != null)
				return false;
		} else if (!description.equals(subscriptionToCompareTo.description))
			return false;
		if (endUserId == null) {
			if (subscriptionToCompareTo.endUserId != null)
				return false;
		} else if (!endUserId.equals(subscriptionToCompareTo.endUserId))
			return false;
		if (id == null) {
			if (subscriptionToCompareTo.id != null)
				return false;
		} else if (!id.equals(subscriptionToCompareTo.id))
			return false;
		if (isAdult != subscriptionToCompareTo.isAdult)
			return false;
		if (onBehalfOf == null) {
			if (subscriptionToCompareTo.onBehalfOf != null)
				return false;
		} else if (!onBehalfOf.equals(subscriptionToCompareTo.onBehalfOf))
			return false;
		if (serviceId == null) {
			if (subscriptionToCompareTo.serviceId != null)
				return false;
		} else if (!serviceId.equals(subscriptionToCompareTo.serviceId))
			return false;
		if (status == null) {
			if (subscriptionToCompareTo.status != null)
				return false;
		} else if (!status.equals(subscriptionToCompareTo.status))
			return false;
		if (subscriptionId == null) {
			if (subscriptionToCompareTo.subscriptionId != null)
				return false;
		} else if (!subscriptionId.equals(subscriptionToCompareTo.subscriptionId))
			return false;
		if (taxedAmount == null) {
			if (subscriptionToCompareTo.taxedAmount != null)
				return false;
		} else if (!taxedAmount.equals(subscriptionToCompareTo.taxedAmount))
			return false;
		if (transactionId == null) {
			if (subscriptionToCompareTo.transactionId != null)
				return false;
		} else if (!transactionId.equals(subscriptionToCompareTo.transactionId))
			return false;

		return Objects.equals(id, subscriptionToCompareTo.getId()) && Objects.equals(subscriptionId, subscriptionToCompareTo.getSubscriptionId())
				&& Objects.equals(creationDate, subscriptionToCompareTo.getCreationDate())
				&& Objects.equals(activationDate, subscriptionToCompareTo.getActivationDate())
				&& Objects.equals(deActivationDate, subscriptionToCompareTo.getDeActivationDate())
				&& Objects.equals(transactionId, subscriptionToCompareTo.getTransactionId())
				&& Objects.equals(serviceId, subscriptionToCompareTo.getServiceId()) && Objects.equals(onBehalfOf, subscriptionToCompareTo.getOnBehalfOf())
				&& Objects.equals(endUserId, subscriptionToCompareTo.getEndUserId()) && Objects.equals(description, subscriptionToCompareTo.getDescription())
				&& Objects.equals(categoryCode, subscriptionToCompareTo.getCategoryCode()) && Objects.equals(amount, subscriptionToCompareTo.getAmount())
				&& Objects.equals(taxedAmount, subscriptionToCompareTo.getTaxedAmount()) && Objects.equals(currency, subscriptionToCompareTo.getCurrency())
				&& isAdult == subscriptionToCompareTo.isAdult && Objects.equals(status, subscriptionToCompareTo.getStatus());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Subscription [id=").append(id).append(", subscriptionId=").append(subscriptionId).append(", creationDate=").append(creationDate)
				.append(", activationDate=").append(activationDate).append(", deActivationDate=").append(deActivationDate).append(", transactionId=")
				.append(transactionId).append(", serviceId=" + serviceId).append(", onBehalfOf=").append(onBehalfOf).append(", endUserId=").append(endUserId)
				.append(", description=").append(description + ", categoryCode=").append(categoryCode).append(", amount=").append(amount)
				.append(", taxedAmount=").append(taxedAmount).append(", currency=").append(currency).append(", isAdult=").append(isAdult).append(", status=")
				.append(status).append("]");
		return sb.toString();
	}

}
