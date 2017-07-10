package com.orange.paddock.suma.business.manager.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@SpringBootApplication
@PropertySource("classpath:suma-business.properties")
@ImportResource("classpath:applicationContext-test.xml")
public class NotificationManagerTestApplication {
	
	public static void main(String[] args) {
        SpringApplication.run(NotificationManagerTestApplication.class, args);
    }

}
