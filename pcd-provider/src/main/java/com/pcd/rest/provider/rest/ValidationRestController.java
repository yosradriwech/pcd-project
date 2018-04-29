package com.pcd.rest.provider.rest;

import com.pcd.rest.dao.mongodb.document.Incident;
import com.pcd.rest.dao.mongodb.repository.IncidentRepository;
import com.pcd.rest.manager.SubscriptionManager;
import com.pcd.rest.manager.ValidationManager;
import com.pcd.rest.manager.exception.AbstractPcdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

public class ValidationRestController {


    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRestController.class);

    @Autowired
    private ValidationManager manager;
    @Autowired
    private IncidentRepository IncidentRepository;


    @DeleteMapping("finished/incidents")
    public ResponseEntity<Incident> delete(HttpServletRequest request, @RequestBody(required = true) Incident body) throws AbstractPcdException {

        LOGGER.debug("End validation request received with type{} and date{} and latitude{} and longitude{} and confirmation{}", body.getType(), body.getDateI(), body.getLatitude(), body.getLongitude(), body.getConfirmation());

        Incident incidentResponse = IncidentRepository.findOneByTypeAndLatitudeAndLongitude(body.getType(), body.getLatitude(), body.getLongitude());
        try {

            if ((incidentResponse.getEtat()=="finished")&&(manager.valider(body.getType(), body.getLatitude(), body.getLongitude())=="validation")){incidentResponse = null;}
        } catch (AbstractPcdException e) {
            LOGGER.error("An error occured {}", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("An error occured {}", e);
        }
        return new ResponseEntity<>(incidentResponse, HttpStatus.ACCEPTED);
    }
    @GetMapping("finished/incidents")
    public ResponseEntity<Incident> Start(HttpServletRequest request, @RequestBody(required = true) Incident body) throws AbstractPcdException {

    LOGGER.debug("start Validation request received with type{} and date{} and latitude{} and longitude{} and confirmation{}", body.getType(), body.getDateI(), body.getLatitude(), body.getLongitude(), body.getConfirmation());

    Incident incidentResponse = IncidentRepository.findOneByTypeAndLatitudeAndLongitude(body.getType(), body.getLatitude(), body.getLongitude());
    try {
        if ((incidentResponse.getEtat()=="started")&&(manager.valider(body.getType(), body.getLatitude(), body.getLongitude())!="validation")){incidentResponse=null;}
    } catch (AbstractPcdException e) {
        LOGGER.error("An error occured {}", e);
        throw e;
    } catch (Exception e) {
        LOGGER.error("An error occured {}", e);
    }
    return new ResponseEntity<>(incidentResponse, HttpStatus.ACCEPTED);
}
}
