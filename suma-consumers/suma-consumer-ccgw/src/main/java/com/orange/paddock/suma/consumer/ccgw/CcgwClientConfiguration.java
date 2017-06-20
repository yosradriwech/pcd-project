package com.orange.paddock.suma.consumer.ccgw;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.orange.paddock.suma.consumer.ccgw.handlers.CcgwResponseHandler;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.ObjectFactory;

@Configuration
public class CcgwClientConfiguration {

	@Bean
	public CcgwResponseHandler ccgwResponseInterceptor() {
		return new CcgwResponseHandler();
	}
	
	@Bean
	public ObjectFactory subscriptionObjectFactory() {
		return new ObjectFactory();
	}
}
