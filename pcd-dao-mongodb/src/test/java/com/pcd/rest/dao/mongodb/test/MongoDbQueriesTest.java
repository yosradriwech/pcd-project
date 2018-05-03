package com.pcd.rest.dao.mongodb.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pcd.rest.dao.mongodb.document.User;
import com.pcd.rest.dao.mongodb.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongoDbTestApplication.class)
public class MongoDbQueriesTest {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(MongoDbQueriesTest.class);

	private static final String LOGIN = "4973";

	@Autowired
	private UserRepository subscriptionRepo;

	@Before
	public void clearBase() {
		subscriptionRepo.deleteAll();
	}

	@Test
	public void saveSubscriptionSuccessfulTest() {
		User sub = new User();
		sub.setLogin(LOGIN);
		User savedSession = subscriptionRepo.save(sub);
		Assert.assertEquals(LOGIN, savedSession.getLogin());

		User toUpdateSession = subscriptionRepo.findOneBySubscriptionId(LOGIN);
		toUpdateSession.setLogin("newLogin");
		;
		TECHNICAL_LOGGER.debug("Trying to update session");
		User savedSessionUpdated = subscriptionRepo.save(toUpdateSession);
		Assert.assertEquals("newLogin", savedSessionUpdated.getLogin());
	}

	@Test
	public void listSubscriptionsTest() {
		User subscription1 = new User();
		User subscription2 = new User();

		String id1 = subscriptionRepo.save(subscription1).getId();
		Assert.assertNotNull(id1);

		subscription2.setLogin(LOGIN);
		String id2 = subscriptionRepo.save(subscription2).getId();
		Assert.assertNotNull(id2);

		List<User> initialSubscriptionList = new ArrayList<User>() {
			private static final long serialVersionUID = 1L;

			{
				add(subscription1);
				add(subscription2);
			}
		};
		List<User> savedSubscriptionList = subscriptionRepo.findAll();

		TECHNICAL_LOGGER.info("Size of list of subscriptions for mongodb tests: {}", savedSubscriptionList.size());
		Assert.assertEquals(savedSubscriptionList, initialSubscriptionList);

	}

}
