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
	
//	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema notificationSchema){
//		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
//		wsdl11Definition.setPortTypeName("NotificationPortType");
//		wsdl11Definition.setLocationUri("/notification");
//		wsdl11Definition.setTargetNamespace("https://ccgw.orange.pl/api/2.1");
//		wsdl11Definition.setSchema(notificationSchema);
//		return wsdl11Definition;
//		
//	}
	
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

		//Map<String, Object> properties = new HashMap<String, Object>();
		// properties.put(SCHEMA_VALIDATION_ENABLED, new Boolean(schemaValidation));
		// endpoint.setProperties(properties);
		endpoint.publish("/");

		return endpoint;
	}

}
