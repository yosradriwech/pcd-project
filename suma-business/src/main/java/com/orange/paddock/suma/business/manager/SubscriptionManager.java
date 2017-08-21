package com.orange.paddock.suma.business.manager;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orange.paddock.commons.msisdn.PdkMsisdnUtils;
import com.orange.paddock.commons.oneapi.PdkAcrUtils;
import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.SumaAlreadyRevokedSubException;
import com.orange.paddock.suma.business.exception.SumaDuplicateSubscriptionRequestException;
import com.orange.paddock.suma.business.exception.SumaInternalErrorException;
import com.orange.paddock.suma.business.exception.SumaSubscriptionCancelIsPendingException;
import com.orange.paddock.suma.business.exception.SumaSubscriptionCancellationPendingException;
import com.orange.paddock.suma.business.exception.SumaUnknownSubscriptionIdException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwIntegrationErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwInternalErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwUnresponsiveException;
import com.orange.paddock.suma.business.exception.iosw.SumaIoswInternalErrorException;
import com.orange.paddock.suma.business.exception.iosw.SumaIoswUnresponsiveException;
import com.orange.paddock.suma.business.exception.wt.SumaWtApiAuthenticationFailureException;
import com.orange.paddock.suma.business.exception.wt.SumaWtApiIntegrationException;
import com.orange.paddock.suma.business.exception.wt.SumaWtApiInternalErrorException;
import com.orange.paddock.suma.business.mapper.SubscriptionDtoMapper;
import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.business.model.SubscriptionResponse;
import com.orange.paddock.suma.business.service.SubscriptionService;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;
import com.orange.paddock.wtapi.client.WTApiClient;
import com.orange.paddock.wtapi.commons.WTInfo;
import com.orange.paddock.wtapi.commons.WTParameter;
import com.orange.paddock.wtapi.exception.WTException;
import com.orange.paddock.wtapi.exception.WTHttpTransportException;

@Service
public class SubscriptionManager {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(SubscriptionManager.class);

