package com.orange.paddock.suma.provider.soap;

import javax.jws.WebMethod;
import javax.jws.WebResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.orange.paddock.commons.date.PdkDateUtils;
import com.orange.paddock.commons.log.PdkLogIdBean;
import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.manager.NotificationManager;
import com.orange.paddock.suma.business.manager.SubscriptionStatusUtils;
import com.orange.paddock.suma.provider.log.LogFields;
import com.orange.paddock.suma.provider.log.LoggerManager;
import com.orange.paddock.suma.provider.soap.model.NotificationPortType;
import com.orange.paddock.suma.provider.soap.model.Status;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationResponseType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationResponseType;

public class NotificationSoapController implements NotificationPortType {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(NotificationSoapController.class);
	
	private static final String SUBSCRIPTION = "SUBSCRIPTION";
	private static final String UNSUBSCRIPTION = "UNSUBSCRIPTION";
	
	@Autowired
	private NotificationManager manager;
	
	@Autowired
	private LoggerManager loggerManager;
	
	@Autowired
	private LogFields logFields;
	
	@Autowired
	private PdkLogIdBean loggerId;
	
	@Override
	@WebMethod(operationName = "SubscriptionNotification")
    @WebResult(name = "activate-subscription-response", targetNamespace = "http://ccgw.vas.gtp.pl/service/NotificationService/v10", partName = "parameters")
	public SubscriptionNotificationResponseType subscriptionNotification(SubscriptionNotificationRequestType parameters) {

		SubscriptionNotificationResponseType response = new SubscriptionNotificationResponseType();
		Status status = new Status();
		status.setSuccess(true);
		response.setStatus(status);
		
		logFields.setInternalId(loggerId.getInternalId());
		logFields.setRequestTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		logFields.setNotificationType(SUBSCRIPTION);
		logFields.setRequestId(parameters.getRequestId());
		logFields.setSubscriptionId(parameters.getSubscriptionId());
		logFields.setSubscriber(parameters.getSubscriber());
		logFields.setActivationDate(String.valueOf(parameters.getActivationDate()));
		logFields.setAssentForActivation(String.valueOf(parameters.isAssentForActivation()));
		logFields.setHttpResponseCode(String.valueOf(HttpStatus.OK));
		logFields.setReturnedResponseStatus(String.valueOf(response.getStatus().isSuccess()));
		
		manager.notificationSubscription(parameters.getSubscriptionId(), parameters.getRequestId(), parameters.getActivationDate().toGregorianCalendar().getTime(), parameters.getSubscriber());

		logFields.setResponseTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		loggerManager.write(logFields);
		
		return response;
	}
	
	@Override
	@WebMethod(operationName = "UnsubscriptionNotification")
    @WebResult(name = "deactivate-subscription-response", targetNamespace = "http://ccgw.vas.gtp.pl/service/NotificationService/v10", partName = "parameters")
	public UnsubscriptionNotificationResponseType unsubscriptionNotification(
			UnsubscriptionNotificationRequestType parameters) {
		
		UnsubscriptionNotificationResponseType response = new UnsubscriptionNotificationResponseType();
		
		Status status = new Status();
		status.setSuccess(true);
		response.setStatus(status);
		
		logFields.setInternalId(loggerId.getInternalId());
		logFields.setRequestTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		logFields.setNotificationType(UNSUBSCRIPTION);
		logFields.setRequestId(parameters.getRequestId());
		logFields.setSubscriptionId(parameters.getSubscriptionId());
		logFields.setSubscriber(parameters.getSubscriber());
		logFields.setActivationDate(String.valueOf(parameters.getActivationDate()));
		logFields.setAssentForActivation(String.valueOf(parameters.isAssentForActivation()));
		logFields.setHttpResponseCode(String.valueOf(HttpStatus.OK));
		logFields.setReturnedResponseStatus(String.valueOf(response.getStatus().isSuccess()));
		
		manager.notificationUnsubscription(parameters.getSubscriptionId(), parameters.getRequestId(), parameters.getSubscriber());

		logFields.setResponseTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		loggerManager.write(logFields);
		
		return response;
	}



}
