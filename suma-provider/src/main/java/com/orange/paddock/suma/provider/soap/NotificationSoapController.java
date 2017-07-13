package com.orange.paddock.suma.provider.soap;

import javax.jws.WebMethod;
import javax.jws.WebResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.orange.paddock.commons.date.PdkDateUtils;
import com.orange.paddock.commons.log.PdkLogIdBean;
import com.orange.paddock.suma.business.manager.NotificationManager;
import com.orange.paddock.suma.provider.log.NorthNotificationLogger;
import com.orange.paddock.suma.provider.soap.model.NotificationPortType;
import com.orange.paddock.suma.provider.soap.model.Status;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationResponseType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationResponseType;

public class NotificationSoapController implements NotificationPortType {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSoapController.class);

	private static final String SUBSCRIPTION = "SUBSCRIPTION";
	private static final String UNSUBSCRIPTION = "UNSUBSCRIPTION";

	@Autowired
	private NotificationManager manager;

	@Autowired
	private NorthNotificationLogger northNotificationLogger;

	@Autowired
	private PdkLogIdBean loggerId;

	@Override
	@WebMethod(operationName = "SubscriptionNotification")
	@WebResult(name = "activate-subscription-response", targetNamespace = "http://ccgw.vas.gtp.pl/service/NotificationService/v10", partName = "parameters")
	public SubscriptionNotificationResponseType subscriptionNotification(
			SubscriptionNotificationRequestType parameters) {

		LOGGER.debug("subscriptionNotification");

		SubscriptionNotificationResponseType response = new SubscriptionNotificationResponseType();
		Status status = new Status();
		status.setSuccess(true);
		response.setStatus(status);

		northNotificationLogger.setInternalId(loggerId.getInternalId());
		northNotificationLogger.setRequestTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		northNotificationLogger.setNotificationType(SUBSCRIPTION);
		northNotificationLogger.setRequestId(parameters.getRequestId());
		northNotificationLogger.setSubscriptionId(parameters.getSubscriptionId());
		northNotificationLogger.setSubscriber(parameters.getSubscriber());
		northNotificationLogger.setActivationDate(String.valueOf(parameters.getActivationDate()));
		northNotificationLogger.setAssentForActivation(String.valueOf(parameters.isAssentForActivation()));
		northNotificationLogger.setHttpResponseCode(String.valueOf(HttpStatus.OK));
		northNotificationLogger.setReturnedResponseStatus(String.valueOf(response.getStatus().isSuccess()));

		manager.notificationSubscription(parameters.getSubscriptionId(), parameters.getRequestId(),
				parameters.getActivationDate().toGregorianCalendar().getTime(), parameters.getSubscriber());

		northNotificationLogger.setResponseTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		northNotificationLogger.write();

		return response;
	}

	@Override
	@WebMethod(operationName = "UnsubscriptionNotification")
	@WebResult(name = "deactivate-subscription-response", targetNamespace = "http://ccgw.vas.gtp.pl/service/NotificationService/v10", partName = "parameters")
	public UnsubscriptionNotificationResponseType unsubscriptionNotification(
			UnsubscriptionNotificationRequestType parameters) {

		LOGGER.debug("unsubscriptionNotification");

		UnsubscriptionNotificationResponseType response = new UnsubscriptionNotificationResponseType();

		Status status = new Status();
		status.setSuccess(true);
		response.setStatus(status);

		northNotificationLogger.setInternalId(loggerId.getInternalId());
		northNotificationLogger.setRequestTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		northNotificationLogger.setNotificationType(UNSUBSCRIPTION);
		northNotificationLogger.setRequestId(parameters.getRequestId());
		northNotificationLogger.setSubscriptionId(parameters.getSubscriptionId());
		northNotificationLogger.setSubscriber(parameters.getSubscriber());
		northNotificationLogger.setActivationDate(String.valueOf(parameters.getActivationDate()));
		northNotificationLogger.setAssentForActivation(String.valueOf(parameters.isAssentForActivation()));
		northNotificationLogger.setHttpResponseCode(String.valueOf(HttpStatus.OK));
		northNotificationLogger.setReturnedResponseStatus(String.valueOf(response.getStatus().isSuccess()));

		manager.notificationUnsubscription(parameters.getSubscriptionId(), parameters.getRequestId(),
				parameters.getSubscriber());

		northNotificationLogger.setResponseTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		northNotificationLogger.write();

		return response;
	}

}
