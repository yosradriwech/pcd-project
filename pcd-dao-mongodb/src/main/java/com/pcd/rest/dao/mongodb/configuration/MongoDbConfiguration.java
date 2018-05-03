package com.pcd.rest.dao.mongodb.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

import com.pcd.rest.dao.mongodb.document.User;

@Component
@EnableMongoAuditing
@EnableMongoRepositories
public class MongoDbConfiguration {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbConfiguration.class);
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public boolean checkCollection() {
		try {
			mongoTemplate.getCollectionName(User.class);
		} catch (Exception e) {
			LOGGER.error("Unable to connect to the MongoDB instance", e);
			return false;
		}
		return true;
	}

}
