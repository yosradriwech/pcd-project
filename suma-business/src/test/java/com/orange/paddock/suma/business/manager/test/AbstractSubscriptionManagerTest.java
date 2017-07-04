package com.orange.paddock.suma.business.manager.test;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.orange.paddock.suma.business.model.SubscriptionDto;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SubscriptionManagerTestApplication.class)
@WebAppConfiguration
public abstract class AbstractSubscriptionManagerTest {

	protected ClientAndServer mockServerClient;
	
	protected static final String endUserIdValue = "MY_END_USER_ID";
	protected static final String mco = "OFR";

	protected static final String SERVICE_ID = "SERVICE_ID";
	protected static final String ON_BEHALF_OF = "ON_BEHALF_OF";
	protected static final String END_USER_ID = "tel:+33979795544";
	protected static final String DESCRIPTION = "DESCRIPTION";
	protected static final String CATEGORY_CODE = "CATEGORY_CODE";
	protected static final BigDecimal AMOUNT = new BigDecimal(1.0);
	protected static final BigDecimal TAXED_AMOUNT = new BigDecimal(0.5);
	protected static final String CURRENCY = "CURRENCY";
	protected static final boolean IS_ADULT = false;

	protected static final String END_USER_ID_INVALID = "END_USER_ID_INVALID";
	protected static final String END_USER_ID_OAT = "acr:OrangeAPIToken";
	protected static final String END_USER_ID_ISE2 = "acr:X-Orange-ISE2";
	
	
	protected static final String SUBSCRIPTION_ID = "4844";
	

	@Before
	public void setUp() {
		mockServerClient = startClientAndServer(1088);
	}

	@After
	public void stopProxy() {
		mockServerClient.stop();
	}

	protected SubscriptionDto initializeValidSubscriptionDto() {
		SubscriptionDto subscriptionDtoInitialized = new SubscriptionDto();
		
		subscriptionDtoInitialized.setServiceId(SERVICE_ID);
		subscriptionDtoInitialized.setOnBehalfOf(ON_BEHALF_OF);
		subscriptionDtoInitialized.setEndUserId(END_USER_ID);
		subscriptionDtoInitialized.setDescription(DESCRIPTION);
		subscriptionDtoInitialized.setCategoryCode(CATEGORY_CODE);
		subscriptionDtoInitialized.setAmount(AMOUNT);
		subscriptionDtoInitialized.setTaxedAmount(TAXED_AMOUNT);
		subscriptionDtoInitialized.setCurrency(CURRENCY);
		subscriptionDtoInitialized.setAdult(IS_ADULT);
		
		return subscriptionDtoInitialized;
	}

}
