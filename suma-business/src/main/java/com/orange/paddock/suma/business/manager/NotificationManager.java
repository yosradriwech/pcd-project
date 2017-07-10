package com.orange.paddock.suma.business.manager;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.SumaAlreadyActiveSubscriptionException;
import com.orange.paddock.suma.business.exception.SumaAlreadyRevokedUnSubException;
import com.orange.paddock.suma.business.exception.SumaSubscriptionIsStillActiveException;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;

@Service
public class NotificationManager {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(NotificationManager.class);

	@Autowired
	private SubscriptionManager manager;

	@Autowired
	private SubscriptionRepository repository;

	private static String PENDING = "PENDING";

	@Async("subscriptionNotificationExecutor")
	public String notificationSubscription(String subscriptionId, String transactionId,
			Date activationDate, String endUserId) throws AbstractSumaException {
		TECHNICAL_LOGGER.debug("Starting asynchronuous subscription notification task");

		boolean subscriptionActivate = false;

		Subscription subscription = repository.findOneBySubscriptionId(subscriptionId);

		if (subscription != null) {

			subscription = repository.findOneBySubscriptionId(subscriptionId);
			String status = subscription.getStatus();

			if (status == SubscriptionStatusUtils.STATUS_ARCHIVED
					|| status == SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING
					|| status == SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING
					|| status == SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED) {

				throw new SumaAlreadyRevokedUnSubException(subscriptionId);

			} else if (status == SubscriptionStatusUtils.STATUS_ACTIVE) {
				// Subscription notification error: trying to active a
				// subscription that is already active
				throw new SumaAlreadyActiveSubscriptionException(subscriptionId);
			} else {
				subscriptionActivate = true;
				status = SubscriptionStatusUtils.STATUS_ACTIVE;
				subscription.setStatus(status);
			}

		} else {
			// LOG ERROR SUMA PDK_SUMA_0004
			if (repository.findOneByTransactionId(transactionId) == null) {
				
				subscription = new Subscription();
				
				subscription.setSubscriptionId(subscriptionId);
				subscription.setCreationDate(new Date());
				subscription.setActivationDate(activationDate);
				subscription.setTransactionId(transactionId);
				subscription.setEndUserId(endUserId);
				subscription.setStatus(SubscriptionStatusUtils.STATUS_PENDING);

				repository.save(subscription);

			} else {
				subscription = repository.findOneByTransactionId(transactionId);
			}

			String subscriptionToDeleteStatus = manager.unsubscribe(transactionId);
			if (subscriptionToDeleteStatus == SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING) {
				subscription.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING);
			} else if (subscriptionToDeleteStatus == SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR) {
				subscription.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);
			}

		}

		repository.save(subscription);

		return subscription.getStatus();
	}

	@Async("unsubscriptionNotificationExecutor")
	public String notificationUnsubscription(String subscriptionId, String requestId, String endUserId)
			throws AbstractSumaException {
		TECHNICAL_LOGGER.debug("Starting asynchronuous Unsubscription notification task");
	
//		boolean subscriptionDeactivate = false;
		
		Subscription subscriptionToDeactivate = repository.findOneBySubscriptionId(subscriptionId);
		
		if (repository.findOneBySubscriptionId(subscriptionId) == null) {
			// LOG PDK_SUMA_0004 in log File
			
			subscriptionToDeactivate = new Subscription();
			
			subscriptionToDeactivate.setSubscriptionId(subscriptionId);
			subscriptionToDeactivate.setCreationDate(new Date());
			subscriptionToDeactivate.setDeActivationDate(new Date());
			subscriptionToDeactivate.setTransactionId(requestId);
			subscriptionToDeactivate.setEndUserId(endUserId);
			subscriptionToDeactivate.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED);

			repository.save(subscriptionToDeactivate);

		} else {
//			subscriptionDeactivate = true;
			
			if (subscriptionToDeactivate.getStatus() == SubscriptionStatusUtils.STATUS_ACTIVE
					|| subscriptionToDeactivate.getStatus() == SubscriptionStatusUtils.STATUS_WAITING_ACTIVATION) {
				
				subscriptionToDeactivate.setDeActivationDate(new Date());
				subscriptionToDeactivate.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED);
				repository.save(subscriptionToDeactivate);
				
				throw new SumaSubscriptionIsStillActiveException(subscriptionId);
			} else if (subscriptionToDeactivate.getStatus() == SubscriptionStatusUtils.STATUS_ARCHIVED || subscriptionToDeactivate.getStatus() == SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED || subscriptionToDeactivate.getStatus() == SubscriptionStatusUtils.STATUS_UNKNOWN_UNSUBSCRIPTION_ARCHIVED){
				throw new SumaAlreadyRevokedUnSubException(subscriptionId);
			} else if (subscriptionToDeactivate.getStatus() == SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING){
				subscriptionToDeactivate.setStatus(SubscriptionStatusUtils.STATUS_ARCHIVED);
			} else if (subscriptionToDeactivate.getStatus() == SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING){
				subscriptionToDeactivate.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_UNSUBSCRIPTION_ARCHIVED);
			} else if (subscriptionToDeactivate.getStatus() == SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR){
				subscriptionToDeactivate.setStatus(SubscriptionStatusUtils.STATUS_ARCHIVED);
			}
			
			subscriptionToDeactivate.setDeActivationDate(new Date());
			repository.save(subscriptionToDeactivate);
			
		}
		
		return subscriptionToDeactivate.getStatus();
	}
	
	
	
}
