package com.orange.paddock.suma.provider.rest;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.orange.paddock.suma.provider.log.NorthGetSubUnsubStatusLogger;
import com.orange.paddock.suma.provider.log.NorthSubscriptionLogger;
import com.orange.paddock.suma.provider.log.NorthUnsubscriptionLogger;
import com.orange.paddock.suma.provider.rest.model.RestSubscriptionResponse;

@RestController
@RequestMapping("subscription/v1")
public class SubscriptionRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRestController.class);

	private static String SUMA_ENDPOINT_SUBSCRIPTION = "subscription/v1/subscriptions/%s";

	@Autowired
	private SubscriptionManager manager;

	@Autowired
	private PdkLogIdBean loggerId;

	@Autowired
	private NorthSubscriptionLogger northSubscriptionLogger;

	@Autowired
	private NorthUnsubscriptionLogger northUnsubscriptionLogger;

	@Autowired
	private NorthGetSubUnsubStatusLogger northGetSubUnsubStatusLogger;

	@PostMapping("/subscriptions")
	public ResponseEntity<Void> subscribe(HttpServletRequest request, @RequestBody(required = true) SubscriptionDto body) throws AbstractSumaException {

		LOGGER.debug("Subscription request receive with service '{}' for endUser '{}'", body.getServiceId(), body.getEndUserId());

		northSubscriptionLogger.setInternalId(loggerId.getInternalId());
		northSubscriptionLogger.setRequestTimestamp(PdkDateUtils.getCurrentDateTimestamp());

		HttpHeaders headers = null;
		String endUserIdValue = "msisdn";
		String mco = null;
		try {
			if (!Objects.isNull(validateSubscriptionRequestInputs(body))) {
				LOGGER.error("Subscription -- request invalid input");
				throw new SumaBadRequestException(validateSubscriptionRequestInputs(body));
			}

			if (body.getEndUserId().equals(PdkAcrUtils.ACR_ORANGE_API_TOKEN)) {

				if (request.getHeader(PdkHeader.ORANGE_API_TOKEN) == null || "".equals(request.getHeader(PdkHeader.ORANGE_API_TOKEN))) {
					LOGGER.error("Subscription -- Missing header OrangeAPIToken");
					throw new SumaBadRequestException("Missing header Orange API Token.");
				}

				LOGGER.debug("Subscription -- EndUserId is an OAT with value is '{}'", request.getHeader(PdkHeader.ORANGE_API_TOKEN));

				northSubscriptionLogger.setOrangeApiToken(request.getHeader(PdkHeader.ORANGE_API_TOKEN));

				endUserIdValue = request.getHeader(PdkHeader.ORANGE_API_TOKEN);

			} else if (body.getEndUserId().equals(PdkAcrUtils.ACR_ISE2)) {

				if (request.getHeader(PdkHeader.ORANGE_ISE2) == null || "".equals(request.getHeader(PdkHeader.ORANGE_ISE2))) {
					LOGGER.error("Subscription -- Missing header ISE2");
					throw new SumaBadRequestException("Missing header ISE2.");
				}

				if (request.getHeader(PdkHeader.ORANGE_MCO) == null || "".equals(request.getHeader(PdkHeader.ORANGE_MCO))) {
					LOGGER.error("Subscription -- Missing required header MCO");
					throw new SumaBadRequestException("Missing required header MCO.");
				}

				LOGGER.debug("Subscription -- EndUserId is an ISE2, Header ISE2 value is '{}' and header MCO value is '{}'",
						request.getHeader(PdkHeader.ORANGE_ISE2), request.getHeader(PdkHeader.ORANGE_MCO));

				northSubscriptionLogger.setIse2(request.getHeader(PdkHeader.ORANGE_ISE2));
				northSubscriptionLogger.setMco(request.getHeader(PdkHeader.ORANGE_MCO));

				endUserIdValue = request.getHeader(PdkHeader.ORANGE_ISE2);
				mco = request.getHeader(PdkHeader.ORANGE_MCO);
			}

			LOGGER.debug("Inputs for subscribe method are OK !");

			northSubscriptionLogger.setServiceId(body.getServiceId());
			northSubscriptionLogger.setOnBehalfOf(body.getOnBehalfOf());
			northSubscriptionLogger.setEndUserId(body.getDescription());
			northSubscriptionLogger.setCategoryCode(body.getCategoryCode());
			northSubscriptionLogger.setAmount(String.valueOf(body.getAmount()));
			northSubscriptionLogger.setTaxedAmount(String.valueOf(body.getTaxedAmount()));
			northSubscriptionLogger.setCurrency(body.getCurrency());
			northSubscriptionLogger.setIsAdult(String.valueOf(body.getIsAdult()));

			LOGGER.debug("Subscription with EndUserIdValue: '{}'", endUserIdValue);
			SubscriptionResponse subId = manager.subscribe(body, endUserIdValue, mco);

			northSubscriptionLogger.setMsisdn(subId.getMsisdn());
			northSubscriptionLogger.setSubscriptionId(subId.getTransactionId());
			northSubscriptionLogger.setReturnedSubscriptionId(subId.getCcgwSubscriptionId());
			northSubscriptionLogger.setHttpResponseCode(HttpStatus.CREATED.toString());

			try {
				// build Location header
				RestSubscriptionResponse uriJsonParameter = new RestSubscriptionResponse();
				uriJsonParameter.setSubscriptionId(subId.getCcgwSubscriptionId());
				uriJsonParameter.setTransactionId(subId.getTransactionId());

				ObjectMapper mapper = new ObjectMapper();
				String uriJsonParameterToString = mapper.writeValueAsString(uriJsonParameter);
				String uriParameter = Base64.getEncoder().encodeToString(uriJsonParameterToString.getBytes(StandardCharsets.UTF_8));
				URI location = new URI(String.format(SUMA_ENDPOINT_SUBSCRIPTION, uriParameter));

				// creates response headers
				headers = new HttpHeaders();
				headers.setLocation(location);

			} catch (Exception e) {
				LOGGER.error("Build internal location header error: '{}'", e.getMessage());
				throw new SumaBadRequestException(e.getMessage());
			}

		} catch (AbstractSumaException e) {
			northSubscriptionLogger.setHttpResponseCode(String.valueOf(e.getHttpStatusCode()));
			northSubscriptionLogger.setInternalErrorCode(e.getInternalErrorCode());
			northSubscriptionLogger.setInternalErrorDescription(e.getErrorDescription());
			northSubscriptionLogger.setReturnedErrorCode(e.getErrorCode());
			throw e;
		} finally {
			northSubscriptionLogger.setResponseTimestamp(PdkDateUtils.getCurrentDateTimestamp());

			northSubscriptionLogger.write();
		}

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	@DeleteMapping("/subscriptions/{encodedSubscriptionId}")
	public ResponseEntity<Void> unsubscribe(HttpServletRequest request, @PathVariable String encodedSubscriptionId) throws AbstractSumaException {

		LOGGER.debug("Unsubscription request receive for subscriptionId '{}'", encodedSubscriptionId);

		/** Decode subscription Id **/
		ObjectMapper mapper = new ObjectMapper();
		RestSubscriptionResponse uriJsonParameter = new RestSubscriptionResponse();
		try {
			String decodedSubscriptionId = new String(Base64.getDecoder().decode(encodedSubscriptionId), "UTF-8");
			uriJsonParameter = mapper.readValue(decodedSubscriptionId, RestSubscriptionResponse.class);
		} catch (Exception e) {
			LOGGER.error("Decode Base64 subscriptionId error: '{}'", e.getMessage());
			throw new SumaBadRequestException();
		}

		/** Decoding OK get Tx ID**/
		String transactionId = uriJsonParameter.getTransactionId();
		northUnsubscriptionLogger.setInternalId(loggerId.getInternalId());
		northUnsubscriptionLogger.setRequestTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		northUnsubscriptionLogger.setSubscriptionId(transactionId);
		northUnsubscriptionLogger.setHttpResponseCode(String.valueOf(HttpStatus.NO_CONTENT));

		try {
			String status = manager.unsubscribe(transactionId);

			if (null != status && (status.equals(SubscriptionStatusUtils.STATUS_ARCHIVED) || status.equals(SubscriptionStatusUtils.STATUS_WAITING_ARCHIVING))) {
				northUnsubscriptionLogger.setIdempotency("true");
			} else {
				northUnsubscriptionLogger.setIdempotency("false");
			}
		} catch (AbstractSumaException e) {
			LOGGER.error("SUMA Functional error {}", e.getMessage());

			northUnsubscriptionLogger.setHttpResponseCode(String.valueOf(e.getHttpStatusCode()));
			northUnsubscriptionLogger.setInternalErrorCode(e.getInternalErrorCode());
			northUnsubscriptionLogger.setInternalErrorDescription(e.getErrorDescription());
			northUnsubscriptionLogger.setReturnedErrorCode(e.getErrorCode());
			throw e;
		} finally {
			northUnsubscriptionLogger.setResponseTimestamp(PdkDateUtils.getCurrentDateTimestamp());
			northUnsubscriptionLogger.write();
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/subscriptions/{encodedSubscriptionId}")
	public ResponseEntity<SubscriptionDto> getSubscriptionStatus(HttpServletRequest request, @PathVariable String encodedSubscriptionId) throws AbstractSumaException {

		northGetSubUnsubStatusLogger.setInternalId(loggerId.getInternalId());
		northGetSubUnsubStatusLogger.setRequestTimestamp(PdkDateUtils.getCurrentDateTimestamp());
		
		/** Decode subscription Id **/
		ObjectMapper mapper = new ObjectMapper();
		RestSubscriptionResponse uriJsonParameter = new RestSubscriptionResponse();
		try {
			String decodedSubscriptionId = new String(Base64.getDecoder().decode(encodedSubscriptionId), "UTF-8");
			uriJsonParameter = mapper.readValue(decodedSubscriptionId, RestSubscriptionResponse.class);
		} catch (Exception e) {
			LOGGER.error("Decode Base64 subscriptionId error: '{}'", e.getMessage());
			throw new SumaBadRequestException();
		}

		/** Decoding OK get Tx ID**/
		String transactionId = uriJsonParameter.getTransactionId();
		String subscriptionId = uriJsonParameter.getSubscriptionId();
		
		northGetSubUnsubStatusLogger.setSubscriptionId(subscriptionId);
		LOGGER.debug("Get subscription status for subId '{}' and TxId :{}", subscriptionId,transactionId);

		SubscriptionDto subscription = null;
		try {
			subscription = manager.getSubscriptionStatus(transactionId);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

			northGetSubUnsubStatusLogger.setCreationDate(formatter.format(subscription.getCreationDate()));
			northGetSubUnsubStatusLogger.setTransactionId(subscription.getTransactionId());
			northGetSubUnsubStatusLogger.setServiceId(subscription.getServiceId());
			northGetSubUnsubStatusLogger.setOnBehalfOf(subscription.getOnBehalfOf());
			northGetSubUnsubStatusLogger.setEndUserId(subscription.getEndUserId());
			northGetSubUnsubStatusLogger.setDescription(subscription.getDescription());
			northGetSubUnsubStatusLogger.setCategoryCode(subscription.getCategoryCode());
			northGetSubUnsubStatusLogger.setAmount(String.valueOf(subscription.getAmount()));
			northGetSubUnsubStatusLogger.setTaxedAmount(String.valueOf(subscription.getTaxedAmount()));
			northGetSubUnsubStatusLogger.setCurrency(subscription.getCurrency());
			northGetSubUnsubStatusLogger.setIsAdult(String.valueOf(subscription.getIsAdult()));
			northGetSubUnsubStatusLogger.setStatus(subscription.getStatus());

			northGetSubUnsubStatusLogger.setHttpResponseCode(String.valueOf(HttpStatus.OK));

		} catch (AbstractSumaException e) {
			northGetSubUnsubStatusLogger.setHttpResponseCode(String.valueOf(e.getHttpStatusCode()));
			northGetSubUnsubStatusLogger.setInternalErrorCode(e.getInternalErrorCode());
			northGetSubUnsubStatusLogger.setInternalErrorDescription(e.getErrorDescription());
			northGetSubUnsubStatusLogger.setReturnedErrorCode(e.getErrorCode());
			throw e;
		} finally {
			northGetSubUnsubStatusLogger.setResponseTimestamp(PdkDateUtils.getCurrentDateTimestamp());

			northGetSubUnsubStatusLogger.write();
		}

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
			if (!PdkAcrUtils.ACR_ISE2.equals(subscriptionDto.getEndUserId()) && !PdkAcrUtils.ACR_ORANGE_API_TOKEN.equals(subscriptionDto.getEndUserId())
					&& !subscriptionDto.getEndUserId().startsWith(PdkMsisdnUtils.PREFIX_TEL + PdkMsisdnUtils.PREFIX_PLUS)) {
				invalidValue = "endUserId format";
			}
		}

		return invalidValue;
	}

}