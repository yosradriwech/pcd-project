package com.orange.paddock.suma.consumer.ccgw.client;

import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.cxf.transport.http.HTTPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import com.orange.paddock.commons.date.PdkDateUtils;
import com.orange.paddock.commons.msisdn.PdkMsisdnUtils;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwNotRespondingException;
import com.orange.paddock.suma.consumer.ccgw.log.CcgwSubscriptionLogger;
import com.orange.paddock.suma.consumer.ccgw.log.CcgwSubscriptionLogger.CcgwSubscriptionFields;
import com.orange.paddock.suma.consumer.ccgw.log.CcgwUnsubscriptionLogger;
import com.orange.paddock.suma.consumer.ccgw.log.CcgwUnsubscriptionLogger.CcgwUnsubscriptionFields;
import com.orange.paddock.suma.consumer.ccgw.model.SumaSubscriptionRequest;
import com.orange.paddock.suma.consumer.ccgw.model.SumaUnsubscriptionRequest;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.AuthenticationMethodType;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.AuthorizationType;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.CcgwSubscriptionPortType;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.ExtendedContentInfoType;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.Fault;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.ObjectFactory;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.PostSubscribeResponseType;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.Price;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.SaleModelType;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.SubscribeRequestType;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.SubscribeResponseType;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.SubscriptionModelType;
import com.orange.paddock.suma.consumer.ccgw.susbcription.model.UnubscribeRequestType;

public class CcgwClient {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(CcgwClient.class);

	private static final String SECRET_KEY_ALGORITHM = "HmacSHA256";
	private static final String KEY_TIMESTAMP = "timestamp=";
	private static final String KEY_PROVIDER_ID = "provider-id=";
	private static final String KEY_PROVIDER_PASS = "provider-pass=";
	private static final String KEY_SALE_PROVIDER_ID = "sale-provider-id=";
	private static final String KEY_TRANSACTION_ID = "transaction-id=";
	private static final String KEY_SUBSCRIBER = "subscriber=";
	private static final String KEY_AUTHENTICATION_METHOD = "authentication-method=";
	private static final String KEY_AUTHORIZATION_TYPE = "authorization-type=";
	private static final String KEY_CONTENT_NAME = "content-name=";
	private static final String KEY_CONTENT_TYPE = "content-type=";
	private static final String KEY_RATING_LEVEL = "rating-level=";
	private static final String KEY_ADULT_FLAG = "adult-flag=";
	private static final String KEY_SUBSCRIPTION_MODEL = "subscription-model=";
	private static final String KEY_SALE_MODEL = "sale-model=";
	private static final String KEY_AMOUNT = "amount=";
	private static final String KEY_TAXED_AMOUNT = "taxed-amount=";
	private static final String KEY_CURRENCY = "currency=";

	private static final String VAS_SEPARATOR = "&";

	private static final String CCGW_RESPONSE_STATUS_ERROR = "ERROR";

	@Value("${ccgw.provider.pass}")
	private String providerPass;

	@Value("${ccgw.authentication.method}")
	private String authenticationMethod;

	@Value("${ccgw.authorization.type}")
	private String authorizationType;

	@Value("${ccgw.rating.level}")
	private String ratingLevel;

	@Value("${ccwg.subscription.model}")
	private String subscriptionModel;

	@Value("${ccgw.sale.model}")
	private String saleModel;

	@Value("${ccgw.provider.secret}")
	private String providerSecret;

	@Autowired
	private CcgwSubscriptionPortType ccgwSubscriptionPort;

	private ObjectFactory subscriptionObjectFactory = new ObjectFactory();

