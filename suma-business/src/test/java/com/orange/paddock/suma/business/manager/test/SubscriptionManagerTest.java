package com.orange.paddock.suma.business.manager.test;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.business.model.SubscriptionDto;

public class SubscriptionManagerTest extends AbstractSubscriptionManagerTest {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(SubscriptionManagerTest.class);

	@Autowired
	private SubscriptionManager subscriptionManager;

	@Test
	public void subscribeSuccessfulTest() {

		TECHNICAL_LOGGER.debug("Sending subscription request expected success");
		mockServerClient.when(request().withPath("/subscribe")).respond(
				response().withStatusCode(200).withBody(
						"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
								+ "<soapenv:Header/>" + "<soapenv:Body><ns:subscription-subscribe-response>" + "<ns:subscription-id>"
								+ new BigInteger(SUBSCRIPTION_ID) + "</ns:subscription-id><ns:status>" + "<ns:success>" + true
								+ "</ns:success></ns:status>"
								+ "<ns:vas-signature></ns:vas-signature></ns:subscription-subscribe-response></soapenv:Body></soapenv:Envelope>"));

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe TEST ERROOOOOOOOOR " + e);

		}
		TECHNICAL_LOGGER.debug("Test result : subscriptionId {}", subscriptionId);
		Assert.assertNotNull(subscriptionId);
	}
}