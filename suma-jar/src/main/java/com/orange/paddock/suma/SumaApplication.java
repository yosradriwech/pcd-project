package com.orange.paddock.mmswrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("file:./config/suma.properties")
public class SumaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SumaApplication.class, args);
	}
	
}