	@Autowired
	private WTApiClient wtClient;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private SubscriptionDtoMapper subscriptionMapper;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	/**
	 * 
	 * @param subscriptionDto
	 * @param endUserIdValue
	 * @param mco
	 * @return subscriptionId
	 * @throws AbstractSumaException
	 */
	public SubscriptionResponse subscribe(SubscriptionDto subscriptionDto, String endUserIdValue, String mco) throws AbstractSumaException {
		TECHNICAL_LOGGER.debug("Starting subscription business logic with subscriptionDto: {} and endUserIdValue: {}", subscriptionDto, endUserIdValue);

		SubscriptionResponse subscriptionResponse = new SubscriptionResponse();

		/**
		 * MSISDN to store in mongo: retrieved from WT or subscriptionDto body
		 * and formatted with tel:+
		 */
		String userMsisdnToStore = null;

		/**
		 * call WT to dealiase ISE2/MCO or acr:OrangeAPIToken
		 */
		if (Objects.equals(PdkAcrUtils.ACR_ISE2, subscriptionDto.getEndUserId())
				|| Objects.equals(PdkAcrUtils.ACR_ORANGE_API_TOKEN, subscriptionDto.getEndUserId())) {

			Map<String, String> wtInputParameters = new HashMap<>();

			if (Objects.equals(PdkAcrUtils.ACR_ORANGE_API_TOKEN, subscriptionDto.getEndUserId())) {
				wtInputParameters.put(WTParameter.ORANGE_API_TOKEN, endUserIdValue);
			} else if (Objects.equals(PdkAcrUtils.ACR_ISE2, subscriptionDto.getEndUserId())) {
				wtInputParameters.put(WTParameter.MCO, mco);
				wtInputParameters.put(WTParameter.ISE2, endUserIdValue);
			}

			userMsisdnToStore = this.dealiasing(wtInputParameters);

		} else {
			// endUserId == tel:+xxx
			userMsisdnToStore = PdkMsisdnUtils.getMsisdnWithoutPrefix(subscriptionDto.getEndUserId());
		}
		TECHNICAL_LOGGER.debug("MSISDN after dealiasing or formatting : {}", userMsisdnToStore);

		/**************** Idempotency **************/
		Subscription previousSubscriptionSession = new Subscription();
		previousSubscriptionSession = subscriptionRepository.findOneByEndUserIdAndServiceIdAndOnBehalfOfAndCategoryCodeAndDescription(userMsisdnToStore,
				subscriptionDto.getServiceId(), subscriptionDto.getOnBehalfOf(), subscriptionDto.getCategoryCode(), subscriptionDto.getDescription());

		if (!Objects.isNull(previousSubscriptionSession)) {
			TECHNICAL_LOGGER.debug("Found previous session for subscription request with subscriptionId= {},  status= {} and transactionId= {}",
					previousSubscriptionSession.getSubscriptionId(), previousSubscriptionSession.getStatus(), previousSubscriptionSession.getTransactionId());

			String status = previousSubscriptionSession.getStatus();
			if (Objects.equals(status, SubscriptionStatusUtils.STATUS_PENDING)) {
				// throw error PDK_SUMA_0008
				TECHNICAL_LOGGER.error("Subscription session found with status PENDING");
				throw new SumaDuplicateSubscriptionRequestException();

			} else if ((Objects.equals(status, SubscriptionStatusUtils.STATUS_WAITING_ACTIVATION))
					|| (Objects.equals(status, SubscriptionStatusUtils.STATUS_ACTIVE))) {
				// idempotency
				TECHNICAL_LOGGER.debug("Subscription session found with status {}", status);
				subscriptionResponse.setCcgwSubscriptionId(previousSubscriptionSession.getSubscriptionId());
				subscriptionResponse.setMsisdn(PdkMsisdnUtils.getMsisdnWithoutPrefix(userMsisdnToStore));
				subscriptionResponse.setSubscriptionId(previousSubscriptionSession.getTransactionId());
				return subscriptionResponse;
			} else if ((Objects.equals(status, SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING))
					|| (Objects.equals(status, SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING))) {
				// throw error PDK_SUMA_0009
				TECHNICAL_LOGGER.error("Subscription session found with status {}", status);
				throw new SumaSubscriptionCancelIsPendingException();

			} else if ((Objects.equals(status, SubscriptionStatusUtils.STATUS_ARCHIVED))
					|| (Objects.equals(status, SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED))
					|| (Objects.equals(status, SubscriptionStatusUtils.STATUS_UNKNOWN_UNSUBSCRIPTION_ARCHIVED))
					|| (Objects.equals(status, SubscriptionStatusUtils.STATUS_SUBSCRIPTION_ERROR))) {
				// delete session
				subscriptionRepository.delete(previousSubscriptionSession);
			
			} else if ((Objects.equals(status, SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR))) {
				TECHNICAL_LOGGER.error("Subscription session found with status {}", status);
				throw new SumaSubscriptionCancellationPendingException();
			}
		}

		TECHNICAL_LOGGER.debug("The process continues after one/no session found ");

		/**************** Store subscription session **************/
		Subscription subscriptionSessionToStore = new Subscription();
		subscriptionSessionToStore = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSessionToStore.setSubscriptionId(UUID.randomUUID().toString());
		subscriptionSessionToStore.setTransactionId(UUID.randomUUID().toString());
		subscriptionSessionToStore.setEndUserId(userMsisdnToStore);

		subscriptionSessionToStore.setStatus(SubscriptionStatusUtils.STATUS_PENDING);
		TECHNICAL_LOGGER.debug("Status ? {}", subscriptionSessionToStore.getStatus());

		Subscription storedSubscription = subscriptionRepository.save(subscriptionSessionToStore);
		TECHNICAL_LOGGER.debug("Returned stored session is null ?? " + Objects.isNull(storedSubscription));
		if (Objects.isNull(storedSubscription)) {
			TECHNICAL_LOGGER.error("Subscription session storing failed");
			throw new SumaInternalErrorException();
		}

		/**************** Call CCGW to get subscriptionId **************/
		String subscriptionId = null;
		try {
			subscriptionId = subscriptionService.subscribe(subscriptionDto, userMsisdnToStore, subscriptionSessionToStore.getTransactionId());

			if (Objects.isNull(subscriptionId)) {
				TECHNICAL_LOGGER.debug("Received subscriptionId is NULL, update session");
				storedSubscription.setStatus(SubscriptionStatusUtils.STATUS_SUBSCRIPTION_ERROR);
			} else {
				TECHNICAL_LOGGER.debug("Received subscriptionId '{}'", subscriptionId);
				storedSubscription.setSubscriptionId(subscriptionId);
				storedSubscription.setStatus(SubscriptionStatusUtils.STATUS_WAITING_ACTIVATION);
			}

		} catch (SumaCcgwUnresponsiveException | SumaCcgwIntegrationErrorException | SumaCcgwInternalErrorException e) {
			TECHNICAL_LOGGER.error("CCGW error: {}", e.getMessage());

			storedSubscription.setStatus(SubscriptionStatusUtils.STATUS_SUBSCRIPTION_ERROR);

			throw e;

		} finally {
			subscriptionRepository.save(storedSubscription);
		}

		subscriptionResponse.setMsisdn(PdkMsisdnUtils.getMsisdnWithoutPrefix(userMsisdnToStore));
		subscriptionResponse.setSubscriptionId(subscriptionSessionToStore.getSubscriptionId());
		subscriptionResponse.setCcgwSubscriptionId(subscriptionId);

		return subscriptionResponse;
	}

