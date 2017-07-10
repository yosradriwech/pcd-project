package com.orange.paddock.suma.dao.mongodb.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

import com.orange.paddock.suma.dao.mongodb.document.Subscription;

@Component
@EnableMongoAuditing
@EnableMongoRepositories
public class MongoDbConfiguration {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbConfiguration.class);
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public boolean checkCollection() {
		try {
			mongoTemplate.getCollectionName(Subscription.class);
		} catch (Exception e) {
			LOGGER.error("Unable to connect to the MongoDB instance", e);
			return false;
		}
		return true;
	}

}
