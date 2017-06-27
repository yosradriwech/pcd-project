package com.orange.paddock.suma.consumer.ccgw.client;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.cxf.transport.http.HTTPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.orange.paddock.commons.date.PdkDateUtils;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;
import com.orange.paddock.suma.consumer.ccgw.interceptors.CcgwSoapFaultInInterceptor;
import com.orange.paddock.suma.consumer.ccgw.interceptors.CcgwSoapInInterceptor;
import com.orange.paddock.suma.consumer.ccgw.log.CcgwSubscriptionLogger;
import com.orange.paddock.suma.consumer.ccgw.log.CcgwSubscriptionLogger.CcgwSubscriptionFields;
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

	@Autowired
	private ObjectFactory subscriptionObjectFactory;

	@Autowired
	private CcgwSoapInInterceptor ccgwSoapInInterceptor;

	@Autowired
	private CcgwSoapFaultInInterceptor ccgwSoapFaultInInterceptor;

	/**
	 * 
	 * @param sumaSubscriptionRequest
	 * @param internalId
	 * @return
	 * @throws CcgwClientException
	 */
	//TODO internalId
	public String subscribe(SumaSubscriptionRequest sumaSubscriptionRequest, String internalId) throws CcgwClientException {

		TECHNICAL_LOGGER.debug("Starting subscription method using sumaSubscriptionRequest with fields {}", sumaSubscriptionRequest.toString());
		String subscriptionId = null;

		Map<CcgwSubscriptionFields, String> logs = new HashMap<CcgwSubscriptionFields, String>();
		logs.put(CcgwSubscriptionFields.INTERNAL_ID, internalId);
		logs.put(CcgwSubscriptionFields.REQUEST_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());

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
		subscribeRequestType.setSubscriber(sumaSubscriptionRequest.getSubscriber());
		subscribeRequestType.setAuthenticationMethod(AuthenticationMethodType.valueOf(authenticationMethod));
		subscribeRequestType.setAuthorizationType(AuthorizationType.valueOf(authorizationType));
		subscribeRequestType.setContentInfo(contentInfo);
		subscribeRequestType.setTimestamp(System.currentTimeMillis());
		String stringToSign = buildSubscriptionRequestString(subscribeRequestType);
		subscribeRequestType.setVasSignature(calculateVasSignature(providerSecret, stringToSign));

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
		logs.put(CcgwSubscriptionFields.VAS_SIGNATURE, String.valueOf(subscribeRequestType.getVasSignature()));

		try {
			// Call CCGW remote service
			SubscribeResponseType subscribeResponseType = ccgwSubscriptionPort.subscribe(subscribeRequestType);

			// CCGW is responsive
			logs.put(CcgwSubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf(ccgwSoapInInterceptor.getHttpResponseCode()));
			logs.put(CcgwSubscriptionFields.SUBSCRIPTION_ID, String.valueOf(subscribeResponseType.getSubscriptionId()));

			if (subscribeResponseType.getStatus().isSuccess()) {
				// CCGW response is successful
				subscriptionId = String.valueOf(subscribeResponseType.getSubscriptionId());
			} else {
				// CCGW response with failure throw CcgwClientException
				TECHNICAL_LOGGER.debug("Error occured in CcgwClient - SUBSCRIBE error response - Code: {}", subscribeResponseType.getStatus()
						.getErrorCode().getValue());

				logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf(ccgwSoapInInterceptor.getHttpResponseCode()));
				logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, String.valueOf(subscribeResponseType.getStatus().isSuccess()));
				logs.put(CcgwSubscriptionFields.CCGW_ERROR_CODE, subscribeResponseType.getStatus().getErrorCode().getValue());

				if (null != subscribeResponseType.getStatus().getErrorParam()) {
					logs.put(CcgwSubscriptionFields.CCGW_ERROR_PARAMS, String.join(",", subscribeResponseType.getStatus().getErrorParam()));
				}

				CcgwSubscriptionLogger.write(logs);
				throw new CcgwClientException(ccgwSoapInInterceptor.getHttpResponseCode(), subscribeResponseType.getStatus().getErrorCode()
						.getValue(), subscribeResponseType.getStatus().getErrorParam());
			}

		} catch (Fault fault) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient - SUBSCRIBE soap fault: {}", fault.toString());
			logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf(ccgwSoapInInterceptor.getHttpResponseCode()));
			logs.put(CcgwSubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, String.valueOf(fault.getFaultInfo().getStatus().isSuccess()));
			logs.put(CcgwSubscriptionFields.CCGW_ERROR_CODE, fault.getFaultInfo().getStatus().getErrorCode().getValue());

			if (null != fault.getFaultInfo().getStatus().getErrorParam()) {
				logs.put(CcgwSubscriptionFields.CCGW_ERROR_PARAMS, String.join(",", fault.getFaultInfo().getStatus().getErrorParam()));
			}
			CcgwSubscriptionLogger.write(logs);
			throw new CcgwClientException(ccgwSoapInInterceptor.getHttpResponseCode(), fault.getFaultInfo().getStatus().getErrorCode().getValue(),
					fault.getFaultInfo().getStatus().getErrorParam());

		} catch (SOAPFaultException soapFaultException) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient - SOAPFaultException");

			logs.put(CcgwSubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf(ccgwSoapFaultInInterceptor.getHttpResponseCode()));

			CcgwClientException ccgwClientException = new CcgwClientException("Error occured in CcgwClient - SOAPFaultException");
			ccgwClientException.setSoapFaultHttpStatusCode(ccgwSoapFaultInInterceptor.getHttpResponseCode());
			ccgwClientException.setSoapFaultCode(soapFaultException.getFault().getFaultCode());
			ccgwClientException.setSoapFaultMessage(soapFaultException.getFault().getFaultString());

			CcgwSubscriptionLogger.write(logs);
			throw ccgwClientException;
		} catch (javax.xml.ws.WebServiceException webServiceException) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient - WebServiceException" + webServiceException);
			CcgwClientException ccgwClientException = new CcgwClientException("Error occured in CcgwClient - WebServiceException");

			if ((null != webServiceException.getCause()) && (webServiceException.getCause() instanceof HTTPException)) {
				TECHNICAL_LOGGER.debug("Error due to http conduit exception");
				HTTPException e = (HTTPException) webServiceException.getCause();
				ccgwClientException.setCcgwFaultHttpStatusCode(e.getResponseCode());
			}

			logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf(ccgwClientException.getCcgwFaultHttpStatusCode()));
			logs.put(CcgwSubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());

			CcgwSubscriptionLogger.write(logs);
			throw ccgwClientException;
		} catch (CcgwClientException ccgwClientException) {
			throw ccgwClientException;
		} catch (Exception e) {
			TECHNICAL_LOGGER.debug("An unexpected exception has occred while requesting ccgw ");
			CcgwSubscriptionLogger.write(logs);
			throw new CcgwClientException();
		}

		CcgwSubscriptionLogger.write(logs);
		TECHNICAL_LOGGER.debug("Received subscriptionId :" + subscriptionId);
		return subscriptionId;
	}

	/**
	 * 
	 * @param sumaUnsubscriptionRequest
	 * @param internalId
	 * @return
	 * @throws CcgwClientException
	 */
	public boolean unsubscribe(SumaUnsubscriptionRequest sumaUnsubscriptionRequest, String internalId) throws CcgwClientException {
		boolean success = false;
		TECHNICAL_LOGGER.debug("Starting unsubscription method using sumaUnsubscriptionRequest with fields {}", sumaUnsubscriptionRequest.toString());

		// TODO applicative logs
		UnubscribeRequestType unubscribeRequestType = subscriptionObjectFactory.createUnubscribeRequestType();
		unubscribeRequestType.setProviderId(sumaUnsubscriptionRequest.getProviderId());
		unubscribeRequestType.setProviderPass(providerPass);
		unubscribeRequestType.setSubscriber(sumaUnsubscriptionRequest.getSubscriber());
		unubscribeRequestType.setSubscriptionId(new BigInteger(sumaUnsubscriptionRequest.getSubscriptionId()));
		unubscribeRequestType.setTimestamp(System.currentTimeMillis());
		String stringToSign = buildUnsubscriptionRequestString(unubscribeRequestType);
		unubscribeRequestType.setVasSignature(calculateVasSignature(providerSecret, stringToSign));

		try {
			PostSubscribeResponseType unsubscribeResponse = ccgwSubscriptionPort.unsubscribe(unubscribeRequestType);
			success = unsubscribeResponse.getStatus().isSuccess();

			if (!success) {
				TECHNICAL_LOGGER.debug("Error occured in CcgwClient - UNSUBSCRIBE error response - Code: {} errorParams: {}", unsubscribeResponse
						.getStatus().getErrorCode().getValue(), String.join(", ", unsubscribeResponse.getStatus().getErrorParam()));
				// throw new
				// CcgwClientException(unsubscribeResponse.getStatus().getErrorCode().getValue(),
				// unsubscribeResponse.getStatus()
				// .getErrorParam());
			}

		} catch (Fault fault) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient - UNSUBSCRIBE soap fault: {}", fault.toString());
			// throw new
			// CcgwClientException(fault.getFaultInfo().getStatus().getErrorCode().getValue(),
			// fault.getFaultInfo().getStatus()
			// .getErrorParam());
		}

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
			mac.doFinal(stringToSign.getBytes("UTF-8"));
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
		return String.format("timestamp=%s&provider-id=%s&provider-pass=%s&subscription-id=%s&subscriber=%s", unubscribeRequestType.getTimestamp(),
				unubscribeRequestType.getProviderId(), unubscribeRequestType.getProviderPass(), unubscribeRequestType.getSubscriptionId(),
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
		sb.append(KEY_PROVIDER_PASS).append(providerPass).append(VAS_SEPARATOR);
		sb.append(KEY_SALE_PROVIDER_ID).append(subscribeRequestType.getSaleProviderId()).append(VAS_SEPARATOR);
		sb.append(KEY_TRANSACTION_ID).append(subscribeRequestType.getTransactionId()).append(VAS_SEPARATOR);
		sb.append(KEY_SUBSCRIBER).append(subscribeRequestType.getSubscriber()).append(VAS_SEPARATOR);
		sb.append(KEY_AUTHENTICATION_METHOD).append(authenticationMethod).append(VAS_SEPARATOR);
		sb.append(KEY_AUTHORIZATION_TYPE).append(authorizationType).append(VAS_SEPARATOR);
		sb.append(KEY_CONTENT_NAME).append(subscribeRequestType.getContentInfo().getContentName()).append(VAS_SEPARATOR);
		sb.append(KEY_CONTENT_TYPE).append(subscribeRequestType.getContentInfo().getContentType()).append(VAS_SEPARATOR);
		sb.append(KEY_RATING_LEVEL).append(ratingLevel).append(VAS_SEPARATOR);
		sb.append(KEY_ADULT_FLAG).append(String.valueOf(subscribeRequestType.getContentInfo().isAdultFlag())).append(VAS_SEPARATOR);
		sb.append(KEY_SUBSCRIPTION_MODEL).append(subscriptionModel).append(VAS_SEPARATOR);
		sb.append(KEY_SALE_MODEL).append(saleModel).append(VAS_SEPARATOR);
		sb.append(KEY_AMOUNT).append(String.valueOf(subscribeRequestType.getContentInfo().getPrice().getAmount())).append(VAS_SEPARATOR);
		sb.append(KEY_TAXED_AMOUNT).append(String.valueOf(subscribeRequestType.getContentInfo().getPrice().getTaxedAmount())).append(VAS_SEPARATOR);
		sb.append(KEY_CURRENCY).append(subscribeRequestType.getContentInfo().getPrice().getCurrency());
		TECHNICAL_LOGGER.debug("String to sign {}", sb.toString());

		return sb.toString();

	}

}