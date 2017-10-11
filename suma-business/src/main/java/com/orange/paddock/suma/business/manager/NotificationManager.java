package com.orange.paddock.suma.business.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwIntegrationErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwInternalErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwUnresponsiveException;
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

	/**
	 * Notification subscription method
	 * 
	 * @param subscriptionId
	 * @param transactionId
	 * @param activationDate
	 * @param endUserId
	 */
	@Async("subscriptionNotificationExecutor")
	public void notificationSubscription(String subscriptionId, String transactionId, Date activationDate, String endUserId) {

		TECHNICAL_LOGGER.info("Starting asynchronuous subscription notification task for subId: {}, endUserId: {}, transactionId: {}, activationDate {}", subscriptionId,
				endUserId, transactionId, activationDate);

		Map<NotifSubFields, String> logs = new HashMap<NotifSubFields, String>();

		logs.put(NotifSubFields.INTERNAL_ID, loggerId.getInternalId());
		logs.put(NotifSubFields.START_PROCESS_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
		logs.put(NotifSubFields.SUBSCRIPTION_ID, subscriptionId);

		try {

			Subscription subscriptionFoundForReceivedNotif = repository.findOneBySubscriptionIdAndEndUserId(subscriptionId, endUserId);
			if (subscriptionFoundForReceivedNotif != null) {

				TECHNICAL_LOGGER.debug("Session found for subscriptionId : {}, msisdn: {}", subscriptionId, endUserId);
				updateSubscriptionForReceivedSubscriptionId(subscriptionFoundForReceivedNotif, subscriptionId, transactionId, activationDate, endUserId);

			} else {

				// SubscriptionId/EndUserId NOT found
				TECHNICAL_LOGGER.debug("Session NOT found for subscriptionId: {}, msisdn :{}", subscriptionId, endUserId);
				SumaNotificationException ex = new SumaNotificationException();
				TECHNICAL_LOGGER.error(ex.getInternalErrorCode(), ex.getErrorDescription());
				logs.put(NotifSubFields.INTERNAL_ERROR_CODE, ex.getInternalErrorCode());
				logs.put(NotifSubFields.INTERNAL_ERROR_DESCRIPTION, ex.getErrorDescription());

				unsubscribeUserWhenNoSubscriptionFound(subscriptionId, transactionId, activationDate, endUserId);
			}

		} catch (AbstractSumaException ex) {
			TECHNICAL_LOGGER.debug("Error occured during Notification Subscription with message: '{}'", ex.getErrorDescription());
			logs.put(NotifSubFields.INTERNAL_ERROR_CODE, ex.getInternalErrorCode());
			logs.put(NotifSubFields.INTERNAL_ERROR_DESCRIPTION, ex.getErrorDescription());

		} catch (Exception e) {
			TECHNICAL_LOGGER.debug("An unexpected error occured during notification Subscription with message: '{}'", e.getMessage());
			logs.put(NotifSubFields.INTERNAL_ERROR_DESCRIPTION, e.getMessage());
			logs.put(NotifSubFields.INTERNAL_ERROR_DESCRIPTION, "Unexpected error has occured");

		} finally {
			logs.put(NotifSubFields.END_PROCESS_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			InternalNotificationSubLogger.write(logs);
		}
	}

	/**
	 * Method called when one subscription session found for notification
	 * 
	 * @param subscriptionFoundForReceivedNotif
	 * @param subscriptionId
	 * @param transactionId
	 * @param activationDate
	 * @param endUserId
	 * @throws SumaAlreadyRevokedUnSubException
	 * @throws SumaAlreadyActiveSubscriptionException
	 */
	private void updateSubscriptionForReceivedSubscriptionId(Subscription subscriptionFoundForReceivedNotif, String subscriptionId, String transactionId,
			Date activationDate, String endUserId) throws SumaAlreadyRevokedUnSubException, SumaAlreadyActiveSubscriptionException {

		String status = subscriptionFoundForReceivedNotif.getStatus();

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
			subscriptionFoundForReceivedNotif.setStatus(status);
			subscriptionFoundForReceivedNotif.setActivationDate(activationDate);
			subscriptionFoundForReceivedNotif.setAutoActivated(false);
			repository.save(subscriptionFoundForReceivedNotif);
		}
	}

	/**
	 * Method called when serviceId is unknown
	 * 
	 * @param serviceIdList
	 * @param subscriptionId
	 * @param transactionId
	 * @param endUserId
	 * @return
	 * @throws SumaCcgwUnresponsiveException
	 * @throws SumaCcgwIntegrationErrorException
	 * @throws SumaCcgwInternalErrorException
	 */
	private String unsubscribeUserFromServiceList(List<String> serviceIdList, String subscriptionId, String transactionId, String endUserId)
			throws SumaCcgwUnresponsiveException, SumaCcgwInternalErrorException {
		boolean serviceFound = false;
		String status = null;

		for (String serviceId : serviceIdList) {

			if (!serviceFound) {
				TECHNICAL_LOGGER.debug("Serviceid = {}", serviceId);
				try {
					boolean unsubscriptionSuccess = subscriptionService.unsubscribe(subscriptionId, serviceId, endUserId);
					
					if (unsubscriptionSuccess) {				
					TECHNICAL_LOGGER.debug("UNSUBSCRIPTION was OK, service found");
					serviceFound = true;
					status = SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING;
				}
				
				} catch (SumaCcgwIntegrationErrorException e) {
					TECHNICAL_LOGGER.debug("Subscription not found for serviceId {}, msisdn {}",serviceId, endUserId);
				
				}
				
			}
		}

		if (!serviceFound) {
			TECHNICAL_LOGGER.debug("UNSUBSCRIPTION was NOT OK.");
			status = SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR;
		}

		return status;

	}

	/**
	 * Method called when no subscription session in error is found for
	 * notification
	 * 
	 * @param subscriptionId
	 * @param transactionId
	 * @param activationDate
	 * @param endUserId
	 * @throws SumaCcgwUnresponsiveException
	 * @throws SumaCcgwIntegrationErrorException
	 * @throws SumaCcgwInternalErrorException
	 */
	private void unsubscribeUserWhenNoSubscriptionFound(String subscriptionId, String transactionId, Date activationDate, String endUserId)
			throws SumaCcgwUnresponsiveException, SumaCcgwIntegrationErrorException, SumaCcgwInternalErrorException {

		Subscription newSubscriptionAfterNotif = new Subscription();
		newSubscriptionAfterNotif.setSubscriptionId(subscriptionId);
		newSubscriptionAfterNotif.setActivationDate(activationDate);
		newSubscriptionAfterNotif.setTransactionId(transactionId);
		newSubscriptionAfterNotif.setEndUserId(endUserId);
		newSubscriptionAfterNotif.setStatus(SubscriptionStatusUtils.STATUS_PENDING);
		newSubscriptionAfterNotif = repository.save(newSubscriptionAfterNotif);

		TECHNICAL_LOGGER.debug("Get all subscription in failure");
		List<Subscription> failedSubscription = repository.findByEndUserIdAndStatus(endUserId, SubscriptionStatusUtils.STATUS_SUBSCRIPTION_ERROR);

		if (!Objects.isNull(failedSubscription) && 0 < failedSubscription.size()) {

			// Get serviceId list for unsubscription
			List<String> serviceIdList = new ArrayList<String>();
			for (Subscription sub : failedSubscription) {
				serviceIdList.add(sub.getServiceId());
			}
			TECHNICAL_LOGGER.debug("Trying to unsubscribe user from {} services", serviceIdList.size());
			String status = unsubscribeUserFromServiceList(serviceIdList, subscriptionId, transactionId, endUserId);
			newSubscriptionAfterNotif.setStatus(status);
			newSubscriptionAfterNotif.setAutoActivated(false);
			repository.save(newSubscriptionAfterNotif);

		} else {
			TECHNICAL_LOGGER.error("No subscription failed for MSISDN {}, problem with SubId: {}", endUserId, subscriptionId);
		}
	}

	/**
	 * Notification subscription method
	 */
	@Async("unsubscriptionNotificationExecutor")
	public void notificationUnsubscription(String subscriptionId, String requestId, String endUserId) throws AbstractSumaException {

		TECHNICAL_LOGGER.debug("Starting asynchronuous Unsubscription notification task");

		Map<NotifUnsubFields, String> logs = new HashMap<NotifUnsubFields, String>();
		logs.put(NotifUnsubFields.INTERNAL_ID, loggerId.getInternalId());
		logs.put(NotifUnsubFields.START_PROCESS_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
		logs.put(NotifUnsubFields.SUBSCRIPTION_ID, subscriptionId);

		try {
			Subscription subscriptionToDeactivate = repository.findOneBySubscriptionIdAndEndUserId(subscriptionId,endUserId);

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
					subscriptionToDeactivate.setAutoActivated(false);
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
				subscriptionToDeactivate.setAutoActivated(false);
				repository.save(subscriptionToDeactivate);
			}

		} catch (AbstractSumaException ex) {

			TECHNICAL_LOGGER.debug("Error occured during Notification Unsubscription with message: '{}'", ex.getErrorDescription());
			logs.put(NotifUnsubFields.INTERNAL_ERROR_CODE, ex.getInternalErrorCode());
			logs.put(NotifUnsubFields.INTERNAL_ERROR_DESCRIPTION, ex.getErrorDescription());

		} catch (Exception e) {
			TECHNICAL_LOGGER.debug("An unexpected error occured during notification Unsubscription with message: '{}'", e.getMessage());
			logs.put(NotifUnsubFields.INTERNAL_ERROR_DESCRIPTION, e.getMessage());
			logs.put(NotifUnsubFields.INTERNAL_ERROR_DESCRIPTION, "Unexpected error has occured");

		} finally {
			logs.put(NotifUnsubFields.END_PROCESS_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			InternalNotificationUnsubLogger.write(logs);
		}

	}

}
