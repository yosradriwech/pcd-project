package com.orange.paddock.suma.business.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;

@Service
public class SubscriptionManager {
	
	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(SubscriptionManager.class);
	
	@Value("${orange.wtpapi.default.serv}")
	private String wtDefaultService;
	
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	
	public String subscribe(SubscriptionDto subscriptionDto, String endUserIdValue) {
		//Validate inputs else PDK_SUMA_0001
		
		//call wt to get MSISDN : endUserId acr:X-Orange-ISE2, acr:OrangeAPIToken or tel+...
		
		//
		
		return null;
	}

	
	
	
	
	public String unsubscribe(String subscriptionId) {
		
		TECHNICAL_LOGGER.debug("Unsubscribe for the subscriptionId '{}'", subscriptionId);

		Subscription subscription = subscriptionRepository.findOne(subscriptionId);
		
		TECHNICAL_LOGGER.debug("Unsubscribe to service '{}' for endUser '{}'", subscription.getServiceId(), subscription.getEndUserId());

		return null;

	}	
	public SubscriptionDto getSubscriptionStatus(String subscriptionId){
		
		TECHNICAL_LOGGER.debug("Get subscription status for the subscriptionId '{}'", subscriptionId);
		
		Subscription subscription = subscriptionRepository.findOne(subscriptionId);
		
		return null;		
	}
	
	
	// TODO Mapping SubscriptionDTO --> Subscription
	// TODO Mappign Subscription --> SubscriptionDTO

}
