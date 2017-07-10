package com.orange.paddock.suma.business.manager.test.unit;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.orange.paddock.suma.business.manager.NotificationManager;
import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.business.mapper.SubscriptionDtoMapper;
import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.ObjectFactory;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;
import com.orange.paddock.wtapi.client.WTApiClient;

/**
 * 
 * Configuration class for UNIT tests
 *
 */
@Configuration
public class SubscriptionUnitTestConfiguration {

	@Bean
	public SubscriptionDtoMapper subscriptionMapper() {
		return new SubscriptionDtoMapper();
	}

	@Bean
	public ObjectFactory subscriptionObjectFactory() {
		return new ObjectFactory();
	};

	@MockBean
	public CcgwClient ccgwClient;

	@MockBean
	public SubscriptionRepository subscriptionRepository;

	@MockBean
	public WTApiClient wtClient;

	@Bean
	public SubscriptionManager subscriptionManager() {
		return new SubscriptionManager();
	};
	
	@Bean
	public NotificationManager notificationManager() {
		return new NotificationManager();
	}
}
