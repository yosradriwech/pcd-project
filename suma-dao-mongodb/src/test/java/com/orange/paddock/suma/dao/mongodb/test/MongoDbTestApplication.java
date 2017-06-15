package com.orange.paddock.suma.dao.mongodb.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@SpringBootApplication
@PropertySource("classpath:suma-mongodb.properties")
@EnableMongoRepositories("com.orange.paddock.suma.dao.mongodb.repository")
@EnableMongoAuditing
public class MongoDbTestApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MongoDbTestApplication.class);
	}
}