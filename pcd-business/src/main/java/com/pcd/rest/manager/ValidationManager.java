package com.pcd.rest.manager;

import com.pcd.rest.dao.mongodb.document.Incident;
import com.pcd.rest.dao.mongodb.repository.IncidentRepository;
import com.pcd.rest.manager.exception.AbstractPcdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class ValidationManager {
    private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(ValidationManager.class);

    @Autowired
    private IncidentRepository IncidentRepository;

    public String valider(String type, double latitude, double longitude){
        TECHNICAL_LOGGER.debug("Starting validation business");

        String retour="";
       Incident validationIncident = IncidentRepository.findOneByTypeAndLatitudeAndLongitude(type,latitude,longitude);

        if (Objects.isNull(validationIncident)) {
            TECHNICAL_LOGGER.error("No  incident found for '{}'",  type,latitude,longitude); }
        else
            {if ( validationIncident.getConfirmation()>=0) { retour = "validation";
                if (validationIncident.getEtat() == "S") {validationIncident.getUser().setTrustRank(1);}
                if (validationIncident.getEtat() == "F") {IncidentRepository.delete(validationIncident);}}
             else retour =  "nonValidation" ;}
        return retour;
    }
}
//F==finished
//S==started