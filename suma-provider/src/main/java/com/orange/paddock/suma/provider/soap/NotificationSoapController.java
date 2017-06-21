package com.orange.paddock.suma.provider.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.paddock.suma.provider.soap.model.NotificationPortType;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationResponseType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationResponseType;

public class NotificationSoapController implements NotificationPortType {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(NotificationSoapController.class);
	
	@Override
	public SubscriptionNotificationResponseType subscriptionNotification(SubscriptionNotificationRequestType parameters) {

		// TODO CALL BUSINESS 
		SubscriptionNotificationResponseType response = null;

		
		return response;
	}
	
	@Override
	public UnsubscriptionNotificationResponseType unsubscriptionNotification(
			UnsubscriptionNotificationRequestType parameters) {
		
		// TODO CALL BUSINESS
		UnsubscriptionNotificationResponseType response = null;

		return response;
	}


}
