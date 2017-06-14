package com.orange.paddock.suma.dao.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.orange.paddock.suma.dao.mongodb.document.Subscription;

public interface SubscriptionRepository extends MongoRepository<Subscription, String>{

}
