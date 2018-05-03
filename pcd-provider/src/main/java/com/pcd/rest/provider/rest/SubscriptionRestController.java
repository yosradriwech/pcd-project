package com.pcd.rest.provider.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pcd.rest.dao.mongodb.document.User;
import com.pcd.rest.manager.SubscriptionManager;
import com.pcd.rest.manager.exception.AbstractPcdException;

import java.util.Objects;

@RestController
@RequestMapping("subscriptions")
public class SubscriptionRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRestController.class);

	@Autowired
	private SubscriptionManager manager;

	@PostMapping("/users")
	public ResponseEntity<User> subscribe(HttpServletRequest request, @RequestBody(required = true) User body) throws AbstractPcdException {

		LOGGER.debug("Subscription request received with login {} and name {}", body.getLogin(), body.getName());
		User createdUser = null;
		try {

			createdUser = manager.subscribe(body);
		} catch (Exception e) {
			LOGGER.error("An error occured {}", e);
		}
		if  (Objects.isNull(createdUser))  { return new ResponseEntity<>(createdUser, HttpStatus.CONFLICT);}
		else {return new ResponseEntity<>(createdUser, HttpStatus.CREATED);}
	}

	@DeleteMapping("unsub/users/{login}")
	public ResponseEntity<Void> unsubscribe(HttpServletRequest request, @PathVariable String login){

		LOGGER.debug("Unsubscription request receive for login '{}'", login);

		try {
			String status = manager.unsubscribe(login);
		} catch (Exception e) {
			LOGGER.error("An error occured {}", e);
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/users/{login}")
	public ResponseEntity<User> getSubscriptionStatus(HttpServletRequest request, @PathVariable String login) throws AbstractPcdException {
		User user = null;
		try {
			user = manager.getSubscriptionStatus(login);
		} catch (Exception e) {
			LOGGER.error("An error occured {}", e);
		}

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

}