package com.orange.paddock.suma.consumer.ccgw.test;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.orange.paddock.suma.consumer.ccgw.client.CcgwClient;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;
import com.orange.paddock.suma.consumer.ccgw.model.SumaSubscriptionRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CcgwClientTestApplication.class)
public class CcgwClientTest {

	private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(CcgwClientTest.class);

	private static final String SALE_PROVIDER_ID = "Paddock";
	private static final String CONTENT_TYPE = "Testowe";
	private static final String CONTENT_NAME = "test_subskrypcja ";
	private static final BigDecimal PRICE = new BigDecimal(1.5);
	private static final boolean ADULT_FLAG = false;
	private static final String SUBSCRIPTION_ID = "4844";
	
	@Autowired
	private CcgwClient ccgwClient;
	
	@Test
	public void SubscriptionTest() throws CcgwClientException {
		SumaSubscriptionRequest subReq = new SumaSubscriptionRequest();
		subReq.setAdultFlag(ADULT_FLAG);
		subReq.setAmount(PRICE);
		subReq.setContentName(CONTENT_NAME);
		subReq.setContentType(CONTENT_TYPE);
		subReq.setCurrency("EUR");
		subReq.setSaleProviderId(SALE_PROVIDER_ID);
		subReq.setProviderId(SALE_PROVIDER_ID);
		
		TECHNICAL_LOGGER.debug("Sending subscription request...");
		String subId = ccgwClient.subscribe(subReq, "TransactionId");
		
		TECHNICAL_LOGGER.debug("Result in : {}",subId);
	}
}
