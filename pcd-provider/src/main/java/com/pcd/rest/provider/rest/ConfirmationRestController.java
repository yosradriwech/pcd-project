package com.pcd.rest.provider.rest;

import com.pcd.rest.dao.mongodb.document.Incident;
import com.pcd.rest.dao.mongodb.repository.IncidentRepository;
import com.pcd.rest.manager.ConfirmationManager;
import com.pcd.rest.manager.exception.AbstractPcdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping("confirmations")
public class ConfirmationRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationRestController.class);

    @Autowired
    private ConfirmationManager manager;
    private IncidentRepository IncidentRepository;

    @PutMapping("/incidents")
    public ResponseEntity<Incident> confirm(HttpServletRequest request, @RequestBody(required = true) Incident body) throws AbstractPcdException {

        LOGGER.debug("confirmation request received with type{} and date{} and latitude{} and longitude{} and confirmation{}", body.getType(), body.getDateI(), body.getLatitude(), body.getLongitude(), body.getConfirmation());
        Incident incidentResponse = IncidentRepository.findOneByTypeAndLatitudeAndLongitude(body.getType(), body.getLatitude(), body.getLongitude());
        try {

            incidentResponse = manager.confirm(body.getType(), body.getLatitude(), body.getLongitude(),body.getConfirmation(),body.getEtat());

        } catch (AbstractPcdException e) {
            LOGGER.error("An error occured {}", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("An error occured {}", e);
        }
        if (Objects.isNull(incidentResponse)) {return new ResponseEntity<>(incidentResponse, HttpStatus.NOT_FOUND);}
        else { return new ResponseEntity<>(incidentResponse, HttpStatus.OK);}
    }
}
//jawou behi
