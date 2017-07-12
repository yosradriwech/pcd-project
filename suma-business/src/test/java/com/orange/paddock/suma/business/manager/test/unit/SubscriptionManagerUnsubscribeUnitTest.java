package com.orange.paddock.suma.business.manager.test.unit;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.SumaAlreadyRevokedSubException;
import com.orange.paddock.suma.business.exception.SumaInternalErrorException;
import com.orange.paddock.suma.business.exception.SumaUnknownSubscriptionIdException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwIntegrationErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwInternalErrorException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwUnresponsiveException;
import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.business.manager.SubscriptionStatusUtils;
import com.orange.paddock.suma.business.manager.test.AbstractSubscriptionManagerTest;
import com.orange.paddock.suma.business.mapper.SubscriptionDtoMapper;
import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwNotRespondingException;
import com.orange.paddock.suma.consumer.ccgw.model.SumaUnsubscriptionRequest;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.ObjectFactory;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;
import com.orange.paddock.wtapi.client.WTApiClient;

/**
 * Unit tests for subscription manager (unsubscribe + getStatus)
 */
@SpringBootTest(classes = SubscriptionUnitTestConfiguration.class)
public class SubscriptionManagerUnsubscribeUnitTest extends AbstractSubscriptionManagerTest {

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

	private void initializeMongoDbMock(String status, Date creationDate) throws CcgwClientException, CcgwNotRespondingException {
		// MongoRepository successfully
		SubscriptionDto subscriptionDto = initializeValidSubscriptionDto();
		Subscription subscriptionSession = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSession.setCreationDate(creationDate);
		subscriptionSession.setStatus(status);
		subscriptionSession.setSubscriptionId(SUBSCRIPTION_ID);
		Mockito.when(subscriptionRepository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(subscriptionSession);

		Mockito.when(subscriptionRepository.save(Mockito.any(Subscription.class))).thenReturn(subscriptionSession);
	}

	@Test(expected = SumaUnknownSubscriptionIdException.class)
	/* Assumes that unsubscribe method is called with invalid subscriptionId */
	public void unsubscribeUnitUnknownIdTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected SumaUnknownSubscriptionIdException");
		Mockito.when(subscriptionRepository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(null);
		try {
			subscriptionManager.unsubscribe(SUBSCRIPTION_ID);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}
	}

	@Test(expected = SumaInternalErrorException.class)
	/* Assumes saved session with status = UNSUBSCRIPTION-ERROR */
	public void unsubscribeUnitUnsubErrorStatusTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected SumaInternalErrorException");
		initializeMongoDbMock(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR, new Date());

		try {
			subscriptionManager.unsubscribe(SUBSCRIPTION_ID);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}

	}

	@Test(expected = SumaAlreadyRevokedSubException.class)
	/* Assumes saved session with status = UNKNOWN_SUBSCRIPTION_ARCHIVED */
	public void unsubscribeUnitUnsubArchivedStatusTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected SumaInternalErrorException");
		initializeMongoDbMock(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED, new Date());

