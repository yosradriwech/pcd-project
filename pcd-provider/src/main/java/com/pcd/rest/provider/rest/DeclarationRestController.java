package com.pcd.rest.provider.rest;

import com.pcd.rest.dao.mongodb.document.Incident;
import com.pcd.rest.dao.mongodb.repository.IncidentRepository;
import com.pcd.rest.manager.DeclarationManager;
import com.pcd.rest.manager.exception.AbstractPcdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("declarations")
public class DeclarationRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeclarationRestController.class);

    @Autowired
    private DeclarationManager manager;
    private IncidentRepository IncidentRepository;

    @PostMapping("/incidents/{login}")
    public ResponseEntity<Incident> declare(HttpServletRequest request, @PathVariable String login, @RequestBody(required = true) Incident body) throws AbstractPcdException {
        LOGGER.debug("declaration request received with type{} and date{} and latitude{} and longitude{} and confirmation{}", body.getType(), body.getDateI(), body.getLatitude(), body.getLongitude(), body.getConfirmation());
       Incident createIncident = null;
        try {
            createIncident = manager.declare(body,login);
        } catch (AbstractPcdException e) {
            LOGGER.error("An error occured {}", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("An error occured {}", e);
        }
        return new ResponseEntity<>(createIncident, HttpStatus.CREATED);
    }

    }
//ici yosr verif el conf de la reponse si c zero thabet toul si c -1 c pas validé et donc elle passe au confirmations