package com.orange.paddock.suma.consumer.ccgw.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.orange.paddock.suma.consumer.ccgw.model.CcgwSubscriptionPortType;
import com.orange.paddock.suma.consumer.ccgw.model.Fault;
import com.orange.paddock.suma.consumer.ccgw.model.SubscribeRequestType;
import com.orange.paddock.suma.consumer.ccgw.model.SumaSubscriptionRequest;

public class CcgwClient {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(CcgwClient.class);

	@Value("{ccgw.provider.pass}")
	private String providerPass;
	
	@Value("{ccgw.authentication.method}")
	private String authenticationMethod;
	
	@Value("{ccgw.authorization.type}")
	private String authorizationType;
	
	@Value("{ccgw.rating.level}")
	private String ratingLevel;
	
	@Value("{ccwg.subscription.model}")
	private String subscriptionModel;
	
	@Value("{ccgw.sale-model}")
	private String saleModel;
	
	@Autowired
	private CcgwSubscriptionPortType sumaClient;

	public String subscribe(SumaSubscriptionRequest sumaSubscriptionRequest) {

		TECHNICAL_LOGGER.debug("Starting subscription method..");
		SubscribeRequestType subscribeParameters = new SubscribeRequestType();
		
		try {
			sumaClient.subscribe(subscribeParameters);
		} catch (Fault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String subscriptionId = null;
		return subscriptionId;
	}

	private String calculateVasSignature() {
		String vasSignature = null;
		return vasSignature;
	}

}