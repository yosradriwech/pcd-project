package com.orange.paddock.suma.provider.rest;

import java.net.URI;

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

import com.orange.paddock.commons.http.PdkHeader;
import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.SUMABadRequestException;
import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.business.model.SubscriptionDto;

@RestController
@RequestMapping("subscription/v1")
public class SubscriptionRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRestController.class);

	private static String SUMA_ENDPOINT_SUBSCRIPTION = "subscription/v1/subscriptions";

	@Autowired
	private SubscriptionManager manager;

	@PostMapping("/subscriptions")
	public ResponseEntity<URI> subscribe(HttpServletRequest request, @RequestBody SubscriptionDto body)
			throws AbstractSumaException {

		LOGGER.debug("Subscription request receive with service '{}' for endUser '{}'", body.getServiceId(),
				body.getEndUserId());

		String subscriptionId = null;
		String endUserIdValue = "msisdn";

		if (body.getEndUserId().equals(PdkHeader.ORANGE_API_TOKEN) && request.getHeader(PdkHeader.ORANGE_API_TOKEN) != null
				&& !"".equals(request.getHeader(PdkHeader.ORANGE_API_TOKEN))) {
			LOGGER.debug("EndUserId is an OAT and Header value is '{}'", request.getHeader(PdkHeader.ORANGE_API_TOKEN));
			endUserIdValue = request.getHeader(PdkHeader.ORANGE_API_TOKEN);

		} else if (body.getEndUserId().equals(PdkHeader.ORANGE_API_TOKEN) && (request
				.getHeader(PdkHeader.ORANGE_API_TOKEN) == null	|| "".equals(request.getHeader(PdkHeader.ORANGE_API_TOKEN)))) {

			LOGGER.error("Missing header OrangeAPIToken");
			throw new SUMABadRequestException("Missing header Orange API Token.");
		}

		if (body.getEndUserId().equals(PdkHeader.ORANGE_ISE2) && request.getHeader(PdkHeader.ORANGE_ISE2) != null
				&& !"".equals(request.getHeader(PdkHeader.ORANGE_ISE2))) {
			if (request.getHeader(PdkHeader.ORANGE_MCO) != null && !"".equals(request.getHeader(PdkHeader.ORANGE_MCO))) {

				LOGGER.debug("EndUserId is an ISE2, Header ISE2 value is '{}' and header MCO value is '{}'",
						request.getHeader(PdkHeader.ORANGE_ISE2), request.getHeader(PdkHeader.ORANGE_MCO));

				endUserIdValue = request.getHeader(PdkHeader.ORANGE_ISE2);
			} else {
				LOGGER.error("Missing required header MCO");
				throw new SUMABadRequestException("Missing required header MCO.");
			}
		} else if (body.getEndUserId().equals(PdkHeader.ORANGE_ISE2) && ((request.getHeader(PdkHeader.ORANGE_ISE2) == null)
				 || "".equals(request.getHeader(PdkHeader.ORANGE_ISE2)))) {
			LOGGER.error("Missing header ISE2");
			throw new SUMABadRequestException("Missing header ISE2.");
		}

		LOGGER.debug("Subscription with EndUserIdType: '{}'", endUserIdValue);
		subscriptionId = manager.subscribe(body, endUserIdValue,"mco");

		URI location = null;
		try {
			// build Location header
			location = new URI(String.format(SUMA_ENDPOINT_SUBSCRIPTION, subscriptionId));
		} catch (Exception e) {
			LOGGER.error("Build internal location header error: '{}'", e.getMessage());
			throw new SUMABadRequestException(e.getMessage());
		}

		return new ResponseEntity<>(location, HttpStatus.CREATED);
	}

	@DeleteMapping("/subscriptions/{subscriptionId}")
	public ResponseEntity<Void> unsubscribe(HttpServletRequest request, @PathVariable String subscriptionId) {

		LOGGER.debug("Unsubscription request receive for subscriptionId '{}'", subscriptionId);

		try {
			manager.unsubscribe(subscriptionId);
		} catch (AbstractSumaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/subscriptions/{subscriptionId}")
	public ResponseEntity<SubscriptionDto> getSubscriptionStatus(HttpServletRequest request,
			@PathVariable String subscriptionId) {

		LOGGER.debug("Get subscription status for subscriptionId '{}'", subscriptionId);

		SubscriptionDto subscription = null;
		try {
			subscription = manager.getSubscriptionStatus(subscriptionId);
		} catch (AbstractSumaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<SubscriptionDto>(subscription, HttpStatus.OK);
	}

}