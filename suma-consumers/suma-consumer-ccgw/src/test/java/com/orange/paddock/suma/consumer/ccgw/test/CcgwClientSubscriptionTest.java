package com.orange.paddock.suma.consumer.ccgw.test;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;
import com.orange.paddock.suma.consumer.ccgw.model.SumaSubscriptionRequest;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.Fault;

public class CcgwClientSubscriptionTest extends AbstractCcgwClientSubscriptionTest {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(CcgwClientSubscriptionTest.class);

	private static final String SALE_PROVIDER_ID = "Paddock";
	private static final String CONTENT_TYPE = "Testowe";
	private static final String CONTENT_NAME = "test_subskrypcja ";
	private static final BigDecimal PRICE = new BigDecimal(1.5);
	private static final boolean ADULT_FLAG = false;
	private static final String SUBSCRIPTION_ID = "4844";

	@Autowired
	private CcgwClient ccgwClient;

	@Test
	public void subscriptionSuccessTest() throws CcgwClientException, MalformedURLException, Fault {

		TECHNICAL_LOGGER.debug("Sending subscription request expected success");
		mockServerClient.when(request().withPath("/subscribe")).respond(
				response().withStatusCode(200).withBody(
						"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
								+ "<soapenv:Header/>" + "<soapenv:Body><ns:subscription-subscribe-response>" + "<ns:subscription-id>"
								+ new BigInteger(SUBSCRIPTION_ID) + "</ns:subscription-id><ns:status>" + "<ns:success>" + true
								+ "</ns:success></ns:status>"
								+ "<ns:vas-signature></ns:vas-signature></ns:subscription-subscribe-response></soapenv:Body></soapenv:Envelope>"));

		SumaSubscriptionRequest subReq = new SumaSubscriptionRequest();
		subReq.setAdultFlag(ADULT_FLAG);
		subReq.setAmount(PRICE);
		subReq.setContentName(CONTENT_NAME);
		subReq.setContentType(CONTENT_TYPE);
		subReq.setCurrency("EUR");
		subReq.setSaleProviderId(SALE_PROVIDER_ID);
		subReq.setProviderId(SALE_PROVIDER_ID);

		String subId = ccgwClient.subscribe(subReq);
		TECHNICAL_LOGGER.debug("Subscription test result in : {}", subId);
		Assert.assertNotNull(subId);
		
	}

	@Test(expected = CcgwClientException.class)
	public void subscriptionFailureTest() throws CcgwClientException, Fault {

		TECHNICAL_LOGGER.debug("Sending subscription request expected success set to false");
		mockServerClient.when(request().withPath("/subscribe")).respond(
				response().withStatusCode(200).withBody(
						"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
								+ "<soapenv:Header/><soapenv:Body><ns:subscription-subscribe-response><ns:subscription-id>" + new BigInteger("0")
								+ "</ns:subscription-id>" + "<ns:status>" + "<ns:success>" + false
								+ "</ns:success><ns:error-code>223</ns:error-code><ns:error-param>ss</ns:error-param></ns:status>"
								+ "<ns:vas-signature></ns:vas-signature></ns:subscription-subscribe-response></soapenv:Body></soapenv:Envelope>"));

		SumaSubscriptionRequest subReq = new SumaSubscriptionRequest();
		subReq.setAdultFlag(ADULT_FLAG);
		subReq.setAmount(PRICE);
		subReq.setContentName(CONTENT_NAME);
		subReq.setContentType(CONTENT_TYPE);
		subReq.setCurrency("EUR");
		subReq.setSaleProviderId(SALE_PROVIDER_ID);
		subReq.setProviderId(SALE_PROVIDER_ID);
		String subId = ccgwClient.subscribe(subReq);
		TECHNICAL_LOGGER.debug("Subscription test result in : {}", subId);
	}

	@Test(expected = CcgwClientException.class)
	public void subscriptionInternalErrorTest() throws CcgwClientException, Fault {

		TECHNICAL_LOGGER.debug("Sending subscription request expected 500");
		mockServerClient.when(request().withPath("/subscribe")).respond(
				response().withStatusCode(500).withBody(
						"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
								+ "<soapenv:Header/><soapenv:Body><ns:subscription-subscribe-response><ns:subscription-id>" + new BigInteger("0")
								+ "</ns:subscription-id>" + "<ns:status>" + "<ns:success>" + false
								+ "</ns:success><ns:error-code>223</ns:error-code><ns:error-param>ss</ns:error-param></ns:status>"
								+ "<ns:vas-signature></ns:vas-signature></ns:subscription-subscribe-response></soapenv:Body></soapenv:Envelope>"));

		SumaSubscriptionRequest subReq = new SumaSubscriptionRequest();
		subReq.setAdultFlag(ADULT_FLAG);
		subReq.setAmount(PRICE);
		subReq.setContentName(CONTENT_NAME);
		subReq.setContentType(CONTENT_TYPE);
		subReq.setCurrency("EUR");
		subReq.setSaleProviderId(SALE_PROVIDER_ID);
		subReq.setProviderId(SALE_PROVIDER_ID);
		String subId = ccgwClient.subscribe(subReq);
		TECHNICAL_LOGGER.debug("Subscription test result in : {}", subId);

	}

	@Test(expected = CcgwClientException.class)
	public void subscriptionBadRequestTest() throws CcgwClientException, Fault {

		TECHNICAL_LOGGER.debug("Sending subscription request expected 400");
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(400));

		SumaSubscriptionRequest subReq = new SumaSubscriptionRequest();
		subReq.setAdultFlag(ADULT_FLAG);
		subReq.setAmount(PRICE);
		subReq.setContentName(CONTENT_NAME);
		subReq.setContentType(CONTENT_TYPE);
		subReq.setCurrency("EUR");
		subReq.setSaleProviderId(SALE_PROVIDER_ID);
		subReq.setProviderId(SALE_PROVIDER_ID);
		String subId = ccgwClient.subscribe(subReq);
		TECHNICAL_LOGGER.debug("Subscription test result in : {}", subId);
	}

	@Test(expected = CcgwClientException.class)
	public void subscriptionSoapFaultTest() throws CcgwClientException, Fault {

		TECHNICAL_LOGGER.debug("Sending subscription request expected SOAP fault");
		mockServerClient
				.when(request().withPath("/subscribe"))
				.respond(
						response()
								.withStatusCode(500)
								.withBody(
										"<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/' xmlns:xsi='http://www.w3.org/1999/XMLSchema-instance' xmlns:xsd='http://www.w3.org/1999/XMLSchema'>"
												+ "<SOAP-ENV:Body><SOAP-ENV:Fault><faultcode xsi:type='xsd:string'>SOAP-ENV:Client</faultcode>"
												+ "<faultstring xsi:type='xsd:string'> SOME MESSAGE RECEIVED </faultstring>"
												+ "</SOAP-ENV:Fault></SOAP-ENV:Body></SOAP-ENV:Envelope>"));

		SumaSubscriptionRequest subReq = new SumaSubscriptionRequest();
		subReq.setAdultFlag(ADULT_FLAG);
		subReq.setAmount(PRICE);
		subReq.setContentName(CONTENT_NAME);
		subReq.setContentType(CONTENT_TYPE);
		subReq.setCurrency("EUR");
		subReq.setSaleProviderId(SALE_PROVIDER_ID);
		subReq.setProviderId(SALE_PROVIDER_ID);
		String subId = ccgwClient.subscribe(subReq);
		TECHNICAL_LOGGER.debug("Subscription test result in : {}", subId);
	}

}
