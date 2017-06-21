package com.orange.paddock.suma.integration;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import java.io.IOException;
import java.net.URL;

import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.orange.paddock.suma.SumaApplicationTest;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = {SumaApplicationTest.class})
public abstract class AbstractControllerTest {
	
	@Autowired
	protected WebApplicationContext ctx;
	
	protected MockMvc mockMvc;

	protected ClientAndServer mockServer;
	
	@Before
	public void setUp() throws IllegalStateException, NamingException {
		mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();

		mockServer = startClientAndServer(1080);
	}

	@After
	public void stopProxy() {
		mockServer.stop();
	}

	protected String readResourceFile(String fileName) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		
		URL url = classLoader.getResource(fileName);
		
		return url.toString();
//		return IOUtils.toString(classLoader.getResource(fileName));
	}
	

}
