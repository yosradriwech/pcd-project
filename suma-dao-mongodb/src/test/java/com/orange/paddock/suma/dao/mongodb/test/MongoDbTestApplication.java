package com.orange.paddock.suma.dao.mongodb.test;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;

@Configuration
@SpringBootApplication
@PropertySource("classpath:suma-mongodb.properties")
@EnableMongoRepositories("com.orange.paddock.suma.dao.mongodb.repository")
@EnableMongoAuditing
public class MongoDbTestApplication {
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Before
	public void setUp() {
		subscriptionRepository.deleteAll();
	}

	public static void main(String[] args) {
		SpringApplication.run(MongoDbTestApplication.class);
	}
}
