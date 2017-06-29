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

import com.orange.paddock.commons.date.PdkDateUtils;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwNotRespondingException;
import com.orange.paddock.suma.consumer.ccgw.interceptors.CcgwSoapFaultInInterceptor;
import com.orange.paddock.suma.consumer.ccgw.interceptors.CcgwSoapInInterceptor;
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
import com.orange.paddock.suma.consumer.ccgw.utils.SoapWebServiceUtils;

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

	@Autowired
	private ObjectFactory subscriptionObjectFactory;

	@Autowired
	private CcgwSoapInInterceptor ccgwSoapInInterceptor;

	@Autowired
	private CcgwSoapFaultInInterceptor ccgwSoapFaultInInterceptor;

	/**
	 * 
	 * @param sumaSubscriptionRequest
	 * @return
	 * @throws CcgwClientException
	 * @throws CcgwNotRespondingException
	 */
	public String subscribe(SumaSubscriptionRequest sumaSubscriptionRequest) throws CcgwClientException, CcgwNotRespondingException {

		TECHNICAL_LOGGER.debug("Starting subscription method from CCGW client...");
		// Logs
		Map<CcgwSubscriptionFields, String> logs = new HashMap<CcgwSubscriptionFields, String>();
		String ccgwErrorCode = "", subscriptionId = "";
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
			logs.put(CcgwSubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());

			// Check if subscribe response in not null
			if (!SoapWebServiceUtils.objectIsNull(subscribeResponseType)) {
				// Retrieve subscription Id
				if (!SoapWebServiceUtils.objectIsNull(subscribeResponseType.getSubscriptionId())) {
					subscriptionId = String.valueOf(subscribeResponseType.getSubscriptionId());
				}
				// Retrieve Http response code
				if (!SoapWebServiceUtils.objectIsNull(ccgwSoapInInterceptor)) {
					httpResponseCode = ccgwSoapInInterceptor.getHttpResponseCode();
				}
				// Check if status object is not NULL
				if (!SoapWebServiceUtils.objectIsNull(subscribeResponseType.getStatus())) {
					logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, String.valueOf(subscribeResponseType.getStatus().isSuccess()));
					if (!subscribeResponseType.getStatus().isSuccess()) {
						// CCGW response with failure throw CcgwClientException
						TECHNICAL_LOGGER.debug("Error occured in CcgwClient - SUBSCRIBE error response");

						if (!SoapWebServiceUtils.objectIsNull(subscribeResponseType.getStatus().getErrorParam())) {
							ccgwErrorParams = subscribeResponseType.getStatus().getErrorParam();
						}
						if (!SoapWebServiceUtils.objectIsNull(subscribeResponseType.getStatus().getErrorCode())) {
							ccgwErrorCode = subscribeResponseType.getStatus().getErrorCode().getValue();
						}

						logs.put(CcgwSubscriptionFields.CCGW_ERROR_CODE, ccgwErrorCode);
						logs.put(CcgwSubscriptionFields.CCGW_ERROR_PARAMS, String.join(",", ccgwErrorParams));
						CcgwSubscriptionLogger.write(logs);
						throw new CcgwClientException(httpResponseCode, ccgwErrorCode, ccgwErrorParams);
					}
				}
			}// response null
			else {
				logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);
			}

			logs.put(CcgwSubscriptionFields.SUBSCRIPTION_ID, subscriptionId);
			logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));

		} catch (Fault fault) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient - SUBSCRIBE service fault");

			logs.put(CcgwSubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			if (!SoapWebServiceUtils.objectIsNull(ccgwSoapInInterceptor)) {
				httpResponseCode = ccgwSoapInInterceptor.getHttpResponseCode();
			}
			if (!SoapWebServiceUtils.objectIsNull(fault.getFaultInfo().getStatus())) {
				logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, String.valueOf(fault.getFaultInfo().getStatus().isSuccess()));
				if (!SoapWebServiceUtils.objectIsNull(fault.getFaultInfo().getStatus().getErrorParam())) {
					ccgwErrorParams = fault.getFaultInfo().getStatus().getErrorParam();
				}
				if (!SoapWebServiceUtils.objectIsNull(fault.getFaultInfo().getStatus().getErrorCode())) {
					ccgwErrorCode = fault.getFaultInfo().getStatus().getErrorCode().getValue();
				}
			}

			logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));
			logs.put(CcgwSubscriptionFields.CCGW_ERROR_CODE, ccgwErrorCode);
			logs.put(CcgwSubscriptionFields.CCGW_ERROR_PARAMS, String.join(",", ccgwErrorParams));
			CcgwSubscriptionLogger.write(logs);
			throw new CcgwClientException(httpResponseCode, ccgwErrorCode, ccgwErrorParams);

		} catch (SOAPFaultException soapFaultException) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient subscribe - SOAPFaultException");

			logs.put(CcgwSubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			if (!SoapWebServiceUtils.objectIsNull(ccgwSoapFaultInInterceptor)) {
				httpResponseCode = ccgwSoapFaultInInterceptor.getHttpResponseCode();
			}
			logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));
			logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);

			CcgwClientException ccgwClientException = new CcgwClientException("Error occured in CcgwClient subscribe - SOAPFaultException");
			ccgwClientException.setSoapFaultHttpStatusCode(httpResponseCode);
			ccgwClientException.setSoapFaultCode(soapFaultException.getFault().getFaultCode());
			ccgwClientException.setSoapFaultMessage(soapFaultException.getFault().getFaultString());

			CcgwSubscriptionLogger.write(logs);
			throw ccgwClientException;

		} catch (javax.xml.ws.WebServiceException webServiceException) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient subscribe - WebServiceException" + webServiceException);
			CcgwClientException ccgwClientException = new CcgwClientException("Error occured in CcgwClient subscribe - WebServiceException");

			if ((null != webServiceException.getCause()) && (webServiceException.getCause() instanceof HTTPException)) {
				TECHNICAL_LOGGER.debug("Error due to http conduit exception");
				HTTPException e = (HTTPException) webServiceException.getCause();
				ccgwClientException.setCcgwFaultHttpStatusCode(e.getResponseCode());
				logs.put(CcgwSubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf(ccgwClientException.getCcgwFaultHttpStatusCode()));
			}

			logs.put(CcgwSubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);

			CcgwSubscriptionLogger.write(logs);
			throw ccgwClientException;

		} catch (CcgwClientException ccgwClientException) {
			throw ccgwClientException;
		} catch (Exception e) {
			TECHNICAL_LOGGER.debug("An unexpected exception has occred while requesting ccgw");
			logs.put(CcgwSubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			logs.put(CcgwSubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);
			CcgwSubscriptionLogger.write(logs);

			if (e.getCause() instanceof SocketTimeoutException) {
				throw new CcgwNotRespondingException();
			}
			throw new CcgwClientException();
		}
		// successful
		CcgwSubscriptionLogger.write(logs);
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
	public boolean unsubscribe(SumaUnsubscriptionRequest sumaUnsubscriptionRequest) throws CcgwClientException, CcgwNotRespondingException {

		TECHNICAL_LOGGER.debug("Starting unsubscription method..");
		// Received request = null
		Objects.requireNonNull(sumaUnsubscriptionRequest);
		TECHNICAL_LOGGER.debug("Received sumaUnsubscriptionRequest with fields {}", sumaUnsubscriptionRequest.toString());

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
		unubscribeRequestType.setSubscriber(sumaUnsubscriptionRequest.getSubscriber());
		unubscribeRequestType.setSubscriptionId(new BigInteger(sumaUnsubscriptionRequest.getSubscriptionId()));
		unubscribeRequestType.setTimestamp(System.currentTimeMillis());
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
			if (!SoapWebServiceUtils.objectIsNull(unsubscribeResponse)) {
				// Retrieve Http response code
				if (!SoapWebServiceUtils.objectIsNull(ccgwSoapInInterceptor)) {
					httpResponseCode = ccgwSoapInInterceptor.getHttpResponseCode();
				}
				// Check if status object is not NULL
				if (!SoapWebServiceUtils.objectIsNull(unsubscribeResponse.getStatus())) {
					success = unsubscribeResponse.getStatus().isSuccess();
					logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS, String.valueOf(success));

					if (!success) {
						// CCGW response with failure throw CcgwClientException
						TECHNICAL_LOGGER.debug("Error occured in CcgwClient - UNSUBSCRIBE error response");

						if (!SoapWebServiceUtils.objectIsNull(unsubscribeResponse.getStatus().getErrorParam())) {
							ccgwErrorParams = unsubscribeResponse.getStatus().getErrorParam();
						}
						if (!SoapWebServiceUtils.objectIsNull(unsubscribeResponse.getStatus().getErrorCode())) {
							ccgwErrorCode = unsubscribeResponse.getStatus().getErrorCode().getValue();
						}

						logs.put(CcgwUnsubscriptionFields.CCGW_ERROR_CODE, ccgwErrorCode);
						logs.put(CcgwUnsubscriptionFields.CCGW_ERROR_PARAMS, String.join(",", ccgwErrorParams));

						CcgwUnsubscriptionLogger.write(logs);
						throw new CcgwClientException(httpResponseCode, ccgwErrorCode, ccgwErrorParams);
					}
				}// unsub response null
				else {
					logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);
				}
			}
			logs.put(CcgwUnsubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));

		} catch (Fault fault) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient - UNSUBSCRIBE service fault");

			logs.put(CcgwUnsubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			if (!SoapWebServiceUtils.objectIsNull(ccgwSoapInInterceptor)) {
				httpResponseCode = ccgwSoapInInterceptor.getHttpResponseCode();
			}
			if (!SoapWebServiceUtils.objectIsNull(fault.getFaultInfo().getStatus())) {
				logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS, String.valueOf(fault.getFaultInfo().getStatus().isSuccess()));
				if (!SoapWebServiceUtils.objectIsNull(fault.getFaultInfo().getStatus().getErrorParam())) {
					ccgwErrorParams = fault.getFaultInfo().getStatus().getErrorParam();
				}
				if (!SoapWebServiceUtils.objectIsNull(fault.getFaultInfo().getStatus().getErrorCode())) {
					ccgwErrorCode = fault.getFaultInfo().getStatus().getErrorCode().getValue();
				}
			}

			logs.put(CcgwUnsubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));
			logs.put(CcgwUnsubscriptionFields.CCGW_ERROR_CODE, ccgwErrorCode);
			logs.put(CcgwUnsubscriptionFields.CCGW_ERROR_PARAMS, String.join(",", ccgwErrorParams));
			CcgwUnsubscriptionLogger.write(logs);

			throw new CcgwClientException(httpResponseCode, ccgwErrorCode, ccgwErrorParams);

		} catch (SOAPFaultException soapFaultException) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient unsubscribe - SOAPFaultException");

			logs.put(CcgwUnsubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			if (!SoapWebServiceUtils.objectIsNull(ccgwSoapFaultInInterceptor)) {
				httpResponseCode = ccgwSoapFaultInInterceptor.getHttpResponseCode();
			}

			CcgwClientException ccgwClientException = new CcgwClientException("Error occured in CcgwClient unsubscribe - SOAPFaultException");
			ccgwClientException.setSoapFaultHttpStatusCode(httpResponseCode);
			ccgwClientException.setSoapFaultCode(soapFaultException.getFault().getFaultCode());
			ccgwClientException.setSoapFaultMessage(soapFaultException.getFault().getFaultString());

			logs.put(CcgwUnsubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf((Object) httpResponseCode));
			logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);
			CcgwUnsubscriptionLogger.write(logs);

			throw ccgwClientException;

		} catch (javax.xml.ws.WebServiceException webServiceException) {
			TECHNICAL_LOGGER.debug("Error occured in CcgwClient unsubscribe - WebServiceException" + webServiceException);
			CcgwClientException ccgwClientException = new CcgwClientException("Error occured in CcgwClient unsubscribe - WebServiceException");

			if ((null != webServiceException.getCause()) && (webServiceException.getCause() instanceof HTTPException)) {
				TECHNICAL_LOGGER.debug("Error due to http conduit exception");
				HTTPException e = (HTTPException) webServiceException.getCause();
				ccgwClientException.setCcgwFaultHttpStatusCode(e.getResponseCode());
			}

			logs.put(CcgwUnsubscriptionFields.CCGW_HTTP_RESPONSE_CODE, String.valueOf(ccgwClientException.getCcgwFaultHttpStatusCode()));
			logs.put(CcgwUnsubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);
			CcgwUnsubscriptionLogger.write(logs);

			throw ccgwClientException;
		} catch (CcgwClientException ccgwClientException) {
			throw ccgwClientException;
		} catch (Exception e) {
			TECHNICAL_LOGGER.debug("An unexpected exception has occred while requesting ccgw ");

			logs.put(CcgwUnsubscriptionFields.RESPONSE_TIMESTAMP, PdkDateUtils.getCurrentDateTimestamp());
			logs.put(CcgwUnsubscriptionFields.CCGW_RESPONSE_STATUS, CCGW_RESPONSE_STATUS_ERROR);
			CcgwUnsubscriptionLogger.write(logs);

			if (e.getCause() instanceof SocketTimeoutException) {
				throw new CcgwNotRespondingException();
			}
			throw new CcgwClientException();
		}
		// successful workflow
		CcgwUnsubscriptionLogger.write(logs);
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