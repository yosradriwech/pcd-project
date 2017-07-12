package com.orange.paddock.suma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@PropertySource("file:config/suma.properties")
@ImportResource("file:config/suma-applicationContext.xml")
@EnableMongoRepositories("com.orange.paddock.suma.dao.mongodb.repository")
@EnableMongoAuditing
public class SumaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SumaApplication.class, args);
	}
	
}
