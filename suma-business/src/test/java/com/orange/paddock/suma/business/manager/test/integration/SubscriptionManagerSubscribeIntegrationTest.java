package com.orange.paddock.suma.business.manager.test.integration;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwIntegrationErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwInternalErrorException;
import com.orange.paddock.suma.business.exception.wt.SumaWtApiAuthenticationFailureException;
import com.orange.paddock.suma.business.exception.wt.SumaWtApiIntegrationException;
import com.orange.paddock.suma.business.exception.wt.SumaWtApiInternalErrorException;
import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.business.manager.test.AbstractSubscriptionManagerTest;
import com.orange.paddock.wtapi.client.WTApiClient;

@SpringBootTest(classes = SubscriptionManagerTestApplication.class)
public class SubscriptionManagerSubscribeIntegrationTest extends AbstractSubscriptionManagerTest {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(SubscriptionManagerSubscribeIntegrationTest.class);

	@Autowired
	private SubscriptionManager subscriptionManager;

	/**
	 * Test procedure :
	 * <p>
	 * 1) field == OrangeApiToken || acr:X-Orange-ISE2 # OrangeAPITokenHeader == null => Error 0001 * Call WT to get MSISDN # IOSW error => 2000 #
	 * Timeout error => 2001 # WT API 401 => 3001 # WT API 403 => 3002 # WT API 404 => 3001 # WT API 500 => 3000 * Check USSI MSISDN # Missing attr =>
	 * 3001 * Create record in DB * Call CCGW # Successful => update nosql record # CCGW 1xx => 1002 # CCGW 2xx => 1002 # CCGW 321 => 1000 # CCGW 3xx
	 * => 1002 # CCGW 4xx => 1002 # CCGW 510/512/513 => 1000 # CCGW 5xx => 1002 # CCGW 628/629 => 1002 # CCGW 6xx => 1000
	 */

	@Value("${orange.wt.iosw.credentials.login}")
	private String wtLogin;

	@Value("${orange.wt.iosw.credentials.password}")
	private String wtPass;

	@Value("${orange.wtpapi.default.serv}")
	private String wtDefaultService;

	@Autowired
	private WTApiClient wtApiClient;

	@Test
	public void subscribeSuccessfulTest() throws AbstractSumaException {

		TECHNICAL_LOGGER.debug("Sending subscription request expected success");
		mockServerClient.when(request().withPath("/subscribe")).respond(
				response().withStatusCode(200).withBody(
						"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
								+ "<soapenv:Header/>" + "<soapenv:Body><ns:subscription-subscribe-response>" + "<ns:subscription-id>"
								+ new BigInteger(SUBSCRIPTION_ID) + "</ns:subscription-id><ns:status>" + "<ns:success>" + true
								+ "</ns:success></ns:status>"
								+ "<ns:vas-signature></ns:vas-signature></ns:subscription-subscribe-response></soapenv:Body></soapenv:Envelope>"));

		mockServerClient.when(request().withPath("/WTR/ApiUserInfo-1/WT/ApiUserInfo")).respond(
				response().withStatusCode(200).withBody(
						"<WTResponse xmlns:xsi=\'http://www.w3.org/2001/XMLSchema-instance\'" + "xsi:noNamespaceSchemaLocation=\'wt.xsd\'>"
								+ " <identifiers>" + "  <ident name=\"mco\" value=\"\" />" + "  <ident name=\"msisdn\" value=\" \" />"
								+ "  <ident name=\"mss\" value=\" \" />" + " </identifiers>" + "</WTResponse>"));

		String subscriptionId = subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
		TECHNICAL_LOGGER.debug("Test result : subscriptionId {}", subscriptionId);
		Assert.assertNotNull(subscriptionId);
	}

