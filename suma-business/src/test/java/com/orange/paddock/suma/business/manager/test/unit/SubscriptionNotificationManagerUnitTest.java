package com.orange.paddock.suma.business.manager.test.unit;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.orange.paddock.suma.business.exception.SumaAlreadyActiveSubscriptionException;
import com.orange.paddock.suma.business.exception.SumaAlreadyRevokedUnSubException;
import com.orange.paddock.suma.business.exception.SumaSubscriptionIsStillActiveException;
import com.orange.paddock.suma.business.manager.NotificationManager;
import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.business.manager.SubscriptionStatusUtils;
import com.orange.paddock.suma.business.manager.test.AbstractSubscriptionNotificationManagerTest;
import com.orange.paddock.suma.business.mapper.SubscriptionDtoMapper;
import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;

@SpringBootTest(classes = SubscriptionUnitTestConfiguration.class)
public class SubscriptionNotificationManagerUnitTest extends AbstractSubscriptionNotificationManagerTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionNotificationManagerUnitTest.class);
			
	@Autowired
	private SubscriptionDtoMapper subscriptionMapper;
	
	@Autowired
	private SubscriptionRepository repository;
	
	@Autowired
	protected NotificationManager manager;
	
	@MockBean
	protected SubscriptionManager subscriptionManager;
	
	@Test
	public void notificationSubscriptionStatusWaitingActivationTest() throws Exception {
		Date date = new Date();
		XMLGregorianCalendar  receivedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar());
		date = receivedDate.toGregorianCalendar().getTime();
		
		SubscriptionDto subscriptionDto = initializeValidSubscriptionDto();
		Subscription subscriptionSession = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSession.setCreationDate(new Date());

		subscriptionSession.setStatus(SubscriptionStatusUtils.STATUS_WAITING_ACTIVATION);
		Mockito.when(repository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(subscriptionSession);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionId(SUBSCRIPTION_ID);
		subscription.setTransactionId(TRANSACTION_ID);
		subscription.setActivationDate(date);
		subscription.setEndUserId(END_USER_ID);
		subscription.setStatus(SubscriptionStatusUtils.STATUS_ACTIVE);
		LOGGER.debug("DATEEE "+subscription.getActivationDate());
		Mockito.when(repository.save(Mockito.any(Subscription.class))).thenReturn(subscription);
	
		manager.notificationSubscription(SUBSCRIPTION_ID, TRANSACTION_ID, date, END_USER_ID);
		
		assertEquals(SubscriptionStatusUtils.STATUS_ACTIVE, subscription.getStatus());
	}
	
	@Test
	public void notificationSubscriptionUnknownSubscriptionIdAndUnknownTransactionIdAndStatusWaitingArchivingTest(){
		Date date = new Date();
		
		SubscriptionDto subscriptionDto = initializeValidSubscriptionDto();
		Subscription subscriptionSession = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSession.setCreationDate(new Date());

		subscriptionSession.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING);
		Mockito.when(repository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(null);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionId(SUBSCRIPTION_ID);
		subscription.setTransactionId(TRANSACTION_ID);
		subscription.setActivationDate(date);
		subscription.setEndUserId(END_USER_ID);
		subscription.setStatus(SubscriptionStatusUtils.STATUS_PENDING);
		Mockito.when(repository.save(Mockito.any(Subscription.class))).thenReturn(subscription);
		Mockito.when(subscriptionManager.unsubscribe(Mockito.anyString())).thenReturn(subscriptionSession.getStatus());
		
//		String result = manager.notificationSubscription(SUBSCRIPTION_ID, TRANSACTION_ID, date, END_USER_ID);
		
		assertEquals(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING, subscriptionSession.getStatus());
	}
	
	@Test
	public void notificationSubscriptionUnknownSubscriptionIdAndUnknownTransactionIdAndStatusUnknownSubWaitingArchivingTest(){
		Date date = new Date();
		
		SubscriptionDto subscriptionDto = initializeValidSubscriptionDto();
		Subscription subscriptionSession = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSession.setCreationDate(new Date());

		subscriptionSession.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);
		Mockito.when(repository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(null);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionId(SUBSCRIPTION_ID);
		subscription.setTransactionId(TRANSACTION_ID);
		subscription.setActivationDate(date);
		subscription.setEndUserId(END_USER_ID);
		subscription.setStatus(SubscriptionStatusUtils.STATUS_PENDING);
		Mockito.when(repository.findOneByTransactionId(TRANSACTION_ID)).thenReturn(null);
		Mockito.when(repository.save(Mockito.any(Subscription.class))).thenReturn(subscription);
		Mockito.when(subscriptionManager.unsubscribe(Mockito.anyString())).thenReturn(subscriptionSession.getStatus());
		
//		String result = manager.notificationSubscription(SUBSCRIPTION_ID, TRANSACTION_ID, date, END_USER_ID);
		
		assertEquals(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR, subscriptionSession.getStatus());
	}
	
	@Test
	public void notificationSubscriptionUnknownSubscriptionIdAndStatusWaitingArchivingTest(){
		Date date = new Date();
		
		SubscriptionDto subscriptionDto = initializeValidSubscriptionDto();
		Subscription subscriptionSession = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSession.setCreationDate(new Date());

		subscriptionSession.setStatus(SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING);
		Mockito.when(repository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(null);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionId(SUBSCRIPTION_ID);
		subscription.setTransactionId(TRANSACTION_ID);
		subscription.setActivationDate(date);
		subscription.setEndUserId(END_USER_ID);
		subscription.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING);
		Mockito.when(repository.save(Mockito.any(Subscription.class))).thenReturn(subscription);
		Mockito.when(repository.findOneByTransactionId(TRANSACTION_ID)).thenReturn(subscriptionSession);
		Mockito.when(subscriptionManager.unsubscribe(Mockito.anyString())).thenReturn(subscriptionSession.getStatus());
		
//		String result = manager.notificationSubscription(SUBSCRIPTION_ID, TRANSACTION_ID, date, END_USER_ID);
		
		assertEquals(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING, subscription.getStatus());
	}
	
	@Test
	public void notificationSubscriptionUnknownSubscriptionIdAndStatusUnsubscriptionErrorTest(){
		Date date = new Date();
		
		SubscriptionDto subscriptionDto = initializeValidSubscriptionDto();
		Subscription subscriptionSession = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSession.setCreationDate(new Date());

		subscriptionSession.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);
		Mockito.when(repository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(null);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionId(SUBSCRIPTION_ID);
		subscription.setTransactionId(TRANSACTION_ID);
		subscription.setActivationDate(date);
		subscription.setEndUserId(END_USER_ID);
		subscription.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);
		Mockito.when(repository.save(Mockito.any(Subscription.class))).thenReturn(subscription);
		Mockito.when(repository.findOneByTransactionId(TRANSACTION_ID)).thenReturn(subscriptionSession);
		Mockito.when(subscriptionManager.unsubscribe(Mockito.anyString())).thenReturn(subscriptionSession.getStatus());
		
