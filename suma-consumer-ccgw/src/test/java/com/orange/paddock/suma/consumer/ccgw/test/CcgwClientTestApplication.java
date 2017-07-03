package com.orange.paddock.suma.consumer.ccgw.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.ObjectFactory;

@Configuration
@SpringBootApplication
@PropertySource("classpath:suma-ccgw.properties")
@ImportResource("classpath:applicationContext-test.xml")
public class CcgwClientTestApplication {
	
	@Bean 
	public CcgwClient ccgwClient () {
		return new CcgwClient();
	}
	
	@Bean
	public ObjectFactory subscriptionObjectFactory() {
		return new ObjectFactory();
	}
	

	public static void main(String[] args) {
		SpringApplication.run(CcgwClientTestApplication.class);
	}
}
