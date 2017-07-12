package com.orange.paddock.suma.provider.rest;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Objects;

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

import com.orange.paddock.commons.date.PdkDateUtils;
import com.orange.paddock.commons.http.PdkHeader;
import com.orange.paddock.commons.log.PdkLogIdBean;
import com.orange.paddock.commons.msisdn.PdkMsisdnUtils;
import com.orange.paddock.commons.oneapi.PdkAcrUtils;
import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.SumaBadRequestException;
import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.business.manager.SubscriptionStatusUtils;
import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.business.model.SubscriptionResponse;
import com.orange.paddock.suma.provider.log.LogFields;
import com.orange.paddock.suma.provider.log.LoggerManager;
import com.orange.paddock.suma.provider.log.NorthGetSubUnsubStatusLogger;
import com.orange.paddock.suma.provider.log.NorthSubscriptionLogger;
import com.orange.paddock.suma.provider.log.NorthUnsubscriptionLogger;

@RestController
@RequestMapping("subscription/v1")
public class SubscriptionRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRestController.class);

	private static String SUMA_ENDPOINT_SUBSCRIPTION = "subscription/v1/subscriptions";

	@Autowired
	private SubscriptionManager manager;
	
	@Autowired
	private PdkLogIdBean loggerId;
	
	@Autowired
	private LoggerManager loggerManager;
	
	@Autowired
	private LogFields logFields;

	@PostMapping("/subscriptions")
	public ResponseEntity<URI> subscribe(HttpServletRequest request, @RequestBody(required=true) SubscriptionDto body)
			throws AbstractSumaException {

		LOGGER.debug("Subscription request receive with service '{}' for endUser '{}'", body.getServiceId(),
				body.getEndUserId());
		
		logFields.setInternalId(loggerId.getInternalId());
		logFields.setRequestTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		
		String endUserIdValue = "msisdn";
		String mco = null;
		
		if (body.getEndUserId().equals(PdkAcrUtils.ACR_ORANGE_API_TOKEN) && request.getHeader(PdkHeader.ORANGE_API_TOKEN) != null
				&& !"".equals(request.getHeader(PdkHeader.ORANGE_API_TOKEN))) {
			LOGGER.debug("EndUserId is an OAT and Header value is '{}'", request.getHeader(PdkHeader.ORANGE_API_TOKEN));
			
			logFields.setOrangeApiToken(request.getHeader(PdkHeader.ORANGE_API_TOKEN));
			
			endUserIdValue = request.getHeader(PdkHeader.ORANGE_API_TOKEN);

		} else if (body.getEndUserId().equals(PdkAcrUtils.ACR_ORANGE_API_TOKEN) && (request
				.getHeader(PdkHeader.ORANGE_API_TOKEN) == null	|| "".equals(request.getHeader(PdkHeader.ORANGE_API_TOKEN)))) {

			LOGGER.error("Missing header OrangeAPIToken");

			throw new SumaBadRequestException("Missing header Orange API Token.");
		}
		
		if (body.getEndUserId().equals(PdkAcrUtils.ACR_ISE2) && request.getHeader(PdkHeader.ORANGE_ISE2) != null
				&& !"".equals(request.getHeader(PdkHeader.ORANGE_ISE2))) {
			if (request.getHeader(PdkHeader.ORANGE_MCO) != null && !"".equals(request.getHeader(PdkHeader.ORANGE_MCO))) {

				LOGGER.debug("EndUserId is an ISE2, Header ISE2 value is '{}' and header MCO value is '{}'",
						request.getHeader(PdkHeader.ORANGE_ISE2), request.getHeader(PdkHeader.ORANGE_MCO));
				
				logFields.setIse2(request.getHeader(PdkHeader.ORANGE_ISE2));
				logFields.setMco(request.getHeader(PdkHeader.ORANGE_MCO));
				
				endUserIdValue = request.getHeader(PdkHeader.ORANGE_ISE2);
				mco = request.getHeader(PdkHeader.ORANGE_MCO);
			} else {
				LOGGER.error("Missing required header MCO");
				
				throw new SumaBadRequestException("Missing required header MCO.");
			}
		} else if (body.getEndUserId().equals(PdkAcrUtils.ACR_ISE2) && (request.getHeader(PdkHeader.ORANGE_ISE2) == null
				 || "".equals(request.getHeader(PdkHeader.ORANGE_ISE2)))) {
			LOGGER.error("Missing header ISE2");

			throw new SumaBadRequestException("Missing header ISE2.");
		}
		
		if (!Objects.isNull(validateSubscriptionRequestInputs(body))) {
			throw new SumaBadRequestException(validateSubscriptionRequestInputs(body));
		}
		
		LOGGER.debug("Inputs for subscribe method are OK !");
		
		logFields.setServiceId(body.getServiceId());
		logFields.setOnBehalfOf(body.getOnBehalfOf());
		logFields.setEndUserId(body.getDescription());
		logFields.setCategoryCode(body.getCategoryCode());
		logFields.setAmount(String.valueOf(body.getAmount()));
		logFields.setTaxedAmount(String.valueOf(body.getTaxedAmount()));
		logFields.setCurrency(body.getCurrency());
		logFields.setIsAdult(String.valueOf(body.getIsAdult()));
		
		LOGGER.debug("Subscription with EndUserIdValue: '{}'", endUserIdValue);
		SubscriptionResponse subId = manager.subscribe(body, endUserIdValue, mco);

		URI location = null;
		try {
			// build Location header
			location = new URI(String.format(SUMA_ENDPOINT_SUBSCRIPTION, subId.getCcgwSubscriptionId()));
		} catch (Exception e) {
			LOGGER.error("Build internal location header error: '{}'", e.getMessage());
			throw new SumaBadRequestException(e.getMessage());
		}
		
		
		logFields.setMsisdn(subId.getMsisdn());
		logFields.setSubscriptionId(subId.getSubscriptionId());
		logFields.setReturnedSubscriptionId(subId.getCcgwSubscriptionId());
		logFields.setHttpResponseCode(HttpStatus.CREATED.toString());
		logFields.setInternalErrorCode("PDK_SUMA_OK");
		logFields.setResponseTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		
		loggerManager.write(logFields);

		return new ResponseEntity<>(location, HttpStatus.CREATED);
	}

	@DeleteMapping("/subscriptions/{subscriptionId}")
	public ResponseEntity<Void> unsubscribe(HttpServletRequest request, @PathVariable String subscriptionId) throws AbstractSumaException {

		LOGGER.debug("Unsubscription request receive for subscriptionId '{}'", subscriptionId);
		
		logFields.setInternalId(loggerId.getInternalId());
		logFields.setRequestTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		logFields.setSubscriptionId(subscriptionId);
		logFields.setHttpResponseCode(String.valueOf(HttpStatus.NO_CONTENT));
		
		String status = manager.unsubscribe(subscriptionId);
		
		if(status == SubscriptionStatusUtils.STATUS_ARCHIVED || status == SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING){
			logFields.setIdempotency("true");
		} else {
			logFields.setIdempotency("false");
		}
		
		logFields.setResponseTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		loggerManager.write(logFields);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/subscriptions/{subscriptionId}")
	public ResponseEntity<SubscriptionDto> getSubscriptionStatus(HttpServletRequest request,
			@PathVariable String subscriptionId) throws AbstractSumaException {

		logFields.setInternalId(loggerId.getInternalId());
		logFields.setRequestTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		logFields.setSubscriptionId(subscriptionId);
		
		LOGGER.debug("Get subscription status for subscriptionId '{}'", subscriptionId);

		SubscriptionDto subscription = manager.getSubscriptionStatus(subscriptionId);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		logFields.setCreationDate(formatter.format(subscription.getCreationDate()));
		logFields.setTransactionId(subscription.getTransactionId());
		logFields.setServiceId(subscription.getServiceId());
		logFields.setOnBehalfOf(subscription.getOnBehalfOf());
		logFields.setEndUserId(subscription.getEndUserId());
		logFields.setDescription(subscription.getDescription());
		logFields.setCategoryCode(subscription.getCategoryCode());
		logFields.setAmount(String.valueOf(subscription.getAmount()));
		logFields.setTaxedAmount(String.valueOf(subscription.getTaxedAmount()));
		logFields.setCurrency(subscription.getCurrency());
		logFields.setIsAdult(String.valueOf(subscription.getIsAdult()));
		logFields.setStatus(subscription.getStatus());
		
		logFields.setHttpResponseCode(String.valueOf(HttpStatus.OK));
		
		logFields.setResponseTimestamp(PdkDateUtils.getCurrentDateTimestamp());	
		
		loggerManager.write(logFields);	

		return new ResponseEntity<SubscriptionDto>(subscription, HttpStatus.OK);
	}
	
	private String validateSubscriptionRequestInputs(SubscriptionDto subscriptionDto) {
		String invalidValue = null;

		if (Objects.isNull(subscriptionDto)) {
			invalidValue = "All mandatory fields";
		} else if (Objects.isNull(subscriptionDto.getServiceId()) || "".equals(subscriptionDto.getServiceId())) {
			invalidValue = "serviceId";
		} else if (Objects.isNull(subscriptionDto.getOnBehalfOf()) || "".equals(subscriptionDto.getOnBehalfOf())) {
			invalidValue = "onBehalfOf";
		} else if (Objects.isNull(subscriptionDto.getEndUserId()) || "".equals(subscriptionDto.getEndUserId())) {
			invalidValue = "endUserId";
		} else if (Objects.isNull(subscriptionDto.getDescription()) || "".equals(subscriptionDto.getDescription())) {
			invalidValue = "description";
		} else if (Objects.isNull(subscriptionDto.getCategoryCode()) || "".equals(subscriptionDto.getCategoryCode())) {
			invalidValue = "categoryCode";
		} else if (Objects.isNull(subscriptionDto.getAmount())) {
			invalidValue = "amount";
		} else if (Objects.isNull(subscriptionDto.getTaxedAmount())) {
			invalidValue = "taxedAmount";
		} else if (Objects.isNull(subscriptionDto.getCurrency()) || "".equals(subscriptionDto.getCurrency())) {
			invalidValue = "currency";
		} else if (Objects.isNull(subscriptionDto.getIsAdult())) {
			invalidValue = "isAdult";
		} else if (!Objects.isNull(subscriptionDto.getEndUserId())) {
			// check if endUserId is well formatted
			if (!PdkAcrUtils.ACR_ISE2.equals(subscriptionDto.getEndUserId())
					&& !PdkAcrUtils.ACR_ORANGE_API_TOKEN.equals(subscriptionDto.getEndUserId())
					&& !subscriptionDto.getEndUserId().startsWith(PdkMsisdnUtils.PREFIX_TEL + PdkMsisdnUtils.PREFIX_PLUS)) {
				invalidValue = "endUserId format";
			}
		}
		
		
		return invalidValue;
	}

}