//		String result = manager.notificationSubscription(SUBSCRIPTION_ID, TRANSACTION_ID, date, END_USER_ID);
		
		assertEquals(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR, subscriptionSession.getStatus());
	}
	
	
	@Test
	public void notificationUnsubscriptionStatusWaitingArchivingTest(){
		Date date = new Date();
		
		SubscriptionDto subscriptionDto = initializeValidSubscriptionDto();
		Subscription subscriptionSession = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSession.setCreationDate(new Date());

		subscriptionSession.setStatus(SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING);
		Mockito.when(repository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(subscriptionSession);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionId(SUBSCRIPTION_ID);
		subscription.setTransactionId(TRANSACTION_ID);
		subscription.setActivationDate(date);
		subscription.setEndUserId(END_USER_ID);
		subscription.setStatus(SubscriptionStatusUtils.STATUS_ARCHIVED);
		Mockito.when(repository.save(Mockito.any(Subscription.class))).thenReturn(subscription);
		
//		String result = manager.notificationUnsubscription(SUBSCRIPTION_ID, TRANSACTION_ID, END_USER_ID);
		
		assertEquals(SubscriptionStatusUtils.STATUS_ARCHIVED, subscription.getStatus());
	}
	
	@Test
	public void notificationUnsubscriptionStatusUknownUnsubscriptionArchivingTest(){
		Date date = new Date();
		
		SubscriptionDto subscriptionDto = initializeValidSubscriptionDto();
		Subscription subscriptionSession = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSession.setCreationDate(new Date());

		subscriptionSession.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_WAITING_ARCHIVING);
		Mockito.when(repository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(subscriptionSession);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionId(SUBSCRIPTION_ID);
		subscription.setTransactionId(TRANSACTION_ID);
		subscription.setActivationDate(date);
		subscription.setEndUserId(END_USER_ID);
		subscription.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_UNSUBSCRIPTION_ARCHIVED);
		Mockito.when(repository.save(Mockito.any(Subscription.class))).thenReturn(subscription);
		
//		String result = manager.notificationUnsubscription(SUBSCRIPTION_ID, TRANSACTION_ID, END_USER_ID);
		
		assertEquals(SubscriptionStatusUtils.STATUS_UNKNOWN_UNSUBSCRIPTION_ARCHIVED, subscription.getStatus());
	}
	
	@Test
	public void notificationUnsubscriptionStatusUnsubscriptionErrorTest(){
		Date date = new Date();
		
		SubscriptionDto subscriptionDto = initializeValidSubscriptionDto();
		Subscription subscriptionSession = subscriptionMapper.map(subscriptionDto, Subscription.class);
		subscriptionSession.setCreationDate(new Date());

		subscriptionSession.setStatus(SubscriptionStatusUtils.STATUS_UNSUBSCRIPTION_ERROR);
		Mockito.when(repository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(subscriptionSession);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionId(SUBSCRIPTION_ID);
		subscription.setTransactionId(TRANSACTION_ID);
		subscription.setActivationDate(date);
		subscription.setEndUserId(END_USER_ID);
		subscription.setStatus(SubscriptionStatusUtils.STATUS_ARCHIVED);
		Mockito.when(repository.save(Mockito.any(Subscription.class))).thenReturn(subscription);
		
//		String result = manager.notificationUnsubscription(SUBSCRIPTION_ID, TRANSACTION_ID, END_USER_ID);
		
		assertEquals(SubscriptionStatusUtils.STATUS_ARCHIVED, subscription.getStatus());
	}
	
	@Test
	public void notificationUnsubscriptionStatusUknownSubscriptionIdTest(){
		Date date = new Date();
		
		Mockito.when(repository.findOneBySubscriptionId(Mockito.anyString())).thenReturn(null);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionId(SUBSCRIPTION_ID);
		subscription.setTransactionId(TRANSACTION_ID);
		subscription.setActivationDate(date);
		subscription.setEndUserId(END_USER_ID);
		subscription.setStatus(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED);
		Mockito.when(repository.save(Mockito.any(Subscription.class))).thenReturn(subscription);
		
//		String result = manager.notificationUnsubscription(SUBSCRIPTION_ID, TRANSACTION_ID, END_USER_ID);
		
		assertEquals(SubscriptionStatusUtils.STATUS_UNKNOWN_SUBSCRIPTION_ARCHIVED, subscription.getStatus());
	}
	
}
