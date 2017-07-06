package com.orange.paddock.suma;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import com.orange.paddock.commons.log.PdkLogIdBean;

@SpringBootApplication
@EnableAsync
@PropertySource("classpath:suma.properties")
@EnableMongoAuditing
@EnableMongoRepositories("com.orange.paddock.suma.dao.mongodb.repository")
public class SumaApplicationTest extends SpringBootServletInitializer{
	
	public static void main(String[] args) {
        SpringApplication.run(SumaApplicationTest.class, args);
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public PdkLogIdBean loggerId() {
            return new PdkLogIdBean();
    }
    
}
