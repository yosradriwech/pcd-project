package com.orange.paddock.suma.provider.soap;

import javax.jws.WebMethod;
import javax.jws.WebResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.orange.paddock.suma.business.manager.NotificationManager;
import com.orange.paddock.suma.provider.soap.model.NotificationPortType;
import com.orange.paddock.suma.provider.soap.model.Status;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationResponseType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationResponseType;

public class NotificationSoapController implements NotificationPortType {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(NotificationSoapController.class);
	
	@Autowired
	private NotificationManager manager;
	
	@Override
	@WebMethod(operationName = "SubscriptionNotification")
    @WebResult(name = "activate-subscription-response", targetNamespace = "http://ccgw.vas.gtp.pl/service/NotificationService/v10", partName = "parameters")
	public SubscriptionNotificationResponseType subscriptionNotification(SubscriptionNotificationRequestType parameters) {

		// TODO CALL BUSINESS 
		SubscriptionNotificationResponseType response = new SubscriptionNotificationResponseType();
		
		Status status = new Status();
		status.setSuccess(true);
		response.setStatus(status);
		
		manager.notificationSubscription(parameters.getSubscriptionId());

		return response;
	}
	
	@Override
	@WebMethod(operationName = "UnsubscriptionNotification")
    @WebResult(name = "deactivate-subscription-response", targetNamespace = "http://ccgw.vas.gtp.pl/service/NotificationService/v10", partName = "parameters")
	public UnsubscriptionNotificationResponseType unsubscriptionNotification(
			UnsubscriptionNotificationRequestType parameters) {
		
		// TODO CALL BUSINESS
		UnsubscriptionNotificationResponseType response = new UnsubscriptionNotificationResponseType();
		
		Status status = new Status();
		status.setSuccess(true);
		response.setStatus(status);
		
		manager.notificationUnsubscription(parameters.getSubscriptionId());

		return response;
	}



}
