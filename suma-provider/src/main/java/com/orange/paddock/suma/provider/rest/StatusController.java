package com.orange.paddock.suma.provider.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orange.paddock.commons.model.status.Component;
import com.orange.paddock.commons.model.status.Component.Status;
import com.orange.paddock.suma.business.manager.StatusManager;

@RestController
@RequestMapping("/status")
public class StatusController {
	
	private static final String mongoDbComponentName = "MongoDB";
	
	@Value("${application.version}")
	private String appVersion;
	
	@Value("${application.title}")
	private String appTitle;
	
	@Autowired
	private StatusManager manager;
	
	@GetMapping
	public ResponseEntity<Component> getStatus(){
		
		HttpStatus httpStatus = HttpStatus.OK;
		
		Component response = new Component(appTitle, appVersion);
		response.setStatus(Status.ok);
		
		Component mongoDbComponent = new Component(mongoDbComponentName);
		mongoDbComponent.setStatus(manager.checkMongoDb() ? Status.ok : Status.ko);
		response.addComponent(mongoDbComponent);
		
		for (Component component : response.getComponents()) {
			if(component.getStatus() != Status.ok){
				response.setStatus(Status.ko);
				httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
				break;
			}
		}
		
		return new ResponseEntity<Component>(response, httpStatus);
		
	}
	
}
