package com.orange.paddock.suma.business.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.orange.paddock.commons.date.PdkDateUtils;
import com.orange.paddock.commons.log.PdkLogIdBean;
import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.SumaAlreadyActiveSubscriptionException;
import com.orange.paddock.suma.business.exception.SumaAlreadyRevokedUnSubException;
import com.orange.paddock.suma.business.exception.SumaSubscriptionIsStillActiveException;
import com.orange.paddock.suma.business.exception.notification.SumaNotificationException;
import com.orange.paddock.suma.business.log.InternalNotificationSubLogger;
import com.orange.paddock.suma.business.log.InternalNotificationSubLogger.NotifSubFields;
import com.orange.paddock.suma.business.log.InternalNotificationUnsubLogger;
import com.orange.paddock.suma.business.log.InternalNotificationUnsubLogger.NotifUnsubFields;
import com.orange.paddock.suma.business.service.SubscriptionService;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;

@Service
public class NotificationManager {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(NotificationManager.class);

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private SubscriptionRepository repository;

	@Autowired
	private PdkLogIdBean loggerId;

	@Async("subscriptionNotificationExecutor")
	public void notificationSubscription(String subscriptionId, String transactionId, Date activationDate, String endUserId) {

		TECHNICAL_LOGGER.debug("Starting asynchronuous subscription notification task for subId: {}, endUserId: {}, transactionId: {}", subscriptionId,
				endUserId, transactionId);

		Map<NotifSubFields, String> logs = new HashMap<NotifSubFields, String>();

		logs.put(NotifSubFields.INTERNAL_ID, loggerId.getInternalId());
		logs.put(NotifSubFields.START_PROCESS_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
		logs.put(NotifSubFields.SUBSCRIPTION_ID, subscriptionId);

		try {
			// catch mongo error
			Subscription subscription = repository.findOneBySubscriptionId(subscriptionId);

			if (subscription != null) {
				TECHNICAL_LOGGER.debug("Session found for subscription id : {}", subscriptionId);
				String status = subscription.getStatus();

				if (status.equals(SubscriptionStatusUtils.STATUS_ARCHIVED) || status.equals(SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING)
						|| status.equals(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING)
						|| status.equals(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED)) {
					TECHNICAL_LOGGER.debug("Status is about archiving");
					throw new SumaAlreadyRevokedUnSubException(subscriptionId);

				} else if (status.equals(SubscriptionStatusUtils.STATUS_ACTIVE)) {
					// Subscription notification error: trying to active a
					// subscription that is already active
					TECHNICAL_LOGGER.debug("Status is already active");
					throw new SumaAlreadyActiveSubscriptionException(subscriptionId);
				} else {
					TECHNICAL_LOGGER.debug("Status should be set to active");
					status = SubscriptionStatusUtils.STATUS_ACTIVE;
					subscription.setStatus(status);
					repository.save(subscription);
				}

			} else {
				// Subscription id not found
				subscription = repository.findOneByTransactionId(transactionId);
				if (null != subscription) {
					TECHNICAL_LOGGER.debug("Session NOT found for subId : {}, but found for transactionId: {} ", subscriptionId, transactionId);
					TECHNICAL_LOGGER.debug("Trying to unsubscribe user....");

					boolean unsubscriptionSuccess = subscriptionService.unsubscribe(subscriptionId, subscription.getServiceId(), endUserId);
					if (unsubscriptionSuccess) {
						TECHNICAL_LOGGER.debug("UNSUBSCRIPTION was OK.");
						subscription.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING);
					} else {
						TECHNICAL_LOGGER.debug("UNSUBSCRIPTION was NOT OK.");
						subscription.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);
					}
				}
			}

		} catch (AbstractSumaException ex) {

			TECHNICAL_LOGGER.debug("Error occured during Notification Subscription with message: '{}'", ex.getErrorDescription());
			logs.put(NotifSubFields.INTERNAL_ERROR_CODE, ex.getInternalErrorCode());
			logs.put(NotifSubFields.INTERNAL_ERROR_DESCRIPTION, ex.getErrorDescription());

		} catch (Exception e) {

			TECHNICAL_LOGGER.debug("An unexpected error occured during notification Subscription with message: '{}'", e.getMessage());
			logs.put(NotifSubFields.INTERNAL_ERROR_DESCRIPTION, e.getMessage());

		} finally {
			logs.put(NotifSubFields.END_PROCESS_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			InternalNotificationSubLogger.write(logs);
		}
	}

