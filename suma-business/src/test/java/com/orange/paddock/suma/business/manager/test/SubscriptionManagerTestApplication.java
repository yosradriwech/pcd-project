package com.orange.paddock.suma.business.manager.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.business.mapper.SubscriptionDtoMapper;
import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.ObjectFactory;
import com.orange.paddock.wtapi.client.WTApiClient;

@Configuration
@SpringBootApplication
@PropertySource("classpath:suma-business.properties")
@ImportResource("classpath:applicationContext-test.xml")
@EnableMongoAuditing
@EnableMongoRepositories("com.orange.paddock.suma.dao.mongodb.repository")
public class SubscriptionManagerTestApplication {
	
	@Bean
	public SubscriptionDtoMapper subscriptionMapper() {
		return new SubscriptionDtoMapper();
	};

	@Bean
	public SubscriptionManager subscriptionManager() {
		return new SubscriptionManager();
	}

	@Bean
	public ObjectFactory subscriptionObjectFactory() {
		return new ObjectFactory();
	}

	@Bean
	public CcgwClient ccgwClient() {
		return new CcgwClient();
	}

	@Bean
	public WTApiClient wtClient() {
		return new WTApiClient();
	}

	public static void main(String[] args) {
		SpringApplication.run(SubscriptionManagerTestApplication.class);
	}
}
