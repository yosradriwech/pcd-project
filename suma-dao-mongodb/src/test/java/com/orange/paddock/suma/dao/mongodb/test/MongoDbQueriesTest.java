package com.orange.paddock.suma.dao.mongodb.test;

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
	private static final Double AMOUNT = 1.0;
	private static final Double AMOUNT_1 = 1.5;
	private static final Double TAXED_AMOUNT = 0.5;

	@Autowired
	private SubscriptionRepository subscriptionRepo;

	@Before
	public void clearBase() {
		subscriptionRepo.deleteAll();
	}

	@Test
	public void saveSubscriptionTest() {
		Subscription sub = new Subscription();
		sub.setEndUserId(END_USER_ID);
		sub.setAmount(AMOUNT);
		sub.setTaxedAmount(TAXED_AMOUNT);
		sub.setServiceId(SERVICE_ID);
		sub.setTransactionId(TRANSACTION_ID);
		Subscription result = subscriptionRepo.save(sub);
		TECHNICAL_LOGGER.info("Saved subscription Id for mongodb tests: {}", result.getSubscriptionId());
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
		String id2 = subscriptionRepo.save(subscription2).getId();
		Assert.assertNotNull(id2);
		List<Subscription> subscriptions = subscriptionRepo.findAll();
		TECHNICAL_LOGGER.info("Size of list of subscriptions for mongodb tests: {}", subscriptions.size());
		TECHNICAL_LOGGER.info("Currency of Sub 1 : {}, currency of Sub 2 : {}", subscriptionRepo.findOne(id1).getCurrency(), subscriptionRepo
				.findOne(id2).getCurrency());

		Assert.assertEquals("currency1", subscriptionRepo.findOne(id1).getCurrency());
		Assert.assertEquals(subscriptionRepo.findOne(id2).getAmount(), 1.5);

	}

}