	/**
	 * 
	 * @param sumaSubscriptionRequest
	 * @return
	 * @throws CcgwClientException
	 * @throws CcgwNotRespondingException
	 */
	public String subscribe(SumaSubscriptionRequest sumaSubscriptionRequest)
			throws CcgwClientException, CcgwNotRespondingException {

		TECHNICAL_LOGGER.debug("Starting subscription method from CCGW client...");
		// Logs
		Map<CcgwSubscriptionFields, String> logs = new HashMap<CcgwSubscriptionFields, String>();
		String ccgwErrorCode = "", subscriptionId = null;
		Integer httpResponseCode = 0;
		List<String> ccgwErrorParams = new ArrayList<String>();

		logs.put(CcgwSubscriptionFields.INTERNAL_ID, UUID.randomUUID().toString());
		logs.put(CcgwSubscriptionFields.REQUEST_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());

		// Received request = null
		Objects.requireNonNull(sumaSubscriptionRequest);

		TECHNICAL_LOGGER.debug("Received sumaSubscriptionRequest with fields {}", sumaSubscriptionRequest.toString());

		ExtendedContentInfoType contentInfo = subscriptionObjectFactory.createExtendedContentInfoType();
		Price contentInfoPrice = subscriptionObjectFactory.createPrice();
		SubscribeRequestType subscribeRequestType = subscriptionObjectFactory.createSubscribeRequestType();

		contentInfo.setContentName(sumaSubscriptionRequest.getContentName());
		contentInfo.setContentType(sumaSubscriptionRequest.getContentType());
		contentInfo.setRatingLevel(ratingLevel);
		contentInfo.setAdultFlag(sumaSubscriptionRequest.getAdultFlag());
		contentInfo.setSubscriptionModel(SubscriptionModelType.valueOf(subscriptionModel));
		contentInfo.setSaleModel(SaleModelType.valueOf(saleModel));
		contentInfoPrice.setAmount(sumaSubscriptionRequest.getAmount());
		contentInfoPrice.setTaxedAmount(sumaSubscriptionRequest.getTaxedAmount());
		contentInfoPrice.setCurrency(sumaSubscriptionRequest.getCurrency());
		contentInfo.setPrice(contentInfoPrice);
		subscribeRequestType.setProviderId(sumaSubscriptionRequest.getProviderId());
		subscribeRequestType.setProviderPass(providerPass);
		subscribeRequestType.setSaleProviderId(sumaSubscriptionRequest.getSaleProviderId());
		subscribeRequestType.setTransactionId(sumaSubscriptionRequest.getTransactionId());
		subscribeRequestType
				.setSubscriber(PdkMsisdnUtils.getMsisdnWithoutPrefix(sumaSubscriptionRequest.getSubscriber()));
		subscribeRequestType.setAuthenticationMethod(AuthenticationMethodType.valueOf(authenticationMethod));
		subscribeRequestType.setAuthorizationType(AuthorizationType.valueOf(authorizationType));
		subscribeRequestType.setContentInfo(contentInfo);
		subscribeRequestType.setTimestamp(System.currentTimeMillis() / 1000L);

		String stringToSign = buildSubscriptionRequestString(subscribeRequestType);
		byte[] vasSignature = calculateVasSignature(providerSecret, stringToSign);
		TECHNICAL_LOGGER.debug("VAS SIgnature parameter: {}", String.valueOf(vasSignature));
		subscribeRequestType.setVasSignature(vasSignature);

		logs.put(CcgwSubscriptionFields.RATING_LEVEL, ratingLevel);
		logs.put(CcgwSubscriptionFields.ADULT_FLAG, String.valueOf(sumaSubscriptionRequest.getAdultFlag()));
		logs.put(CcgwSubscriptionFields.SUBSCRIPTION_MODEL, subscriptionModel);
		logs.put(CcgwSubscriptionFields.SALE_MODEL, saleModel);
		logs.put(CcgwSubscriptionFields.AMOUNT, String.valueOf(sumaSubscriptionRequest.getAmount()));
		logs.put(CcgwSubscriptionFields.TAXED_AMOUNT, String.valueOf(sumaSubscriptionRequest.getTaxedAmount()));
		logs.put(CcgwSubscriptionFields.CURRENCY, sumaSubscriptionRequest.getCurrency());
		logs.put(CcgwSubscriptionFields.PROVIDER_ID, sumaSubscriptionRequest.getProviderId());
		logs.put(CcgwSubscriptionFields.PROVIDER_PASS, providerPass);
		logs.put(CcgwSubscriptionFields.SALE_PROVIDER_ID, sumaSubscriptionRequest.getSaleProviderId());
		logs.put(CcgwSubscriptionFields.TRANSACTION_ID, sumaSubscriptionRequest.getTransactionId());
		logs.put(CcgwSubscriptionFields.SUBSCRIBER, sumaSubscriptionRequest.getSubscriber());
		logs.put(CcgwSubscriptionFields.AUTHENTICATION_METHOD, authenticationMethod);
		logs.put(CcgwSubscriptionFields.AUTHORIZATION_TYPE, authorizationType);
		logs.put(CcgwSubscriptionFields.CONTENT_NAME, sumaSubscriptionRequest.getContentName());
		logs.put(CcgwSubscriptionFields.CONTENT_TYPE, sumaSubscriptionRequest.getContentType());
		logs.put(CcgwSubscriptionFields.TIMESTAMP, String.valueOf(subscribeRequestType.getTimestamp()));
		logs.put(CcgwSubscriptionFields.VAS_SIGNATURE, new String(subscribeRequestType.getVasSignature()));

		try {
			// Call CCGW remote service
			SubscribeResponseType subscribeResponseType = ccgwSubscriptionPort.subscribe(subscribeRequestType);
			logs.put(CcgwSubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());

			// Check if subscribe response in not null
			if (!Objects.isNull(subscribeResponseType)) {
				// Retrieve subscription Id
				if (!Objects.isNull(subscribeResponseType.getSubscriptionId())) {
					subscriptionId = String.valueOf(subscribeResponseType.getSubscriptionId());
				}

				httpResponseCode = HttpStatus.OK.value();

				// Check if status object is not NULL
				if (!Objects.isNull(subscribeResponseType.getStatus())) {
					logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS,
							String.valueOf(subscribeResponseType.getStatus().isSuccess()));
					if (!subscribeResponseType.getStatus().isSuccess()) {
						// CCGW response with failure throw CcgwClientException
						TECHNICAL_LOGGER.debug("Error occured in CcgwClient - SUBSCRIBE error response");

						if (!Objects.isNull(subscribeResponseType.getStatus().getErrorParam())) {
							ccgwErrorParams = subscribeResponseType.getStatus().getErrorParam();
						}
						if (!Objects.isNull(subscribeResponseType.getStatus().getErrorCode())) {
							ccgwErrorCode = subscribeResponseType.getStatus().getErrorCode().getValue();
						}

						logs.put(CcgwSubscriptionFields.CCGW_ERROR_CODE, ccgwErrorCode);
						logs.put(CcgwSubscriptionFields.CCGW_ERROR_PARAMS, String.join(",", ccgwErrorParams));

						throw new CcgwClientException(httpResponseCode, ccgwErrorCode, ccgwErrorParams);
					}
				}
			} // response null
			else {
				logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);
			}

