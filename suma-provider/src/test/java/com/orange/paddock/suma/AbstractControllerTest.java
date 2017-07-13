package com.orange.paddock.suma;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import java.io.IOException;
import java.math.BigDecimal;

import javax.naming.NamingException;
import javax.xml.ws.Endpoint;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.orange.paddock.commons.log.PdkLogIdBean;
import com.orange.paddock.suma.business.manager.SubscriptionManager;
import com.orange.paddock.suma.dao.mongodb.repository.SubscriptionRepository;
import com.orange.paddock.suma.provider.rest.SubscriptionRestController;
import com.orange.paddock.suma.provider.soap.NotificationSoapController;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = { SumaApplicationTest.class })
public abstract class AbstractControllerTest {
	
	protected static String SUMA_ENDPOINT_SUBSCRIPTION = "/subscription/v1/subscriptions/";
	protected static String SUMA_ENDPOINT_UNSUBSCRIPTION = "/subscription/v1/subscriptions/";
	
	protected static String serviceId = "Fortumo";
	protected static String onBehalfOf = "Marc Dorcel";
	protected static String endUserid = "tel:+33123456789";
	protected static String description = "XXX Content";
	protected static String categoryCode = "XXX";
	protected static BigDecimal amount = new BigDecimal("100");
	protected static BigDecimal taxedAmount = new BigDecimal("120");
	protected static String currency = "PLN";
	protected static boolean isAdult = true;
	
	
	@Autowired
	protected WebApplicationContext ctx;

	@Autowired
	protected NotificationSoapController soapController;

	@Autowired
	protected SubscriptionRestController restController;
	
	@MockBean
	protected PdkLogIdBean loggerId;
	
	@MockBean
	protected SubscriptionRepository repository;
	
	@MockBean
	protected SubscriptionManager manager;

	protected MockMvc mockMvc;

	protected ClientAndServer mockServer;

	private static Endpoint endpoint;
	private static int port;

	@Before
	public void setUp() throws IllegalStateException, NamingException {
		mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();

		mockServer = startClientAndServer(1080);

		// Clean up database
	}

	@After
	public void stopProxy() {
		mockServer.stop();
	}

//	protected String readResourceFile(String fileName) throws IOException {
//		ClassLoader classLoader = getClass().getClassLoader();
//
//		URL url = classLoader.getResource(fileName);
//
//		return url.toString();
//	}
	
	protected String readResourceFile(String fileName) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		return IOUtils.toString(classLoader.getResource(fileName));
	}


}
