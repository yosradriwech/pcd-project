package com.orange.paddock.suma.dao.mongodb.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.orange.paddock.suma.dao.mongodb.document.Subscription;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongoDbTestApplication.class)
public class MongoDbQueriesTest {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(MongoDbQueriesTest.class);

	private static final String END_USER_ID = "+99900000027501";
	private static final String SERVICE_ID = "suma-service";
	private static final String TRANSACTION_ID = "suma-transaction-id";
	private static final String SUBSCRIPTION_ID = "4973";
	private static final BigDecimal AMOUNT = new BigDecimal(1.0);
	private static final BigDecimal AMOUNT_1 = new BigDecimal(1.5);
	private static final BigDecimal TAXED_AMOUNT = new BigDecimal(0.5);

	@Autowired
	private SubscriptionRepository subscriptionRepo;

	@Before
	public void clearBase() {
		subscriptionRepo.deleteAll();
	}

	@Test
	public void saveSubscriptionSuccessfulTest() {
		Subscription sub = new Subscription();
		sub.setEndUserId(END_USER_ID);
		sub.setAmount(AMOUNT);
		sub.setTaxedAmount(TAXED_AMOUNT);
		sub.setServiceId(SERVICE_ID);
		sub.setTransactionId(TRANSACTION_ID);
		sub.setSubscriptionId(SUBSCRIPTION_ID);
		Subscription savedSession = subscriptionRepo.save(sub);
		Assert.assertEquals(SUBSCRIPTION_ID, savedSession.getSubscriptionId());
		Assert.assertEquals(AMOUNT, savedSession.getAmount());
		
		Subscription toUpdateSession = subscriptionRepo.findOneBySubscriptionId(SUBSCRIPTION_ID);
		toUpdateSession.setAmount(AMOUNT_1);
		TECHNICAL_LOGGER.debug("Trying to update session");
		Subscription savedSessionUpdated = subscriptionRepo.save(toUpdateSession);
		Assert.assertEquals(AMOUNT_1, savedSessionUpdated.getAmount());
	}
	

	@Test
	public void listSubscriptionsTest() {
		Subscription subscription1 = new Subscription();
		Subscription subscription2 = new Subscription();
		
		subscription1.setEndUserId(END_USER_ID);
		subscription1.setAmount(AMOUNT);
		subscription1.setTaxedAmount(TAXED_AMOUNT);
		subscription1.setServiceId(SERVICE_ID);
		subscription1.setTransactionId(TRANSACTION_ID);
		subscription1.setCurrency("currency1");
		String id1 = subscriptionRepo.save(subscription1).getId();
		Assert.assertNotNull(id1);
		
		subscription2.setEndUserId(END_USER_ID);
		subscription2.setAmount(AMOUNT_1);
		subscription2.setTaxedAmount(TAXED_AMOUNT);
		subscription2.setServiceId(SERVICE_ID);
		subscription2.setTransactionId(TRANSACTION_ID);
		subscription2.setCurrency("currency2");
		subscription2.setSubscriptionId(SUBSCRIPTION_ID);
		String id2 = subscriptionRepo.save(subscription2).getId();
		Assert.assertNotNull(id2);

		List<Subscription> initialSubscriptionList = new ArrayList<Subscription>() {
			private static final long serialVersionUID = 1L;

			{
				add(subscription1);
				add(subscription2);
			}
		};
		List<Subscription> savedSubscriptionList = subscriptionRepo.findAll();

		TECHNICAL_LOGGER.info("Size of list of subscriptions for mongodb tests: {}", savedSubscriptionList.size());
		TECHNICAL_LOGGER.info("Currency of Sub 1 : {}, currency of Sub 2 : {}", subscriptionRepo.findOne(id1).getCurrency(), subscriptionRepo
				.findOne(id2).getCurrency());
		Assert.assertEquals(savedSubscriptionList, initialSubscriptionList);
		
	}

}
