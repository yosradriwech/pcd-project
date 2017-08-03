package com.orange.paddock.suma.business.service;

import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwIntegrationErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwInternalErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwUnresponsiveException;
import com.orange.paddock.suma.business.factory.IExceptionFactory;
import com.orange.paddock.suma.business.mapper.SubscriptionDtoMapper;
import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwNotRespondingException;
import com.orange.paddock.suma.consumer.ccgw.model.SumaSubscriptionRequest;
import com.orange.paddock.suma.consumer.ccgw.model.SumaUnsubscriptionRequest;

@Service
public class SubscriptionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionService.class);

	@Autowired
	private CcgwClient ccgwClient;

	@Autowired
	private SubscriptionDtoMapper subscriptionMapper;

	@Resource
	private Map<String, IExceptionFactory> exceptionFactoryMapping;

	public boolean unsubscribe(String subscriptionId, String providerId, String subscriber) throws SumaCcgwUnresponsiveException,
			SumaCcgwIntegrationErrorException, SumaCcgwInternalErrorException {

		boolean ccgwResponseStatus = false;

		SumaUnsubscriptionRequest sumaUnsubscriptionRequest = new SumaUnsubscriptionRequest();
		sumaUnsubscriptionRequest.setProviderId(providerId);
		sumaUnsubscriptionRequest.setSubscriptionId(subscriptionId);
		sumaUnsubscriptionRequest.setSubscriber(subscriber);

		try {
			ccgwResponseStatus = ccgwClient.unsubscribe(sumaUnsubscriptionRequest);

		} catch (CcgwNotRespondingException e) {
			LOGGER.error("CCGW is unresponsive");

			throw new SumaCcgwUnresponsiveException();

		} catch (CcgwClientException e) {
			LOGGER.error("An error occured while calling CCGW {}", e.toString());

			mapCcgwErrorAndThrowSumaException(e);

		} catch (Exception e) {
			LOGGER.error("An unexpected error occured while calling CCGW", e);

			throw new SumaCcgwIntegrationErrorException();
		}

		return ccgwResponseStatus;
	}

	public String subscribe(SubscriptionDto subscriptionDto, String msisdn, String transactionId) {

		String subscriptionId = null;
		SumaSubscriptionRequest sumaSubscriptionRequest = new SumaSubscriptionRequest();
		sumaSubscriptionRequest = subscriptionMapper.map(subscriptionDto, SumaSubscriptionRequest.class);
		sumaSubscriptionRequest.setSubscriber(msisdn);
		sumaSubscriptionRequest.setTransactionId(transactionId);

		try {
			LOGGER.debug("Trying to call CCGW..");

			subscriptionId = ccgwClient.subscribe(sumaSubscriptionRequest);
		} catch (CcgwNotRespondingException e) {
			LOGGER.error("CCGW is unresponsive");

			throw new SumaCcgwUnresponsiveException();

		} catch (CcgwClientException e) {
			LOGGER.error("An error occured while calling CCGW {}", e.toString());

			mapCcgwErrorAndThrowSumaException(e);

		} catch (Exception e) {
			LOGGER.error("An unexpected error occured while calling CCGW {}", e);
			throw new SumaCcgwIntegrationErrorException();
		}

		return subscriptionId;
	}

	private void mapCcgwErrorAndThrowSumaException(CcgwClientException e) {
		String ccgwFaultStatusCode = e.getCcgwFaultStatusCode();
		LOGGER.debug("map CCGW error code {}", ccgwFaultStatusCode);

		IExceptionFactory exceptionFactory = null;
		for (String pattern : exceptionFactoryMapping.keySet()) {
			if (Pattern.matches(pattern, ccgwFaultStatusCode)) {
				LOGGER.debug("Found matching pattern {}", pattern);
				exceptionFactory = exceptionFactoryMapping.get(pattern);
			}
		}

		if (null == exceptionFactory) {
			throw new SumaCcgwIntegrationErrorException();
		}

		exceptionFactory.throwException(e);
	}

}
