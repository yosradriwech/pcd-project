package com.orange.paddock.suma.business.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.orange.paddock.suma.business.manager.SubscriptionStatusUtils;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;

@Component
public class SubscriptionAutoActivationJob {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(SubscriptionAutoActivationJob.class);

	@Value("${suma.subscription.wait.activation.time}")
	private long timeToWaitInSeconds;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Scheduled(cron = "${suma.subscription.activation.period}")
	public void activateSubscriptionJob() {
		TECHNICAL_LOGGER.debug("Start Activation job");
		List<Subscription> allRegistredSubscriptions = new ArrayList<Subscription>();

		List<String> waiting_status = Arrays.asList(SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING, SubscriptionStatusUtils.STATUS_WAITING_ACTIVATION);

		try {

			allRegistredSubscriptions = subscriptionRepository.findByStatusIn(waiting_status);
			TECHNICAL_LOGGER.debug("Size of subscriptions {}", allRegistredSubscriptions.size());

			if (!allRegistredSubscriptions.isEmpty()) {
				Date currentDate = new Date();
				TECHNICAL_LOGGER.debug("List is not empty ");
				for (Subscription subscriptionFound : allRegistredSubscriptions) {

					// TECHNICAL_LOGGER.debug("sub found with service {} & UserId {}, update status ..", subscriptionFound.getServiceId(),
					// subscriptionFound.getEndUserId());

					long differenceInSeconds = (currentDate.getTime() - subscriptionFound.getLastUpdatedDate().getTime()) / 1000;
					// TECHNICAL_LOGGER.debug("Must be autoActivate subscription");

					if (differenceInSeconds > timeToWaitInSeconds) {
						// TECHNICAL_LOGGER.debug("Difference in seconds {}",differenceInSeconds);
						subscriptionFound.setAutoActivated(true);

						if (subscriptionFound.getStatus().equals(SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING)) {
							subscriptionFound.setStatus(SubscriptionStatusUtils.STATUS_ARCHIVED);
							subscriptionFound.setDeActivationDate(new Date());
						}
						if (subscriptionFound.getStatus().equals(SubscriptionStatusUtils.STATUS_WAITING_ACTIVATION)) {
							subscriptionFound.setStatus(SubscriptionStatusUtils.STATUS_ACTIVE);
							subscriptionFound.setActivationDate(new Date());
						}
						subscriptionRepository.save(subscriptionFound);
					}
				}
			}
		} catch (Exception e) {
			TECHNICAL_LOGGER.error("Error auto activating subscriptions :" + e);
		}
	}
}
