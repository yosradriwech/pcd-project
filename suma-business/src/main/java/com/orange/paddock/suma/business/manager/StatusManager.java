package com.orange.paddock.suma.business.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orange.paddock.suma.dao.mongodb.configuration.MongoDbConfiguration;

@Service
public class StatusManager {

	@Autowired
	private MongoDbConfiguration mongoDbConfiguration;
	
	public boolean checkMongoDb() {
		return mongoDbConfiguration.checkCollection();
	}
	
}
