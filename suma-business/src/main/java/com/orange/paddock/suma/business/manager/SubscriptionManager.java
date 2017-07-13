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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.orange.paddock.commons.msisdn.PdkMsisdnUtils;
import com.orange.paddock.commons.oneapi.PdkAcrUtils;
import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.SumaAlreadyRevokedSubException;
import com.orange.paddock.suma.business.exception.SumaInternalErrorException;
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
import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwNotRespondingException;
import com.orange.paddock.suma.consumer.ccgw.model.SumaSubscriptionRequest;
import com.orange.paddock.suma.consumer.ccgw.model.SumaUnsubscriptionRequest;
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

	@Value("${orange.wtpapi.default.serv}")
	private String wtDefaultService;

	@Autowired
	private WTApiClient wtClient;

	@Autowired
	private CcgwClient ccgwClient;

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
		TECHNICAL_LOGGER.debug("Starting subscription business logic with subscriptionDto: {} and endUserIdValue: {}", subscriptionDto,
				endUserIdValue);

		SubscriptionResponse subId = new SubscriptionResponse();
		
		/** MSISDN to store in mongo: retrieved from WT or subscriptionDto body and formatted with tel:+ */
		String userMsisdnToStore = null;

		/**************** call WT to get MSISDN : endUserId acr:X-Orange-ISE2, acr:OrangeAPIToken **************/
		if (Objects.equals(PdkAcrUtils.ACR_ISE2, subscriptionDto.getEndUserId())
				|| Objects.equals(PdkAcrUtils.ACR_ORANGE_API_TOKEN, subscriptionDto.getEndUserId())) {

			TECHNICAL_LOGGER.debug("Trying to get MSISDN from WT..");

			List<String> wtRequestedInfos = new ArrayList<String>();
			wtRequestedInfos.add(WTInfo.USER_PROFILE_MSISDN);

			Map<String, String> wtInputParameters = new HashMap<>();
			wtInputParameters.put(WTParameter.SERVICE, wtDefaultService);
			wtInputParameters.put(WTParameter.ENCODING, "UTF-8");

			if (Objects.equals(PdkAcrUtils.ACR_ORANGE_API_TOKEN, subscriptionDto.getEndUserId())) {
				wtInputParameters.put(WTParameter.ORANGE_API_TOKEN, subscriptionDto.getEndUserId());
			}
			if (Objects.equals(PdkAcrUtils.ACR_ISE2, subscriptionDto.getEndUserId())) {
				wtInputParameters.put(WTParameter.MCO, mco);
				wtInputParameters.put(WTParameter.ISE2, subscriptionDto.getEndUserId());
			}
			try {
				String retrievedWtMsisdn = wtClient.getWassupInfos(wtRequestedInfos, wtInputParameters).get(WTInfo.USER_PROFILE_MSISDN);
				TECHNICAL_LOGGER.debug("WT returned MSISDN = {}", retrievedWtMsisdn);

				if (Objects.isNull(retrievedWtMsisdn) || Objects.equals("", retrievedWtMsisdn)) {
					TECHNICAL_LOGGER.error("WT module returns invalid MSISDN value");
					throw new SumaWtApiIntegrationException();
				} else
					userMsisdnToStore = PdkMsisdnUtils.formatMsisdn(retrievedWtMsisdn);
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

		} else {
			// endUserId == tel:+xxx
			userMsisdnToStore = subscriptionDto.getEndUserId();
		}
		TECHNICAL_LOGGER.debug("MSISDN after retrieve and formatting : {}", userMsisdnToStore);

		/**************** Store subscription session **************/
		Subscription subscriptionSessionToStore = new Subscription();
		subscriptionSessionToStore = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSessionToStore.setSubscriptionId(UUID.randomUUID().toString());
		String generatedTransactionId = UUID.randomUUID().toString();
		subscriptionSessionToStore.setTransactionId(generatedTransactionId);
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
		SumaSubscriptionRequest sumaSubscriptionRequest = new SumaSubscriptionRequest();
		sumaSubscriptionRequest = subscriptionMapper.map(subscriptionDto, SumaSubscriptionRequest.class);
		sumaSubscriptionRequest.setSubscriber(userMsisdnToStore);

		try {
			TECHNICAL_LOGGER.debug("Trying to call CCGW..");
			sumaSubscriptionRequest.setTransactionId(generatedTransactionId);
			subscriptionId = ccgwClient.subscribe(sumaSubscriptionRequest);

			TECHNICAL_LOGGER.debug("Received CCGW response {}", subscriptionId);
			if (!Objects.isNull(subscriptionId)) {// CCGW response OK
				TECHNICAL_LOGGER.debug("Received subscriptionId is NOOOT NULL, update session");
				storedSubscription.setSubscriptionId(subscriptionId);
				storedSubscription.setStatus(SubscriptionStatusUtils.STATUS_WAITING_ACTIVATION);
				subscriptionRepository.save(storedSubscription);

			} else {// CCGW response NOT OK
				TECHNICAL_LOGGER.debug("Received subscriptionId is NULL, update session");
				// Store session with status SUBSCRIPTION-ERROR
				storedSubscription.setStatus(SubscriptionStatusUtils.STATUS_SUBSCRIPTION_ERROR);
				subscriptionRepository.save(storedSubscription);
			}

		} catch (CcgwNotRespondingException e) {
			TECHNICAL_LOGGER.error("CCGW is unresponsive");

			storedSubscription.setStatus(SubscriptionStatusUtils.STATUS_SUBSCRIPTION_ERROR);
			subscriptionRepository.save(storedSubscription);
			throw new SumaCcgwUnresponsiveException();

		} catch (CcgwClientException e) {
			TECHNICAL_LOGGER.error("An error occured while calling CCGW {}", e.toString());

			storedSubscription.setStatus(SubscriptionStatusUtils.STATUS_SUBSCRIPTION_ERROR);
			subscriptionRepository.save(storedSubscription);

			if (e.getCcgwFaultStatusCode().startsWith("6") && !e.getCcgwFaultStatusCode().equals("628") && !e.getCcgwFaultStatusCode().equals("629")) {
				throw new SumaCcgwInternalErrorException();
			}
			String fault = e.getCcgwFaultStatusCode();
			TECHNICAL_LOGGER.debug("================================  ", fault);
			switch (e.getCcgwFaultStatusCode()) {
			case "321":
			case "510":
			case "512":
			case "513":
				throw new SumaCcgwInternalErrorException();

			default:
				throw new SumaCcgwIntegrationErrorException();
			}

		} catch (Exception e) {
			TECHNICAL_LOGGER.error("An unexpected error occured while calling CCGW {}", e);
			storedSubscription.setStatus(SubscriptionStatusUtils.STATUS_SUBSCRIPTION_ERROR);
			subscriptionRepository.save(storedSubscription);
			throw new SumaCcgwIntegrationErrorException();
		}
		
		subId.setMsisdn(PdkMsisdnUtils.getMsisdnWithoutPrefix(userMsisdnToStore));
		subId.setSubscriptionId(subscriptionSessionToStore.getSubscriptionId());
		subId.setCcgwSubscriptionId(subscriptionId);
		
		return subId;
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
		String subscriptionStatus = null;

		if (Objects.isNull(subscriptionSessionFound)) {
			TECHNICAL_LOGGER.error("No subscription session found for '{}'", subscriptionId);
			throw new SumaUnknownSubscriptionIdException();
		} else {
			subscriptionStatus = subscriptionSessionFound.getStatus();
		}

		if (Objects.equals(subscriptionStatus, SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR)) {
			TECHNICAL_LOGGER.error("Subscription session found with status UNSUBSCRIPTION_ERROR");
			throw new SumaInternalErrorException();
		} else if (Objects.equals(subscriptionStatus, SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED)
				|| Objects.equals(subscriptionStatus, SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING)) {
			TECHNICAL_LOGGER.error("Subscription session found with status {}", subscriptionStatus);
			throw new SumaAlreadyRevokedSubException(subscriptionId);
		} else if (Objects.equals(subscriptionStatus, SubscriptionStatusUtils.STATUS_ARCHIVED)
				|| Objects.equals(subscriptionStatus, SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING)) {
			TECHNICAL_LOGGER.info("Nothin to do, subscription status: {}", subscriptionStatus);
		} else {

			// Calls CCGW to unsubscribe
			SumaUnsubscriptionRequest sumaUnsubscriptionRequest = new SumaUnsubscriptionRequest();
			sumaUnsubscriptionRequest.setProviderId(subscriptionSessionFound.getServiceId());
			sumaUnsubscriptionRequest.setSubscriptionId(subscriptionId);
			sumaUnsubscriptionRequest.setSubscriber(subscriptionSessionFound.getEndUserId());

			try {
				Boolean ccgwResponseStatus = ccgwClient.unsubscribe(sumaUnsubscriptionRequest);

				if (!ccgwResponseStatus) {
					TECHNICAL_LOGGER.info("Unsubscribe call from CCGW was not successful");
					subscriptionSessionFound.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);
					Subscription updatedSession = subscriptionRepository.save(subscriptionSessionFound);
					subscriptionStatus = updatedSession.getStatus();
				} else {
					TECHNICAL_LOGGER.debug("Unsubscribe call from CCGW OK !");
					subscriptionSessionFound.setStatus(SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING);
					Subscription updatedSession = subscriptionRepository.save(subscriptionSessionFound);
					subscriptionStatus = updatedSession.getStatus();
				}

			} catch (CcgwNotRespondingException e) {
				TECHNICAL_LOGGER.error("CCGW is unresponsive");
				subscriptionSessionFound.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);
				Subscription updatedSession =subscriptionRepository.save(subscriptionSessionFound);
				subscriptionStatus = updatedSession.getStatus();
				throw new SumaCcgwUnresponsiveException();

			} catch (CcgwClientException e) {
				TECHNICAL_LOGGER.error("An error occured while calling CCGW {}", e.toString());

				subscriptionSessionFound.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);
				Subscription updatedSession =	subscriptionRepository.save(subscriptionSessionFound);
				subscriptionStatus = updatedSession.getStatus();
				if (e.getCcgwFaultStatusCode().startsWith("6") && !e.getCcgwFaultStatusCode().equals("628")
						&& !e.getCcgwFaultStatusCode().equals("629")) {
					throw new SumaCcgwInternalErrorException();
				}
				switch (e.getCcgwFaultStatusCode()) {
				case "321":
				case "510":
				case "512":
				case "513":
					throw new SumaCcgwInternalErrorException();

				default:
					throw new SumaCcgwIntegrationErrorException();
				}

			} catch (Exception e) {
				TECHNICAL_LOGGER.error("An unexpected error occured while calling CCGW {}", e);
				subscriptionSessionFound.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);
				Subscription updatedSession =subscriptionRepository.save(subscriptionSessionFound);
				subscriptionStatus = updatedSession.getStatus();
				throw new SumaCcgwIntegrationErrorException();
			}
		}

		return subscriptionStatus;
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
}