		try {
			subscriptionManager.unsubscribe(SUBSCRIPTION_ID);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}

	}

	@Test
	/* Assumes saved session with status = ARCHIVED */
	public void unsubscribeUnitArchivedStatusTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected status = status in mongo");
		initializeMongoDbMock(SubscriptionStatusUtils.STATUS_ARCHIVED, new Date());

		try {
			String status = subscriptionManager.unsubscribe(SUBSCRIPTION_ID);
			Assert.assertEquals(status, SubscriptionStatusUtils.STATUS_ARCHIVED);

		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}

	}

	@Test
	/* Assumes saved session with status = OTHER and CCGW OK */
	public void unsubscribeUnitSuccessfulCcgwTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected status = status in mongo");

		Date creationDate = new Date();
		initializeMongoDbMock(SubscriptionStatusUtils.STATUS_PENDING, creationDate);

		// Ccgw Client always returns a SUBSCRIPTION_ID
		Mockito.when(ccgwClient.unsubscribe(Mockito.any(SumaUnsubscriptionRequest.class))).thenReturn(true);

		try {
			String status = subscriptionManager.unsubscribe(SUBSCRIPTION_ID);
			initializeMongoDbMock(SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING, creationDate);
			Assert.assertEquals(status, SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}

	}

	@Test
	/* Assumes saved session with status = OTHER and CCGW KO */
	public void unsubscribeUnitFailCcgwTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected status = status in mongo");

		initializeMongoDbMock(SubscriptionStatusUtils.STATUS_PENDING, new Date());

		// Ccgw Client always returns a SUBSCRIPTION_ID
		Mockito.when(ccgwClient.unsubscribe(Mockito.any(SumaUnsubscriptionRequest.class))).thenReturn(false);

		try {
			String status = subscriptionManager.unsubscribe(SUBSCRIPTION_ID);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}

	}

	@Test(expected = SumaCcgwUnresponsiveException.class)
	/* Assumes saved session with status = OTHER and CCGW KO */
	public void unsubscribeUnitCcgwUnresponsiveTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected unsubscribe SumaCcgwUnresponsiveException");

		initializeMongoDbMock(SubscriptionStatusUtils.STATUS_PENDING, new Date());

		// Ccgw Client always returns a SUBSCRIPTION_ID
		Mockito.when(ccgwClient.unsubscribe(Mockito.any(SumaUnsubscriptionRequest.class))).thenThrow(CcgwNotRespondingException.class);

		try {
			subscriptionManager.unsubscribe(SUBSCRIPTION_ID);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}

	}

	@Test(expected = SumaCcgwInternalErrorException.class)
	/* Assumes saved session with status = OTHER and CCGW KO */
	public void unsubscribeUnitCcgwInternalErrorTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected unsubscribe SumaCcgwInternalErrorException");

		initializeMongoDbMock(SubscriptionStatusUtils.STATUS_PENDING, new Date());

		// Ccgw Client throws CcgwNotRespondingException
		CcgwClientException exception = new CcgwClientException();
		exception.setCcgwFaultStatusCode("321");
		Mockito.when(ccgwClient.unsubscribe(Mockito.any(SumaUnsubscriptionRequest.class))).thenThrow(exception);

		try {
			subscriptionManager.unsubscribe(SUBSCRIPTION_ID);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}

	}

	@Test(expected = SumaCcgwIntegrationErrorException.class)
	/* Assumes saved session with status = OTHER and CCGW KO */
	public void unsubscribeUnitCcgwIntegrationErrorTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected unsubscribe SumaCcgwIntegrationErrorException");
		initializeMongoDbMock(SubscriptionStatusUtils.STATUS_PENDING, new Date());

		// Ccgw Client throws CcgwNotRespondingException
		CcgwClientException exception = new CcgwClientException();
		exception.setCcgwFaultStatusCode("721");
		Mockito.when(ccgwClient.unsubscribe(Mockito.any(SumaUnsubscriptionRequest.class))).thenThrow(exception);

		try {
			subscriptionManager.unsubscribe(SUBSCRIPTION_ID);
		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}

	}

	@Test
	/* Assumes saved session with status = OTHER and CCGW KO */
	public void getStatusUnitSuccessfulTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected status = status in mongo");
		Date creationDate = new Date();
		initializeMongoDbMock(SubscriptionStatusUtils.STATUS_PENDING, creationDate);

		SubscriptionDto subscriptionDto = initializeValidSubscriptionDto();
		subscriptionDto.setCreationDate(creationDate);
		subscriptionDto.setStatus(SubscriptionStatusUtils.STATUS_PENDING);

		try {
			SubscriptionDto subscriptionDtoSession = subscriptionManager.getSubscriptionStatus(SUBSCRIPTION_ID);
			Assert.assertEquals(subscriptionDtoSession, subscriptionDto);

		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}

	}

	@Test(expected = SumaAlreadyRevokedSubException.class)
	/* Assumes saved session with status = OTHER and CCGW KO */
	public void getStatusUnitSessionAlreadyRevokedTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected get statusSumaAlreadyRevokedSubException");

		initializeMongoDbMock(SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING, new Date());
		try {
			subscriptionManager.getSubscriptionStatus(SUBSCRIPTION_ID);

		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}

	}

	@Test(expected = SumaUnknownSubscriptionIdException.class)
	/* Assumes saved session with status = OTHER and CCGW KO */
	public void getStatusUnitSessionNotFoundTest() throws CcgwClientException, CcgwNotRespondingException, AbstractSumaException {
		TECHNICAL_LOGGER.debug("Expected get status SumaUnknownSubscriptionIdException");
		Mockito.when(subscriptionRepository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(null);
		try {
			subscriptionManager.getSubscriptionStatus(SUBSCRIPTION_ID);

		} catch (AbstractSumaException e) {
			TECHNICAL_LOGGER.error("Subscribe unit TEST error " + e);
			throw e;
		}

	}

}
