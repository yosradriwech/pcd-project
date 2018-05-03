package com.pcd.rest.dao.mongodb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pcd.rest.dao.mongodb.document.User;


public interface UserRepository extends MongoRepository<User, String> {

	User findOneBySubscriptionId(String subscriptionId);

	List<User> findBySubscriptionIdAndStatus(String subscriptionId, String status);
	
	List<User> findByStatusIn(List<String> status);

	User findOneByLogin(String login);
	 User save(User user);
	 Void delete(User user);
}
