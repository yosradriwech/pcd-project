package com.orange.paddock.suma.business.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.orange.paddock.suma.business.mapper.SubscriptionDtoMapper;
import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;
import com.orange.paddock.wtapi.client.WTApiClient;

@Configuration
@ImportResource("classpath:applicationContext-business.xml")
public class SumaBusinessConfiguration {

	@Bean
	public SubscriptionDtoMapper subscriptionMapper() {
		return new SubscriptionDtoMapper();
	};

	@Bean
	public CcgwClient ccgwClient() {
		return new CcgwClient();
	}

	@Bean
	public WTApiClient wtClient() {
		return new WTApiClient();
	}
	
}
