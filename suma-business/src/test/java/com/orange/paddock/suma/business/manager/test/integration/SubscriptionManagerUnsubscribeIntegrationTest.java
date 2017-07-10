package com.orange.paddock.suma.business.manager.test.integration;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.math.BigInteger;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.orange.paddock.suma.business.exception.SumaAlreadyRevokedSubException;
import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.business.manager.SubscriptionStatusUtils;
import com.orange.paddock.suma.business.manager.test.AbstractSubscriptionManagerTest;
import com.orange.paddock.suma.business.model.SubscriptionDto;

@SpringBootTest(classes = SubscriptionManagerTestApplication.class)
public class SubscriptionManagerUnsubscribeIntegrationTest extends AbstractSubscriptionManagerTest {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(SubscriptionManagerUnsubscribeIntegrationTest.class);

	@Autowired
	private SubscriptionManager subscriptionManager;


	/**
	 * Unsubscribe method tests Successful
	 * 
	 */
	

	/**
	 * Get status unit tests Successful : returns found session PDK_SUMA_0003 : session with status UNKNOWN-UNSUBSCRIPTION-ARCHIVED PDK_SUMA_0002 : if
	 * no session found
	 * 
	 */

	@Test
	public void getStatusSuccessfultTest() {
		TECHNICAL_LOGGER.debug("Sending getStatus request expected success");
		// Start with Successful subscribe
		mockServerClient.when(request().withPath("/subscribe")).respond(
				response().withStatusCode(200).withBody(
						"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
								+ "<soapenv:Header/>" + "<soapenv:Body><ns:subscription-subscribe-response>" + "<ns:subscription-id>"
								+ new BigInteger(SUBSCRIPTION_ID) + "</ns:subscription-id><ns:status>" + "<ns:success>" + true
								+ "</ns:success></ns:status>"
								+ "<ns:vas-signature></ns:vas-signature></ns:subscription-subscribe-response></soapenv:Body></soapenv:Envelope>"));

		SubscriptionDto subscriptionReceived = initializeValidSubscriptionDto();

		String subscriptionId = subscriptionManager.subscribe(subscriptionReceived, endUserIdValue, mco);

		// Get status
		SubscriptionDto subscriptionSessionFound = subscriptionManager.getSubscriptionStatus(subscriptionId);

		// Following fields are set in MongoDb by SUMA
		subscriptionReceived.setStatus(SubscriptionStatusUtils.STATUS_WAITING_ACTIVATION);
		subscriptionReceived.setCreationDate(subscriptionSessionFound.getCreationDate());
		subscriptionReceived.setTransactionId(subscriptionSessionFound.getTransactionId());

		Assert.assertEquals(subscriptionReceived, subscriptionSessionFound);

	}


	@Test(expected = SumaAlreadyRevokedSubException.class)
	public void getStatus0003ErrorTest() {
		TECHNICAL_LOGGER.debug("Sending getStatus request expected SumaAlreadyRevokedSubException");

		// Start with Successful subscribe
		String subscriptionId = "45663";
		mockServerClient.when(request().withPath("/subscribe")).respond(
				response().withStatusCode(200).withBody(
						"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
								+ "<soapenv:Header/>" + "<soapenv:Body><ns:subscription-subscribe-response>" + "<ns:subscription-id>"
								+ new BigInteger(subscriptionId) + "</ns:subscription-id><ns:status>" + "<ns:success>" + true
								+ "</ns:success></ns:status>"
								+ "<ns:vas-signature></ns:vas-signature></ns:subscription-subscribe-response></soapenv:Body></soapenv:Envelope>"));

		SubscriptionDto subscriptionReceived = initializeValidSubscriptionDto();

		subscriptionId = subscriptionManager.subscribe(subscriptionReceived, endUserIdValue, mco);
		Assert.assertNotNull(subscriptionId);

		// Successful unsubscribe request to set status WAITING_ARCHIVING
		TECHNICAL_LOGGER.debug("Sending unsubscription request expected success");
		mockServerClient.reset();
		mockServerClient.when(request().withPath("/subscribe")).respond(
				response().withStatusCode(200).withBody(
						"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
								+ "<soapenv:Header/>" + "<soapenv:Body><ns:subscription-unsubscribe-response><ns:status><ns:success>" + true
								+ "</ns:success></ns:status>"
								+ "<ns:vas-signature></ns:vas-signature></ns:subscription-unsubscribe-response></soapenv:Body></soapenv:Envelope>"));
		
		String unsubscribeStatus = subscriptionManager.unsubscribe(subscriptionId);
		TECHNICAL_LOGGER.debug("Session status after unsubscription {}");
		Assert.assertEquals(unsubscribeStatus, SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING);
		
		// Get status
		subscriptionManager.getSubscriptionStatus(subscriptionId);

	}

}
