package com.orange.paddock.suma.business.manager.test.unit;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.orange.paddock.commons.oneapi.PdkAcrUtils;
import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.SumaInternalErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwIntegrationErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwInternalErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwUnresponsiveException;
import com.orange.paddock.suma.business.exception.wt.SumaWtApiAuthenticationFailureException;
import com.orange.paddock.suma.business.exception.wt.SumaWtApiIntegrationException;
import com.orange.paddock.suma.business.exception.wt.SumaWtApiInternalErrorException;
import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.business.manager.SubscriptionStatusUtils;
import com.orange.paddock.suma.business.manager.test.AbstractSubscriptionManagerTest;
import com.orange.paddock.suma.business.mapper.SubscriptionDtoMapper;
import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwNotRespondingException;
import com.orange.paddock.suma.consumer.ccgw.model.SumaSubscriptionRequest;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.ObjectFactory;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;
import com.orange.paddock.wtapi.client.WTApiClient;
import com.orange.paddock.wtapi.exception.WTException;
import com.sun.xml.bind.v2.schemagen.xmlschema.List;

/**
 * Unit tests for subscription manager (subscribe)
 */
@SpringBootTest(classes = SubscriptionUnitTestConfiguration.class)
public class SubscriptionManagerSubscribeUnitTest extends AbstractSubscriptionManagerTest {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(SubscriptionManagerSubscribeUnitTest.class);

	protected static final String MSISDN = "msisdn";
	
	@Autowired
	private SubscriptionDtoMapper subscriptionMapper;

	@Autowired
	private ObjectFactory subscriptionObjectFactory;

	@Autowired
	private CcgwClient ccgwClient;

	@Autowired
	private WTApiClient wtClient;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private SubscriptionManager subscriptionManager;

	@SuppressWarnings("unchecked")
	private void initializeMocksForSuccessful() throws CcgwClientException, CcgwNotRespondingException {
		// WT mock returns valid MSISDN WT_MSISDN
		Map<String, String> wtRetrievedInfo = new HashMap<String, String>();
		wtRetrievedInfo.put(MSISDN, WT_MSISDN);
		TECHNICAL_LOGGER.debug("returned wt mock " + wtRetrievedInfo);
		Mockito.when(wtClient.getWassupInfos((java.util.List<String>) Mockito.any(List.class), Mockito.any(Map.class))).thenReturn(wtRetrievedInfo);

		// Ccgw Client always returns a SUBSCRIPTION_ID
		Mockito.when(ccgwClient.subscribe(Mockito.any(SumaSubscriptionRequest.class))).thenReturn(SUBSCRIPTION_ID);

		// MongoRepository successfully save sessions
		SubscriptionDto subscriptionDto = initializeValidSubscriptionDto();
		Subscription subscriptionSession = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSession.setCreationDate(new Date());
		Mockito.when(subscriptionRepository.save(Mockito.any(Subscription.class))).thenReturn(subscriptionSession);

		subscriptionSession.setStatus(SubscriptionStatusUtils.STATUS_PENDING);
		Mockito.when(subscriptionRepository.findOne(Mockito.anyString())).thenReturn(subscriptionSession);
	}