			logs.put(CcgwSubscriptionFields.SUBSCRIPTION_ID, subscriptionId);
			logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));

		} catch (Fault fault) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient - SUBSCRIBE service fault");

			httpResponseCode = HttpStatus.OK.value();
			if (!Objects.isNull(fault.getFaultInfo().getStatus())) {
				logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS,
						String.valueOf(fault.getFaultInfo().getStatus().isSuccess()));
				if (!Objects.isNull(fault.getFaultInfo().getStatus().getErrorParam())) {
					ccgwErrorParams = fault.getFaultInfo().getStatus().getErrorParam();
				}
				if (!Objects.isNull(fault.getFaultInfo().getStatus().getErrorCode())) {
					ccgwErrorCode = fault.getFaultInfo().getStatus().getErrorCode().getValue();
				}
			}

			logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));
			logs.put(CcgwSubscriptionFields.CCGW_ERROR_CODE, ccgwErrorCode);
			logs.put(CcgwSubscriptionFields.CCGW_ERROR_PARAMS, String.join(",", ccgwErrorParams));

			throw new CcgwClientException(httpResponseCode, ccgwErrorCode, ccgwErrorParams);

		} catch (SOAPFaultException soapFaultException) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient subscribe - SOAPFaultException");

			httpResponseCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
			logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));
			logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);

			CcgwClientException ccgwClientException = new CcgwClientException(
					"Error occured in CcgwClient subscribe - SOAPFaultException");
			ccgwClientException.setSoapFaultHttpStatusCode(httpResponseCode);
			ccgwClientException.setSoapFaultCode(soapFaultException.getFault().getFaultCode());
			ccgwClientException.setSoapFaultMessage(soapFaultException.getFault().getFaultString());

			throw ccgwClientException;

		} catch (javax.xml.ws.WebServiceException webServiceException) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient subscribe - WebServiceException" + webServiceException);
			CcgwClientException ccgwClientException = new CcgwClientException(
					"Error occured in CcgwClient subscribe - WebServiceException");

			if ((null != webServiceException.getCause()) && (webServiceException.getCause() instanceof HTTPException)) {
				TECHNICAL_LOGGER.debug("Error due to http conduit exception");
				HTTPException e = (HTTPException) webServiceException.getCause();
				ccgwClientException.setCcgwFaultHttpStatusCode(e.getResponseCode());
				logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE,
						String.valueOf(ccgwClientException.getCcgwFaultHttpStatusCode()));
			}

			logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);

			throw ccgwClientException;

		} catch (CcgwClientException ccgwClientException) {
			throw ccgwClientException;
		} catch (Exception e) {
			TECHNICAL_LOGGER.debug("An unexpected exception has occred while requesting ccgw");

			logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);

			if (e.getCause() instanceof SocketTimeoutException) {
				throw new CcgwNotRespondingException();
			}
			throw new CcgwClientException();
		} finally {
			logs.put(CcgwSubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			CcgwSubscriptionLogger.write(logs);
		}
		TECHNICAL_LOGGER.debug("Received subscriptionId :" + subscriptionId);
		return subscriptionId;
	}

	/**
	 * 
	 * @param sumaUnsubscriptionRequest
	 * @return
	 * @throws CcgwClientException
	 * @throws CcgwNotRespondingException
	 */
	public boolean unsubscribe(SumaUnsubscriptionRequest sumaUnsubscriptionRequest)
			throws CcgwClientException, CcgwNotRespondingException {

		TECHNICAL_LOGGER.debug("Starting unsubscription method..");

		// Received request = null
		Objects.requireNonNull(sumaUnsubscriptionRequest);
		TECHNICAL_LOGGER.debug("Received sumaUnsubscriptionRequest with fields {}",
				sumaUnsubscriptionRequest.toString());

		Map<CcgwUnsubscriptionFields, String> logs = new HashMap<CcgwUnsubscriptionFields, String>();
		boolean success = false;
		String ccgwErrorCode = "";
		Integer httpResponseCode = 0;
		List<String> ccgwErrorParams = new ArrayList<String>();

		logs.put(CcgwUnsubscriptionFields.INTERNAL_ID, UUID.randomUUID().toString());
		logs.put(CcgwUnsubscriptionFields.REQUEST_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());

		UnubscribeRequestType unubscribeRequestType = subscriptionObjectFactory.createUnubscribeRequestType();
		unubscribeRequestType.setProviderId(sumaUnsubscriptionRequest.getProviderId());
		unubscribeRequestType.setProviderPass(providerPass);
		unubscribeRequestType
				.setSubscriber(PdkMsisdnUtils.getMsisdnWithoutPrefix(sumaUnsubscriptionRequest.getSubscriber()));
		unubscribeRequestType.setSubscriptionId(new BigInteger(sumaUnsubscriptionRequest.getSubscriptionId()));
		unubscribeRequestType.setTimestamp(System.currentTimeMillis() / 1000L);

		String stringToSign = buildUnsubscriptionRequestString(unubscribeRequestType);
		unubscribeRequestType.setVasSignature(calculateVasSignature(providerSecret, stringToSign));

		logs.put(CcgwUnsubscriptionFields.PROVIDER_ID, sumaUnsubscriptionRequest.getProviderId());
		logs.put(CcgwUnsubscriptionFields.PROVIDER_PASS, providerPass);
		logs.put(CcgwUnsubscriptionFields.TIMESTAMP, String.valueOf(unubscribeRequestType.getTimestamp()));
		logs.put(CcgwUnsubscriptionFields.SUBSCRIPTION_ID, String.valueOf(unubscribeRequestType.getSubscriptionId()));
		logs.put(CcgwUnsubscriptionFields.SUBSCRIBER, unubscribeRequestType.getSubscriber());
		logs.put(CcgwUnsubscriptionFields.VAS_SIGNATURE, String.valueOf(unubscribeRequestType.getVasSignature()));

		try {
			PostSubscribeResponseType unsubscribeResponse = ccgwSubscriptionPort.unsubscribe(unubscribeRequestType);
			logs.put(CcgwUnsubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());

			// Check if un-subscribe response in not null
			if (!Objects.isNull(unsubscribeResponse)) {
				// Retrieve Http response code
				httpResponseCode = HttpStatus.OK.value();
				// Check if status object is not NULL
				if (!Objects.isNull(unsubscribeResponse.getStatus())) {
					success = unsubscribeResponse.getStatus().isSuccess();
					logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS, String.valueOf(success));

					if (!success) {
						// CCGW response with failure throw CcgwClientException
						TECHNICAL_LOGGER.debug("Error occured in CcgwClient - UNSUBSCRIBE error response");

						if (!Objects.isNull(unsubscribeResponse.getStatus().getErrorParam())) {
							ccgwErrorParams = unsubscribeResponse.getStatus().getErrorParam();
						}
						if (!Objects.isNull(unsubscribeResponse.getStatus().getErrorCode())) {
							ccgwErrorCode = unsubscribeResponse.getStatus().getErrorCode().getValue();
						}

						logs.put(CcgwUnsubscriptionFields.CCGW_ERROR_CODE, ccgwErrorCode);
						logs.put(CcgwUnsubscriptionFields.CCGW_ERROR_PARAMS, String.join(",", ccgwErrorParams));

						throw new CcgwClientException(httpResponseCode, ccgwErrorCode, ccgwErrorParams);
					}
				} // unsub response null
				else {
					logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);
				}
			}
			logs.put(CcgwUnsubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));

		} catch (Fault fault) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient - UNSUBSCRIBE service fault");

			httpResponseCode = HttpStatus.OK.value();
			if (!Objects.isNull(fault.getFaultInfo().getStatus())) {
				logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS,
						String.valueOf(fault.getFaultInfo().getStatus().isSuccess()));
				if (!Objects.isNull(fault.getFaultInfo().getStatus().getErrorParam())) {
					ccgwErrorParams = fault.getFaultInfo().getStatus().getErrorParam();
				}
				if (!Objects.isNull(fault.getFaultInfo().getStatus().getErrorCode())) {
					ccgwErrorCode = fault.getFaultInfo().getStatus().getErrorCode().getValue();
				}
			}

			logs.put(CcgwUnsubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));
			logs.put(CcgwUnsubscriptionFields.CCGW_ERROR_CODE, ccgwErrorCode);
			logs.put(CcgwUnsubscriptionFields.CCGW_ERROR_PARAMS, String.join(",", ccgwErrorParams));

			throw new CcgwClientException(httpResponseCode, ccgwErrorCode, ccgwErrorParams);

		} catch (SOAPFaultException soapFaultException) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient unsubscribe - SOAPFaultException");

			httpResponseCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
			CcgwClientException ccgwClientException = new CcgwClientException(
					"Error occured in CcgwClient unsubscribe - SOAPFaultException");
			ccgwClientException.setSoapFaultHttpStatusCode(httpResponseCode);
			ccgwClientException.setSoapFaultCode(soapFaultException.getFault().getFaultCode());
			ccgwClientException.setSoapFaultMessage(soapFaultException.getFault().getFaultString());

			logs.put(CcgwUnsubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));
			logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);

			throw ccgwClientException;

		} catch (javax.xml.ws.WebServiceException webServiceException) {
			TECHNICAL_LOGGER
					.debug("Error occured in CcgwClient unsubscribe - WebServiceException" + webServiceException);
			CcgwClientException ccgwClientException = new CcgwClientException(
					"Error occured in CcgwClient unsubscribe - WebServiceException");

			if ((null != webServiceException.getCause()) && (webServiceException.getCause() instanceof HTTPException)) {
				TECHNICAL_LOGGER.debug("Error due to http conduit exception");
				HTTPException e = (HTTPException) webServiceException.getCause();
				ccgwClientException.setCcgwFaultHttpStatusCode(e.getResponseCode());
			}

			logs.put(CcgwUnsubscriptionFields.CCGW_HTTP_RESPONSE_CODE,
					String.valueOf(ccgwClientException.getCcgwFaultHttpStatusCode()));
			logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);

			throw ccgwClientException;
		} catch (CcgwClientException ccgwClientException) {
			throw ccgwClientException;
		} catch (Exception e) {
			TECHNICAL_LOGGER.debug("An unexpected exception has occred while requesting ccgw ");

			logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);

			if (e.getCause() instanceof SocketTimeoutException) {
				throw new CcgwNotRespondingException();
			}
			throw new CcgwClientException();
		} finally {
			logs.put(CcgwUnsubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			CcgwUnsubscriptionLogger.write(logs);
		}
		TECHNICAL_LOGGER.debug("Received response status success :" + success);

		return success;
	}

	/**
	 * 
	 * @param secret
	 * @param stringToSign
	 * @return
	 * @throws CcgwClientException
	 */
	private byte[] calculateVasSignature(String secret, String stringToSign) throws CcgwClientException {
		byte[] vasSignature = null;

		try {
			SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), SECRET_KEY_ALGORITHM);
			Mac mac = Mac.getInstance(SECRET_KEY_ALGORITHM);
			mac.init(secretKey);
			vasSignature = mac.doFinal(stringToSign.getBytes("UTF-8"));
		} catch (Exception e) {
			TECHNICAL_LOGGER.debug("CCGW client : building vas signature exception ", e);
			throw new CcgwClientException();
		}
		return vasSignature;
	}

	/**
	 * 
	 * @param unubscribeRequestType
	 * @return
	 */
	private String buildUnsubscriptionRequestString(UnubscribeRequestType unubscribeRequestType) {
		return String.format("timestamp=%s&provider-id=%s&provider-pass=%s&subscription-id=%s&subscriber=%s",
				unubscribeRequestType.getTimestamp(), unubscribeRequestType.getProviderId(),
				unubscribeRequestType.getProviderPass(), unubscribeRequestType.getSubscriptionId(),
				unubscribeRequestType.getSubscriber());
	}

	/**
	 * 
	 * @param subscribeRequestType
	 * @return
	 */
	private String buildSubscriptionRequestString(SubscribeRequestType subscribeRequestType) {
		StringBuilder sb = new StringBuilder();
		sb.append(KEY_TIMESTAMP).append(subscribeRequestType.getTimestamp()).append(VAS_SEPARATOR);
		sb.append(KEY_PROVIDER_ID).append(subscribeRequestType.getProviderId()).append(VAS_SEPARATOR);
		sb.append(KEY_PROVIDER_PASS).append(subscribeRequestType.getProviderPass()).append(VAS_SEPARATOR);
		sb.append(KEY_SALE_PROVIDER_ID).append(subscribeRequestType.getSaleProviderId()).append(VAS_SEPARATOR);
		sb.append(KEY_TRANSACTION_ID).append(subscribeRequestType.getTransactionId()).append(VAS_SEPARATOR);
		sb.append(KEY_SUBSCRIBER).append(subscribeRequestType.getSubscriber()).append(VAS_SEPARATOR);
		sb.append(KEY_AUTHENTICATION_METHOD).append(subscribeRequestType.getAuthenticationMethod().value())
				.append(VAS_SEPARATOR);
		sb.append(KEY_AUTHORIZATION_TYPE).append(subscribeRequestType.getAuthorizationType().value())
				.append(VAS_SEPARATOR);
		sb.append(KEY_CONTENT_NAME).append(subscribeRequestType.getContentInfo().getContentName())
				.append(VAS_SEPARATOR);
		sb.append(KEY_CONTENT_TYPE).append(subscribeRequestType.getContentInfo().getContentType())
				.append(VAS_SEPARATOR);
		sb.append(KEY_RATING_LEVEL).append(subscribeRequestType.getContentInfo().getRatingLevel())
				.append(VAS_SEPARATOR);
		sb.append(KEY_ADULT_FLAG).append(String.valueOf(subscribeRequestType.getContentInfo().isAdultFlag()))
				.append(VAS_SEPARATOR);
		sb.append(KEY_SUBSCRIPTION_MODEL).append(subscribeRequestType.getContentInfo().getSubscriptionModel().value())
				.append(VAS_SEPARATOR);
		sb.append(KEY_SALE_MODEL).append(subscribeRequestType.getContentInfo().getSaleModel().value())
				.append(VAS_SEPARATOR);
		sb.append(KEY_AMOUNT).append(subscribeRequestType.getContentInfo().getPrice().getAmount())
				.append(VAS_SEPARATOR);
		sb.append(KEY_TAXED_AMOUNT).append(subscribeRequestType.getContentInfo().getPrice().getTaxedAmount())
				.append(VAS_SEPARATOR);
		sb.append(KEY_CURRENCY).append(subscribeRequestType.getContentInfo().getPrice().getCurrency());
		TECHNICAL_LOGGER.debug("String to sign {}", sb.toString());

		return sb.toString();

	}

}