	/**
	 * 
	 * @param subscriptionId
	 * @return subscriptionStatus after unsubscribe CCGW response
	 * @throws AbstractSumaException
	 */
	public String unsubscribe(String subscriptionId) throws AbstractSumaException {
		TECHNICAL_LOGGER.debug("Starting unsubscription business logic with subscriptionId '{}'", subscriptionId);

		Subscription subscriptionSessionFound = subscriptionRepository.findOneBySubscriptionId(subscriptionId);

		if (Objects.isNull(subscriptionSessionFound)) {
			TECHNICAL_LOGGER.error("No subscription session found for '{}'", subscriptionId);
			throw new SumaUnknownSubscriptionIdException(subscriptionId);
		}

		if (Objects.equals(subscriptionSessionFound.getStatus(), SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR)) {
			TECHNICAL_LOGGER.error("Subscription session found with status UNSUBSCRIPTION_ERROR");
			throw new SumaInternalErrorException();
		} else if (Objects.equals(subscriptionSessionFound.getStatus(), SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED)
				|| Objects.equals(subscriptionSessionFound.getStatus(), SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING)) {
			TECHNICAL_LOGGER.error("Subscription session found with status {}", subscriptionSessionFound.getStatus());
			throw new SumaAlreadyRevokedSubException(subscriptionId);
		} else if (Objects.equals(subscriptionSessionFound.getStatus(), SubscriptionStatusUtils.STATUS_ARCHIVED)
				|| Objects.equals(subscriptionSessionFound.getStatus(), SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING)) {
			TECHNICAL_LOGGER.info("Nothin to do, subscription status: {}", subscriptionSessionFound.getStatus());
		} else {
			try {
				if (subscriptionService.unsubscribe(subscriptionId, subscriptionSessionFound.getServiceId(), subscriptionSessionFound.getEndUserId())) {
					TECHNICAL_LOGGER.debug("Unsubscribe call from CCGW OK !");
					subscriptionSessionFound.setStatus(SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING);
				} else {
					TECHNICAL_LOGGER.info("Unsubscribe call from CCGW was not successful");
					subscriptionSessionFound.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);
				}

			} catch (SumaCcgwUnresponsiveException | SumaCcgwIntegrationErrorException | SumaCcgwInternalErrorException e) {
				TECHNICAL_LOGGER.error("CCGW error: {}", e.getMessage());

				subscriptionSessionFound.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);

				throw e;
			} finally {
				subscriptionRepository.save(subscriptionSessionFound);
			}
		}

		return subscriptionSessionFound.getStatus();
	}

	/**
	 * 
	 * @param subscriptionId
	 * @return subscriptionDto of requested subscriptionId
	 */
	public SubscriptionDto getSubscriptionStatus(String subscriptionId) throws AbstractSumaException {

		TECHNICAL_LOGGER.debug("Starting getStatus business logic with subscriptionId '{}'", subscriptionId);

		Subscription subscriptionSessionFound = subscriptionRepository.findOneBySubscriptionId(subscriptionId);
		if (Objects.isNull(subscriptionSessionFound)) {
			TECHNICAL_LOGGER.error("No session found for identifier {}", subscriptionId);
			throw new SumaUnknownSubscriptionIdException(subscriptionId);
		}

		switch (subscriptionSessionFound.getStatus()) {
		case SubscriptionStatusUtils.STATUS_ARCHIVED:
		case SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING:
		case SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED:
		case SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING:
		case SubscriptionStatusUtils.STATUS_UNKNOWN_UNSUBSCRIPTION_ARCHIVED:
			TECHNICAL_LOGGER.error("Subscription already revoked for identifier {}", subscriptionId);
			throw new SumaAlreadyRevokedSubException(subscriptionId);
		default:
			TECHNICAL_LOGGER.debug("Subscription status is {}", subscriptionSessionFound.getStatus());
		}

		SubscriptionDto subscriptionResponseDto = new SubscriptionDto();
		subscriptionResponseDto = subscriptionMapper.map(subscriptionSessionFound, SubscriptionDto.class);

		TECHNICAL_LOGGER.debug("Return getStatus response:  {}", subscriptionResponseDto.toString());

		return subscriptionResponseDto;
	}

	private String dealiasing(Map<String, String> wtInputParameters) throws SumaIoswInternalErrorException, SumaIoswUnresponsiveException,
			SumaInternalErrorException, SumaWtApiIntegrationException, SumaWtApiInternalErrorException, SumaWtApiAuthenticationFailureException {

		String msisdn = null;

		TECHNICAL_LOGGER.debug("Trying to get MSISDN from WT..");

		List<String> wtRequestedInfos = new ArrayList<String>();
		wtRequestedInfos.add(WTInfo.USER_PROFILE_MSISDN);

		try {
			msisdn = wtClient.getWassupInfos(wtRequestedInfos, wtInputParameters).get(WTInfo.USER_PROFILE_MSISDN);
			TECHNICAL_LOGGER.debug("WT returned MSISDN = {}", msisdn);

			if (Objects.isNull(msisdn) || Objects.equals("", msisdn)) {
				TECHNICAL_LOGGER.error("WT module returns invalid MSISDN value");
				throw new SumaWtApiIntegrationException();
			}

		} catch (WTHttpTransportException wtHttpException) {
			TECHNICAL_LOGGER.error("Http Transport Exception caugh when trying to reach WT module");
			throw new SumaIoswInternalErrorException();

		} catch (WTException wtException) {
			TECHNICAL_LOGGER.error("Error while calling WT module");

			switch (wtException.getHttpStatusCode()) {
			case 500:
				throw new SumaWtApiInternalErrorException();
			case 403:
				throw new SumaWtApiAuthenticationFailureException();
			default:// 401, 404, default
				throw new SumaWtApiIntegrationException();
			}

		} catch (AbstractSumaException e) {
			throw e;
		} catch (Exception e) {
			TECHNICAL_LOGGER.error("Unexpected error while calling WT module " + e.getCause());
			if (e.getCause() instanceof SocketTimeoutException) {
				throw new SumaIoswUnresponsiveException();
			} else
				throw new SumaInternalErrorException();
		}

		return msisdn;
	}

}
