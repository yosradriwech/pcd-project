package com.orange.paddock.suma.consumer.ccgw.test;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.math.BigInteger;
import java.net.MalformedURLException;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwNotRespondingException;
import com.orange.paddock.suma.consumer.ccgw.model.SumaSubscriptionRequest;
import com.orange.paddock.suma.consumer.ccgw.model.SumaUnsubscriptionRequest;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.Fault;

public class CcgwClientUnsubscriptionTest extends AbstractCcgwClientSubscriptionTest {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(CcgwClientSubscriptionTest.class);

	private static final String SALE_PROVIDER_ID = "Paddock";
	private static final String SUBSCRIBER = "Paddock";
	private static final String SUBSCRIPTION_ID = "4844";

	@Autowired
	private CcgwClient ccgwClient;

	@Test
	public void unsubscriptionSuccessTest() throws CcgwClientException, MalformedURLException, Fault, CcgwNotRespondingException {

		TECHNICAL_LOGGER.debug("Sending unsubscription request expected success");
		mockServerClient.when(request().withPath("/subscribe")).respond(
				response().withStatusCode(200).withBody(
						"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
								+ "<soapenv:Header/>" + "<soapenv:Body><ns:subscription-unsubscribe-response><ns:status><ns:success>" + true
								+ "</ns:success></ns:status>"
								+ "<ns:vas-signature></ns:vas-signature></ns:subscription-unsubscribe-response></soapenv:Body></soapenv:Envelope>"));

		SumaUnsubscriptionRequest unsubReq = new SumaUnsubscriptionRequest();
		unsubReq.setProviderId(SALE_PROVIDER_ID);
		unsubReq.setSubscriber(SUBSCRIBER);
		unsubReq.setSubscriptionId(SUBSCRIPTION_ID);

		Boolean unsubscribeStatus = ccgwClient.unsubscribe(unsubReq);
		TECHNICAL_LOGGER.debug("Unsubscription test result in : {}", unsubscribeStatus);
		Assert.assertNotNull(unsubscribeStatus);

	}

	@Test(expected = CcgwClientException.class)
	public void unsubscriptionInternalErrorTest() throws CcgwClientException, Fault, CcgwNotRespondingException {

		TECHNICAL_LOGGER.debug("Sending subscription request expected 500");
		mockServerClient.when(request().withPath("/subscribe")).respond(
				response().withStatusCode(500).withBody(
						"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
								+ "<soapenv:Header/><soapenv:Body><ns:subscription-subscribe-response><ns:subscription-id>" + new BigInteger("0")
								+ "</ns:subscription-id>" + "<ns:status>" + "<ns:success>" + false
								+ "</ns:success><ns:error-code>223</ns:error-code><ns:error-param>ss</ns:error-param></ns:status>"
								+ "<ns:vas-signature></ns:vas-signature></ns:subscription-subscribe-response></soapenv:Body></soapenv:Envelope>"));

		SumaUnsubscriptionRequest unsubReq = new SumaUnsubscriptionRequest();
		unsubReq.setProviderId(SALE_PROVIDER_ID);
		unsubReq.setSubscriber(SUBSCRIBER);
		unsubReq.setSubscriptionId(SUBSCRIPTION_ID);

		Boolean unsubscribeStatus = ccgwClient.unsubscribe(unsubReq);
		TECHNICAL_LOGGER.debug("Unsubscription test result in : {}", unsubscribeStatus);
	}

	@Test(expected = CcgwClientException.class)
	public void unsubscriptionWithFaultInterceptorNotInvokedTest() throws CcgwClientException, Fault, CcgwNotRespondingException {

		TECHNICAL_LOGGER
				.debug("Sending subscription request with vas-signature value throwing SOAPFaultException, expected httpResponseCode null because response httpstatus is 200OK");
		mockServerClient
				.when(request().withPath("/subscribe"))
				.respond(
						response()
								.withStatusCode(200)
								.withBody(
										"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
												+ "<soapenv:Header/><soapenv:Body><ns:subscription-subscribe-response>"
												+ "<ns:status>"
												+ "<ns:success>"
												+ false
												+ "</ns:success><ns:error-code>223</ns:error-code><ns:error-param>ss</ns:error-param></ns:status>"
												+ "<ns:vas-signature>ssss</ns:vas-signature></ns:subscription-subscribe-response></soapenv:Body></soapenv:Envelope>"));

		SumaUnsubscriptionRequest unsubReq = new SumaUnsubscriptionRequest();
		unsubReq.setProviderId(SALE_PROVIDER_ID);
		unsubReq.setSubscriber(SUBSCRIBER);
		unsubReq.setSubscriptionId(SUBSCRIPTION_ID);

		Boolean unsubscribeStatus = ccgwClient.unsubscribe(unsubReq);
		TECHNICAL_LOGGER.debug("Unsubscription test result in : {}", unsubscribeStatus);
	}

}
