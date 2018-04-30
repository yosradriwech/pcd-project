package com.pcd.rest.provider.rest;

import com.pcd.rest.dao.mongodb.document.Incident;
import com.pcd.rest.dao.mongodb.repository.IncidentRepository;
import com.pcd.rest.manager.ValidationManager;
import com.pcd.rest.manager.exception.AbstractPcdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("validations")
public class ValidationRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationRestController.class);

    @Autowired
    private ValidationManager manager;

    @DeleteMapping("finished/incidents")
    public ResponseEntity<Incident> delete(HttpServletRequest request, @RequestBody(required = true) Incident body) throws AbstractPcdException {

        LOGGER.debug("End validation request received with type{} and date{} and latitude{} and longitude{} and confirmation{}", body.getType(), body.getDateI(), body.getLatitude(), body.getLongitude(), body.getConfirmation());
        Incident reponseIncident = new Incident();
        try {

            if ((body.getEtat()=="finished")&&(manager.valider(body.getType(), body.getLatitude(), body.getLongitude())=="validation")){reponseIncident = null; }
            } catch (AbstractPcdException e) {
            LOGGER.error("An error occured {}", e);
            throw e;  
        } catch (Exception e) {
            LOGGER.error("An error occured {}", e);
        }

        return new ResponseEntity<>(reponseIncident, HttpStatus.OK);
    }
    @GetMapping("started/incidents")
    public ResponseEntity<Incident> Start(HttpServletRequest request, @RequestBody(required = true) Incident body) throws AbstractPcdException {
    Incident reponseIncident = new Incident();

        try {
        if ((reponseIncident.getEtat()=="started")&&(manager.valider(body.getType(), body.getLatitude(), body.getLongitude())!="validation")){reponseIncident=null;}
    } catch (AbstractPcdException e) {
        LOGGER.error("An error occured {}", e);
        throw e;
    } catch (Exception e) {
        LOGGER.error("An error occured {}", e);
    }
    return new ResponseEntity<>(reponseIncident, HttpStatus.OK);
}
}
