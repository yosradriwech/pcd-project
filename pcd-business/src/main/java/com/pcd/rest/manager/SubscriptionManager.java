package com.pcd.rest.manager;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcd.rest.dao.mongodb.document.User;
import com.pcd.rest.dao.mongodb.repository.UserRepository;
import com.pcd.rest.manager.exception.AbstractPcdException;


@Service
public class SubscriptionManager {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(SubscriptionManager.class);

	@Autowired
	private UserRepository userRepository;

	public User subscribe(User user){
 		TECHNICAL_LOGGER.debug("Starting subscription business");

 		User userSessionFound = userRepository.findOneByLogin(user.getLogin());

 		User subscriptionResponse = new User(user.getName(),user.getLastName(),user.getGender(),user.getAge(),user.getLogin(),user.getPassword());

 		if (Objects.isNull(userSessionFound)) {
			TECHNICAL_LOGGER.error("No subscription session found for '{}'",  user.getLogin());
			subscriptionResponse.setStatus("subscribed");
			userRepository.save(subscriptionResponse);
			return subscriptionResponse;
		}
		else { subscriptionResponse = null;
				return subscriptionResponse;}
	}

	public String unsubscribe(String login){
		TECHNICAL_LOGGER.debug("Starting unsubscription business logic with login '{}'", login);

		User userSessionFound = userRepository.findOneByLogin(login);

		if (Objects.isNull(userSessionFound)) {
			TECHNICAL_LOGGER.error("No subscription session found for '{}'",  login); }
		else
			userSessionFound.setStatus("unsubscribed");

		return userSessionFound.getStatus();
	}


	public User getSubscriptionStatus(String login){

		TECHNICAL_LOGGER.debug("Starting getStatus business logic with TxId '{}'", login);

		User userSessionFound = userRepository.findOneByLogin(login);
		if (Objects.isNull(userSessionFound)) {
			TECHNICAL_LOGGER.error("No session found for identifier {}", login);
			
		}

		
		TECHNICAL_LOGGER.debug("Return getStatus response:  {}", userSessionFound.toString());

		return userSessionFound;
	}

	
}
