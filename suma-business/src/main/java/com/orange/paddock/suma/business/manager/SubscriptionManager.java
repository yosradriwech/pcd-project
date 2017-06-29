package com.orange.paddock.suma.business.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;

@Service
public class SubscriptionManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionManager.class);
	
	@Autowired
	private SubscriptionRepository repository;

	public String subscribe(SubscriptionDto body) {

//		LOGGER.debug("Subscribe to service '{}' for endUser '{}'", body.getServiceId(), body.getEndUserId());

		return null;
	}

	public String unsubscribe(String subscriptionId) {
		
		LOGGER.debug("Unsubscribe for the subscriptionId '{}'", subscriptionId);

		Subscription subscription = repository.findOne(subscriptionId);
		
		LOGGER.debug("Unsubscribe to service '{}' for endUser '{}'", subscription.getServiceId(), subscription.getEndUserId());

		return null;

	}
	
	public SubscriptionDto getSubscriptionStatus(String subscriptionId){
		
		LOGGER.debug("Get subscription status for the subscriptionId '{}'", subscriptionId);
		
		Subscription subscription = repository.findOne(subscriptionId);
		
		return null;		
	}
	
	
	// TODO Mapping SubscriptionDTO --> Subscription
	// TODO Mappign Subscription --> SubscriptionDTO

}
