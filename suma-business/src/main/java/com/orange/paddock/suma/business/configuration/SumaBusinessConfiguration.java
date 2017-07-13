package com.orange.paddock.suma.business.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.orange.paddock.commons.log.PdkLogIdBean;
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
	
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public PdkLogIdBean loggerId() {
		return new PdkLogIdBean();
	}
	
}