	@Async("unsubscriptionNotificationExecutor")
	public void notificationUnsubscription(String subscriptionId, String requestId, String endUserId) throws AbstractSumaException {

		TECHNICAL_LOGGER.debug("Starting asynchronuous Unsubscription notification task");

		Map<NotifUnsubFields, String> logs = new HashMap<NotifUnsubFields, String>();
		logs.put(NotifUnsubFields.INTERNAL_ID, loggerId.getInternalId());
		logs.put(NotifUnsubFields.START_PROCESS_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
		logs.put(NotifUnsubFields.SUBSCRIPTION_ID, subscriptionId);

		try {
			Subscription subscriptionToDeactivate = repository.findOneBySubscriptionId(subscriptionId);

			if (null == subscriptionToDeactivate) {
				// LOG PDK_SUMA_0004 in log File
				TECHNICAL_LOGGER.debug("No session found for unsubscription notif request ..");
				subscriptionToDeactivate = new Subscription();
				subscriptionToDeactivate.setSubscriptionId(subscriptionId);
				subscriptionToDeactivate.setCreationDate(new Date());
				subscriptionToDeactivate.setDeActivationDate(new Date());
				subscriptionToDeactivate.setTransactionId(requestId);
				subscriptionToDeactivate.setEndUserId(endUserId);
				subscriptionToDeactivate.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED);

				repository.save(subscriptionToDeactivate);
				throw new SumaNotificationException();

			} else {
				// subscriptionDeactivate = true;

				if (subscriptionToDeactivate.getStatus().equals(SubscriptionStatusUtils.STATUS_ACTIVE)
						|| subscriptionToDeactivate.getStatus().equals(SubscriptionStatusUtils.STATUS_WAITING_ACTIVATION)) {

					subscriptionToDeactivate.setDeActivationDate(new Date());
					subscriptionToDeactivate.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_UNSUBSCRIPTION_ARCHIVED);
					repository.save(subscriptionToDeactivate);

					throw new SumaSubscriptionIsStillActiveException(subscriptionId);

				} else if (subscriptionToDeactivate.getStatus().equals(SubscriptionStatusUtils.STATUS_ARCHIVED)
						|| subscriptionToDeactivate.getStatus().equals(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED)
						|| subscriptionToDeactivate.getStatus().equals(SubscriptionStatusUtils.STATUS_UNKNOWN_UNSUBSCRIPTION_ARCHIVED)) {

					throw new SumaAlreadyRevokedUnSubException(subscriptionId);

				} else if (subscriptionToDeactivate.getStatus().equals(SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING)) {
					subscriptionToDeactivate.setStatus(SubscriptionStatusUtils.STATUS_ARCHIVED);
				} else if (subscriptionToDeactivate.getStatus().equals(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING)) {
					subscriptionToDeactivate.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_UNSUBSCRIPTION_ARCHIVED);
				} else if (subscriptionToDeactivate.getStatus().equals(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR)) {
					subscriptionToDeactivate.setStatus(SubscriptionStatusUtils.STATUS_ARCHIVED);
				}

				subscriptionToDeactivate.setDeActivationDate(new Date());
				repository.save(subscriptionToDeactivate);
			}

		} catch (AbstractSumaException ex) {

			TECHNICAL_LOGGER.debug("Error occured during Notification Unsubscription with message: '{}'", ex.getErrorDescription());
			logs.put(NotifUnsubFields.INTERNAL_ERROR_CODE, ex.getInternalErrorCode());
			logs.put(NotifUnsubFields.INTERNAL_ERROR_DESCRIPTION, ex.getErrorDescription());

		} catch (Exception e) {
			TECHNICAL_LOGGER.debug("An unexpected error occured during notification Unsubscription with message: '{}'", e.getMessage());
			logs.put(NotifUnsubFields.INTERNAL_ERROR_DESCRIPTION, e.getMessage());

		} finally {
			logs.put(NotifUnsubFields.END_PROCESS_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			InternalNotificationUnsubLogger.write(logs);
		}

	}

}
