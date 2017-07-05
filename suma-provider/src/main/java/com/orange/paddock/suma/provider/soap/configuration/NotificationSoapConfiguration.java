package com.orange.paddock.suma.provider.soap.configuration;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;

import com.orange.paddock.suma.provider.soap.NotificationSoapController;
import com.orange.paddock.suma.provider.soap.model.NotificationPortType;

@Configuration
@EnableWs
public class NotificationSoapConfiguration {
	
	@Bean
	public ServletRegistrationBean legacyServletRegistrationBean() {
		return new ServletRegistrationBean(new CXFServlet(), "/notification/*");
	}

	@Bean(name = Bus.DEFAULT_BUS_ID)
	public SpringBus springBus() {
		return new SpringBus();
	}
	
	@Bean
	public NotificationPortType legacyService() {
		return new NotificationSoapController();
	}
	
	@Bean
	public Endpoint endpointLegacy() {

		EndpointImpl endpoint = new EndpointImpl(springBus(), legacyService());

		endpoint.publish("/");

		return endpoint;
	}

}