	@Test
	/* Assumes that subscribe method is called with valid subscriptionDto object and all south calls are OK */
	public void subscribeUnitSuccessfulTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected successful test : save subscription session and return SUBSCRIPTION_ID");
		initializeMocksForSuccessful();
		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}
		TECHNICAL_LOGGER.debug("Test result : subscriptionId {}", subscriptionId);
		Assert.assertEquals(subscriptionId, SUBSCRIPTION_ID);
	}

	@Test(expected = SumaWtApiIntegrationException.class)
	/* Assumes that subscribe method is called with endUserIdvalid=acr:OrangeAPIToken and WT response is NULL */
	public void subscribeUnitWtIntegrationException() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected SumaWtApiIntegrationException : WT KO");
		initializeMocksForSuccessful();
		Map<String, String> wtRetrievedInfo = new HashMap<String, String>();
		wtRetrievedInfo.put(MSISDN, null);
		Mockito.when(wtClient.getWassupInfos((java.util.List<String>) Mockito.any(List.class), Mockito.any(Map.class))).thenReturn(wtRetrievedInfo);

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		subscriptionDto.setEndUserId(PdkAcrUtils.ACR_ORANGE_API_TOKEN);
		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}
		Assert.assertNull(subscriptionId);
	}

	@Test
	/* Assumes that subscribe method is called with endUserIdvalid=acr:X-Orange-ISE2 */
	public void subscribeUnitIse2Test() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {

		TECHNICAL_LOGGER.debug("Expected successful test with endUserIdvalid=acr:X-Orange-ISE2");
		initializeMocksForSuccessful();

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		subscriptionDto.setEndUserId(PdkAcrUtils.ACR_ISE2);

		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
		} catch (AbstractSumaException e1) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e1);
			throw e1;
		}
		Assert.assertEquals(subscriptionId, SUBSCRIPTION_ID);
	}

	@Test(expected = SumaWtApiInternalErrorException.class)
	/* Assumes that subscribe method is called with endUserIdvalid=acr:OrangeAPIToken and WT call 500 */
	public void subscribeUnitWtApiInternalErrorTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected SumaWtApiIntegrationException : WT KO");
		initializeMocksForSuccessful();
		WTException wtException = new WTException();
		wtException.setHttpStatusCode(500);
		Mockito.when(wtClient.getWassupInfos((java.util.List<String>) Mockito.any(List.class), Mockito.any(Map.class))).thenThrow(wtException);

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		subscriptionDto.setEndUserId(PdkAcrUtils.ACR_ORANGE_API_TOKEN);

		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}
		Assert.assertNull(subscriptionId);
	}

	@Test(expected = SumaWtApiAuthenticationFailureException.class)
	/* Assumes that subscribe method is called with endUserIdvalid=acr:OrangeAPIToken and WT call 403 */
	public void subscribeUnitWtApiAuthenticationFailureTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected SumaWtApiIntegrationException : WT KO");
		initializeMocksForSuccessful();
		WTException wtException = new WTException();
		wtException.setHttpStatusCode(403);
		Mockito.when(wtClient.getWassupInfos((java.util.List<String>) Mockito.any(List.class), Mockito.any(Map.class))).thenThrow(wtException);

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		subscriptionDto.setEndUserId(PdkAcrUtils.ACR_ORANGE_API_TOKEN);

		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}
		Assert.assertNull(subscriptionId);
	}

	@Test(expected = SumaWtApiIntegrationException.class)
	/* Assumes that subscribe method is called with endUserIdvalid=acr:OrangeAPIToken and WT call 404 */
	public void subscribeUnitWtApiNotFoundTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected SumaWtApiIntegrationException : WT KO");
		initializeMocksForSuccessful();
		WTException wtException = new WTException();
		wtException.setHttpStatusCode(404);
		Mockito.when(wtClient.getWassupInfos((java.util.List<String>) Mockito.any(List.class), Mockito.any(Map.class))).thenThrow(wtException);

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		subscriptionDto.setEndUserId(PdkAcrUtils.ACR_ORANGE_API_TOKEN);

		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}
		Assert.assertNull(subscriptionId);
	}

	@Test(expected = SumaInternalErrorException.class)
	/* Assumes that Mongo Db is DOWN expected SumaInternalErrorException */
	public void subscribeUnitMongoKoTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected SumaInternalErrorException : Mongo down");
		initializeMocksForSuccessful();

		Mockito.when(subscriptionRepository.save(Mockito.any(Subscription.class))).thenReturn(null);
		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		subscriptionDto.setEndUserId(PdkAcrUtils.ACR_ORANGE_API_TOKEN);

		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
		} catch (AbstractSumaException e1) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e1);
			throw e1;
		}
		Assert.assertNull(subscriptionId);
	}

	@Test
	/* Assumes that CCGW returns NULL */
	public void subscribeUnitCcgwKoTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {

		TECHNICAL_LOGGER.debug("Test with CCGW response = NULL");
		initializeMocksForSuccessful();

		// Ccgw Client always returnsNULL
		Mockito.when(ccgwClient.subscribe(Mockito.any(SumaSubscriptionRequest.class))).thenReturn(null);

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		subscriptionDto.setEndUserId(PdkAcrUtils.ACR_ORANGE_API_TOKEN);

		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
			String statusMustBeSubscriptionError = subscriptionRepository.findOne(SUBSCRIPTION_ID).getStatus();
			Assert.assertEquals(statusMustBeSubscriptionError, SubscriptionStatusUtils.STATUS_SUBSCRIPTION_ERROR);
		} catch (AbstractSumaException e1) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e1);
			throw e1;
		}
		Assert.assertNull(subscriptionId);
	}

	@Test(expected = SumaCcgwUnresponsiveException.class)
	/* Assumes that CCGW is not responsive expected CcgwNotRespondingException */
	public void subscribeUnitCcgwUnresponsiveTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {

		TECHNICAL_LOGGER.debug("Expected CcgwNotRespondingException : CCGW unreachable");
		initializeMocksForSuccessful();

		// Ccgw Client throws CcgwNotRespondingException
		Mockito.when(ccgwClient.subscribe(Mockito.any(SumaSubscriptionRequest.class))).thenThrow(CcgwNotRespondingException.class);

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		subscriptionDto.setEndUserId(PdkAcrUtils.ACR_ORANGE_API_TOKEN);

		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
		} catch (AbstractSumaException e1) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e1);
			throw e1;
		}
		Assert.assertNull(subscriptionId);

	}

	@Test(expected = SumaCcgwInternalErrorException.class)
	/* Assumes that CCGW is not responsive expected CcgwNotRespondingException */
	public void subscribeUnitCcgwSubInternalErrorTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {

		TECHNICAL_LOGGER.debug("Expected CcgwClientException with status 600");
		initializeMocksForSuccessful();

		// Ccgw Client throws CcgwNotRespondingException
		CcgwClientException exception = new CcgwClientException();
		exception.setCcgwFaultStatusCode("600");
		Mockito.when(ccgwClient.subscribe(Mockito.any(SumaSubscriptionRequest.class))).thenThrow(exception);

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		subscriptionDto.setEndUserId(PdkAcrUtils.ACR_ORANGE_API_TOKEN);

		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
		} catch (AbstractSumaException e1) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e1);
			throw e1;
		}
		Assert.assertNull(subscriptionId);
	}

	@Test(expected = SumaCcgwIntegrationErrorException.class)
	/* Assumes that CCGW is not responsive expected CcgwNotRespondingException */
	public void subscribeUnitCcgwSubIntegrationErrorTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {

		TECHNICAL_LOGGER.debug("Expected SumaCcgwIntegrationErrorException with status 100");
		initializeMocksForSuccessful();

		// Ccgw Client throws CcgwNotRespondingException
		CcgwClientException exception = new CcgwClientException();
		exception.setCcgwFaultStatusCode("100");
		Mockito.when(ccgwClient.subscribe(Mockito.any(SumaSubscriptionRequest.class))).thenThrow(exception);

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto = initializeValidSubscriptionDto();
		subscriptionDto.setEndUserId(PdkAcrUtils.ACR_ORANGE_API_TOKEN);

		String subscriptionId = null;
		try {
			subscriptionId = subscriptionManager.subscribe(subscriptionDto, endUserIdValue, mco);
		} catch (AbstractSumaException e1) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e1);
			throw e1;
		}
		Assert.assertNull(subscriptionId);
	}

}
