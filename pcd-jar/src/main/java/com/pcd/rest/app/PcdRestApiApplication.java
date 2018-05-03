package com.pcd.rest.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@PropertySource("file:config/pcd.properties")
@EnableMongoRepositories("com.pcd.rest.dao.mongodb.repository")
@EnableMongoAuditing
@EnableScheduling
public class PcdRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PcdRestApiApplication.class, args);
	}

	
}
