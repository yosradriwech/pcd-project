package com.orange.paddock.suma.consumer.ccgw.test;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CcgwClientTestApplication.class)
@WebAppConfiguration
public abstract class AbstractCcgwClientSubscriptionTest {
	
	protected ClientAndServer mockServerClient;

	@Before
	public void setUp() {
		mockServerClient = startClientAndServer(1088);
	}

	@After
	public void stopProxy() {
		mockServerClient.stop();
	}

}
