package com.orange.paddock.suma.business.manager.test.unit;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.orange.paddock.commons.log.PdkLogIdBean;
import com.orange.paddock.suma.business.factory.CcgwIntegrationErrorExceptionFactory;
import com.orange.paddock.suma.business.factory.CcgwInternalErrorExceptionFactory;
import com.orange.paddock.suma.business.factory.IExceptionFactory;
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
	
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public PdkLogIdBean loggerId() {
		return new PdkLogIdBean();
	}
	
	@Bean
	public Map<String, IExceptionFactory> exceptionFactoryMapping() {
		Map<String, IExceptionFactory> exceptionFactory = new HashMap<>();
		exceptionFactory.put("(?!321|510|512|513)(^1[0-9]{2}$|^2[0-9]{2}$|^3[0-9]{2}$|^4[0-9]{2}$|^5[0-9]{2}$)", new CcgwIntegrationErrorExceptionFactory());
		exceptionFactory.put("(?!628|629)(^6[0-9]{2}$|321|510|512|513)", new CcgwInternalErrorExceptionFactory());
		
		return exceptionFactory;
	}
}