	// Error 3001
	@Test(expected = SumaWtApiIntegrationException.class)
	public void subscribeWT401ErrorTest() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/WTR/ApiUserInfo-1/WT/ApiUserInfo")).respond(
				response().withStatusCode(401)
						.withBody(
								"<WTResponse xmlns:xsi=\'http://www.w3.org/2001/XMLSchema-instance\' xsi:noNamespaceSchemaLocation=\'wt.xsd\'>"
										+ " <error>" + "  <code>220</code>" + "  <message>info is missing or are not authorized</message>"
										+ " </error>" + "</WTResponse>"));
		subscriptionManager.subscribe(initializeValidSubscriptionDtoOAT(), endUserIdValue, mco);
	}

	// Error 3002
	@Test(expected = SumaWtApiAuthenticationFailureException.class)
	public void subscribeWT403ErrorTest() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/WTR/ApiUserInfo-1/WT/ApiUserInfo")).respond(
				response().withStatusCode(403).withBody(
						"<WTResponse xmlns:xsi=\'http://www.w3.org/2001/XMLSchema-instance\' xsi:noNamespaceSchemaLocation=\'wt.xsd\'>" + " <error>"
								+ "  <code>1000</code>" + "  <message>Authentification failed</message>" + " </error>" + "</WTResponse>"));
		subscriptionManager.subscribe(initializeValidSubscriptionDtoOAT(), endUserIdValue, mco);
	}

	// Error 3001 ok
	@Test(expected = SumaWtApiIntegrationException.class)
	public void subscribe404ErrorTest() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/WTR/ApiUserInfo-1/WT/ApiUserInfo")).respond(
				response().withStatusCode(404).withBody(
						"<WTResponse xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='wt.xsd'>"
								+ "<error><message>Ressource not found</message></error>" + "</WTResponse>"));
		subscriptionManager.subscribe(initializeValidSubscriptionDtoOAT(), endUserIdValue, mco);
	}

	// Error 3000
	@Test(expected = SumaWtApiInternalErrorException.class)
	public void subscribe500ErrorTest() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/WTR/ApiUserInfo-1/WT/ApiUserInfo")).respond(
				response().withStatusCode(500).withBody(
						"<WTResponse xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:noNamespaceSchemaLocation='wt.xsd'>"
								+ "<error><code>1001</code><message>....</message></error>" + "</WTResponse>"));
		subscriptionManager.subscribe(initializeValidSubscriptionDtoOAT(), endUserIdValue, mco);
	}

	// Error 3001 ok
	@Test(expected = SumaWtApiIntegrationException.class)
	public void subscribeMissingUSSOEmptyErrorTest() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/WTR/ApiUserInfo-1/WT/ApiUserInfo")).respond(
				response().withStatusCode(200).withBody(
						"<WTResponse xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + "xsi:noNamespaceSchemaLocation=\"wt.xsd\">"
								+ " <identifiers>" + "  <ident name=\"mco\" value=\"\" />" + "  <ident name=\"msisdn\" value=\" \" />"
								+ "  <ident name=\"mss\" value=\" \" />" + " </identifiers>" + "</WTResponse>"));
		subscriptionManager.subscribe(initializeValidSubscriptionDtoOAT(), endUserIdValue, mco);
	}

	// Error 1002
	// TODO: Test
	@Test(expected = SumaCcgwIntegrationErrorException.class)
	public void subscribeCCGWError1xxTest() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(200).withBody(getErrorResponseCCGW(100)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}

	// Error 1002
	@Test(expected = SumaCcgwIntegrationErrorException.class)
	public void subscribeCCGWError2xxTest() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(200).withBody(getErrorResponseCCGW(204)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}

	// Error 1000
	@Test(expected = SumaCcgwInternalErrorException.class)
	public void subscribeCCGWError321Test() throws AbstractSumaException {

		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(200).withBody(getErrorResponseCCGW(321)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}

	// Error 1002
	@Test(expected = SumaCcgwIntegrationErrorException.class)
	public void subscribeCCGWError3xxTest() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(200).withBody(getErrorResponseCCGW(301)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}

	// Error 1002
	@Test(expected = SumaCcgwIntegrationErrorException.class)
	public void subscribeCCGWError4xxTest() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(200).withBody(getErrorResponseCCGW(402)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}

	// Error 1000
	@Test(expected = SumaCcgwInternalErrorException.class)
	public void subscribeCCGWError510Test() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(500).withBody(getErrorResponseCCGW(510)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}

	// Error 1000
	@Test(expected = SumaCcgwInternalErrorException.class)
	public void subscribeCCGWError512Test() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(200).withBody(getErrorResponseCCGW(512)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}

	// Error 1000
	@Test(expected = SumaCcgwInternalErrorException.class)
	public void subscribeCCGWError513Test() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(200).withBody(getErrorResponseCCGW(513)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}

	// Error 1002
	@Test(expected = SumaCcgwIntegrationErrorException.class)
	public void subscribeCCGWError5xxTest() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(200).withBody(getErrorResponseCCGW(505)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}

	// Error 1002
	@Test(expected = SumaCcgwIntegrationErrorException.class)
	public void subscribeCCGWError628Test() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(200).withBody(getErrorResponseCCGW(628)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}

	// Error 1002
	@Test(expected = SumaCcgwIntegrationErrorException.class)
	public void subscribeCCGWError629Test() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(200).withBody(getErrorResponseCCGW(629)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}

	// Error 1000
	@Test(expected = SumaCcgwInternalErrorException.class)
	public void subscribeCCGWError6xxTest() throws AbstractSumaException {
		mockServerClient.when(request().withPath("/subscribe")).respond(response().withStatusCode(200).withBody(getErrorResponseCCGW(600)));

		subscriptionManager.subscribe(initializeValidSubscriptionDto(), endUserIdValue, mco);
	}
	
	// Error PDK SUMA 2000 TODO : Check if another way to filter this exception
	// @Test(expected = SumaIoswInternalErrorException.class)
	// public void subscribeIOSWErrorTest() throws AbstractSumaException {
	// given(wtApiClient.getWassupInfos(any(), any())).willThrow(WTHttpTransportException.class);
	// subscriptionManager.subscribe(initializeValidSubscriptionDtoOAT(), endUserIdValue, mco);
	// }

	// Error PDK SUMA 2001
	// @Test(expected = SumaIoswInternalErrorException.class)
	// public void subscribeTimeOutErrorTest() throws AbstractSumaException {
	// given(wtApiClient.getWassupInfos(any(), any())).willThrow(WTHttpTransportException.class);
	// subscriptionManager.subscribe(initializeValidSubscriptionDtoOAT(), endUserIdValue, mco);
	// }
	// Error 3001
	// @Test(expected = SumaWtApiIntegrationException.class)
	// public void subscribeMissingUSSONullErrorTest() throws AbstractSumaException {
	// HashMap hashMap = new HashMap();
	// given(wtApiClient.getWassupInfos(any(), any())).willReturn(hashMap);
	// subscriptionManager.subscribe(initializeValidSubscriptionDtoOAT(), endUserIdValue, mco);
	// }

}