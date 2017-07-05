package com.orange.paddock.suma.business.manager.test;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwInternalErrorException;
import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.business.model.SubscriptionDto;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class SubscriptionManagerTest extends AbstractSubscriptionManagerTest {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(SubscriptionManagerTest.class);

	@Autowired
	private SubscriptionManager subscriptionManager;

	/**
	 * Test procedure :
	 *
	 * 1) field == OrangeApiToken || acr:X-Orange-ISE2
	 * 		# OrangeAPITokenHeader == null => Error 0001
	 * 		* Call WT to get MSISDN
	 * 			# IOSW error    => 2000
	 * 			# Timeout error => 2001
	 * 			# WT API    401 => 3001
	 * 			# WT API    403 => 3002
	 * 			# WT API    404 => 3001
	 * 			# WT API    500 => 3000
	 * 		* Check USSI MSISDN
	 * 	 		# Missing attr  => 3001
	 *		* Create record in DB
	 *		* Call CCGW
	 *			# Successful => update nosql record
	 *			# CCGW         1xx => 1002
	 *			# CCGW         2xx => 1002
	 *			# CCGW         321 => 1000
	 *			# CCGW         3xx => 1002
	 *			# CCGW         4xx => 1002
	 *			# CCGW 510/512/513 => 1000
	 *			# CCGW 		   5xx => 1002
	 *			# CCGW     628/629 => 1002
	 *			# CCGW 		   6xx => 1000
	 */

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

	/**
	//Error PDK SUMA 0001
	@Test
	public void subscribeEmptyOrangeAPITokenErrorTest() {

	}

	//Error PDK SUMA 2000
	@Test
	public void subscribeIOSWErrorTest() {

	}

	//Error PDK SUMA 2001
	@Test
	public void subscribeTimeOutErrorTest() {

	}

	//Error 3001
	@Test
	public void subscribeWT401ErrorTest() {

	}

	//Error 3002
	@Test
	public void subscribeWT403ErrorTest() {

	}

	//Error 3001
	@Test
	public void subscribe404ErrorTest() {

	}

	//Error 3000
	@Test
	public void subscribe500ErrorTest() {

	}

	//Error 3001
	@Test
	public void subscribeMissingUSSOErrorTest() {

	}

	//Error 1002
	@Test
	public void subscribeCCGWError1xxTest() {

	}

	//Error 1002
	@Test
	public void subscribeCCGWError2xxTest() {

	}

	//Error 1000
	@Test(expected = SumaCcgwInternalErrorException.class)
	public void subscribeCCGWError321Test() {

	}

	//Error 1002
	@Test
	public void subscribeCCGWError3xxTest() {

	}

	//Error 1002
	@Test
	public void subscribeCCGWError4xxTest() {

	}

	//Error 1000
	@Test(expected = SumaCcgwInternalErrorException.class)
	public void subscribeCCGWError510Test() {

	}

	//Error 1000
	@Test(expected = SumaCcgwInternalErrorException.class)
	public void subscribeCCGWError512Test() {

	}

	//Error 1000
	@Test(expected = SumaCcgwInternalErrorException.class)
	public void subscribeCCGWError513Test() {

	}

	//Error 1002
	@Test
	public void subscribeCCGWError5xxTest() {

	}

	//Error 1002
	@Test
	public void subscribeCCGWError628Test() {

	}

	//Error 1002
	@Test
	public void subscribeCCGWError629Test() {

	}

	//Error 1000
	@Test(expected = SumaCcgwInternalErrorException.class)
	public void subscribeCCGWError6xxTest() {

	}**/

}