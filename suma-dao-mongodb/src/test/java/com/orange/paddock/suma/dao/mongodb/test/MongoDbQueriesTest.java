package com.orange.paddock.suma.dao.mongodb.test;

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
		Subscription result = subscriptionRepo.save(sub);
		TECHNICAL_LOGGER.info("Saved subscription Id : {}", result.getSubscriptionId());
	}

}
