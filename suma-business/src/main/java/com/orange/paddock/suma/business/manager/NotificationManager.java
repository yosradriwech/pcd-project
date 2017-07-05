package com.orange.paddock.suma.business.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationManager {
	
	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(NotificationManager.class);
	
//	@Async("subscriptionNotificationExecutor")
	public String notificationSubscription(String subscriptionId){
		TECHNICAL_LOGGER.debug("Starting asynchronuous subscription notification task");
		
		return null;
	}
	
//	@Async("unsubscriptionNotificationExecutor")
	public String notificationUnsubscription(String subscriptionId){
		TECHNICAL_LOGGER.debug("Starting asynchronuous UNsubscription notification task");
		
		return null;
	}

}
