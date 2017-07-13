package com.orange.paddock.suma.business.manager.test;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.orange.paddock.commons.log.PdkLogIdBean;
import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;

/**
 * 
 * Abstract class for BOTH UNIT & INTEGRATION tests : 
 * Configuration class must not be specified here since there are different configuration for tests
 *
 */
@RunWith(SpringRunner.class)
public abstract class AbstractSubscriptionManagerTest {

	protected ClientAndServer mockServerClient;

	protected static final String endUserIdValue = "MY_END_USER_ID";
	protected static final String mco = "OFR";

	protected static final String SERVICE_ID = "SERVICE_ID";
	protected static final String ON_BEHALF_OF = "ON_BEHALF_OF";
	protected static final String END_USER_ID = "33979795544";
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
	
	protected static final String WT_MSISDN = "33675952191";
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	
//	@MockBean
//	protected PdkLogIdBean loggerId;
	
	
	@Before
	public void setUp() {
		mockServerClient = startClientAndServer(1088);
		subscriptionRepository.deleteAll();
	}

	@After
	public void stopProxy() {
		mockServerClient.stop();
	}

	protected String getErrorResponseCCGW(int errorCode) {
		return "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ns='https://ccgw.orange.pl/api/2.1'>"
				+ "<soapenv:Header/>" + "<soapenv:Body><ns:subscription-subscribe-response>" 
				+ "<ns:status> <ns:success>false</ns:success> "
				+ "<ns:error-code>"+ errorCode +"</ns:error-code>"
				+ "<ns:error-param>${errorParam}</ns:error-param> </ns:status>"
				+ "<ns:vas-signature></ns:vas-signature></ns:subscription-subscribe-response></soapenv:Body></soapenv:Envelope>";
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

	protected SubscriptionDto initializeValidSubscriptionDtoOAT() {
		SubscriptionDto subscriptionDtoInitialized = new SubscriptionDto();

		subscriptionDtoInitialized.setServiceId(SERVICE_ID);
		subscriptionDtoInitialized.setOnBehalfOf(ON_BEHALF_OF);
		subscriptionDtoInitialized.setEndUserId(END_USER_ID_OAT);
		subscriptionDtoInitialized.setDescription(DESCRIPTION);
		subscriptionDtoInitialized.setCategoryCode(CATEGORY_CODE);
		subscriptionDtoInitialized.setAmount(AMOUNT);
		subscriptionDtoInitialized.setTaxedAmount(TAXED_AMOUNT);
		subscriptionDtoInitialized.setCurrency(CURRENCY);
		subscriptionDtoInitialized.setAdult(IS_ADULT);

		return subscriptionDtoInitialized;
	}

	protected SubscriptionDto initializeValidSubscriptionDtoISE2() {
		SubscriptionDto subscriptionDtoInitialized = new SubscriptionDto();

		subscriptionDtoInitialized.setServiceId(SERVICE_ID);
		subscriptionDtoInitialized.setOnBehalfOf(ON_BEHALF_OF);
		subscriptionDtoInitialized.setEndUserId(END_USER_ID_ISE2);
		subscriptionDtoInitialized.setDescription(DESCRIPTION);
		subscriptionDtoInitialized.setCategoryCode(CATEGORY_CODE);
		subscriptionDtoInitialized.setAmount(AMOUNT);
		subscriptionDtoInitialized.setTaxedAmount(TAXED_AMOUNT);
		subscriptionDtoInitialized.setCurrency(CURRENCY);
		subscriptionDtoInitialized.setAdult(IS_ADULT);

		return subscriptionDtoInitialized;
	}

	protected SubscriptionDto initializeInvalidSubscriptionDto() {
		SubscriptionDto subscriptionDtoInitialized = new SubscriptionDto();

		subscriptionDtoInitialized.setServiceId(SERVICE_ID);
		subscriptionDtoInitialized.setOnBehalfOf(ON_BEHALF_OF);
		subscriptionDtoInitialized.setEndUserId(END_USER_ID_INVALID);
		subscriptionDtoInitialized.setDescription(DESCRIPTION);
		subscriptionDtoInitialized.setCategoryCode(CATEGORY_CODE);
		subscriptionDtoInitialized.setAmount(AMOUNT);
		subscriptionDtoInitialized.setTaxedAmount(TAXED_AMOUNT);
		subscriptionDtoInitialized.setCurrency(CURRENCY);
		subscriptionDtoInitialized.setAdult(IS_ADULT);		

		return subscriptionDtoInitialized;
	}

}
