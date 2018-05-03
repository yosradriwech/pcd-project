package com.pcd.rest.dao.mongodb.test;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.pcd.rest.dao.mongodb.repository.UserRepository;

@Configuration
@SpringBootApplication
@PropertySource("classpath:pcd-mongodb.properties")
@EnableMongoRepositories("com.pcd.rest.dao.mongodb.repository")
@EnableMongoAuditing
public class MongoDbTestApplication {
	
	@Autowired
	private UserRepository subscriptionRepository;

	@Before
	public void setUp() {
		subscriptionRepository.deleteAll();
	}

	public static void main(String[] args) {
		SpringApplication.run(MongoDbTestApplication.class);
	}
}
