package com.orange.paddock.suma.dao.mongodb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.orange.paddock.suma.dao.mongodb.document.Subscription;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

	Subscription findOneBySubscriptionId(String subscriptionId);

	List<Subscription> findByEndUserIdAndStatus(String endUserId, String status);
	
	List<Subscription> findByStatusIn(List<String> status);

	Subscription findOneBySubscriptionIdAndEndUserId(String subscriptionId, String endUserId);

	Subscription findOneByTransactionId(String transactionId);

	Subscription findOneByEndUserIdAndServiceIdAndOnBehalfOfAndCategoryCodeAndDescription(String endUserId, String serviceId, String onBehalfOf,
			String categoryCode, String description);

}
