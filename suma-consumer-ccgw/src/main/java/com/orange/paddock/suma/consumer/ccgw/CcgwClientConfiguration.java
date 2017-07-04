package com.orange.paddock.suma.consumer.ccgw;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.orange.paddock.suma.consumer.ccgw.susbcription.model.ObjectFactory;

@Configuration
@ImportResource("classpath:applicationContext-consumer-ccgw.xml")
public class CcgwClientConfiguration {

	@Bean
	public ObjectFactory subscriptionObjectFactory() {
		return new ObjectFactory();
	}
}
