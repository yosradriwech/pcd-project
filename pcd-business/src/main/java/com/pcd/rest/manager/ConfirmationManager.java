package com.pcd.rest.manager;

import com.pcd.rest.dao.mongodb.document.Incident;
import com.pcd.rest.dao.mongodb.repository.IncidentRepository;
import com.pcd.rest.manager.exception.AbstractPcdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class ConfirmationManager {
    private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(ConfirmationManager.class);

    @Autowired
    private IncidentRepository IncidentRepository;

    public int confirm(String type, double latitude, double longitude, int conf) throws AbstractPcdException {
        TECHNICAL_LOGGER.debug("Starting confirmation business");

        Incident incidentFound = IncidentRepository.findOneByTypeAndLatitudeAndLongitude(type,latitude,longitude);

        if (Objects.isNull(incidentFound)) {
            TECHNICAL_LOGGER.error("No  incident found for '{}'", type,latitude,longitude); }
        else
            incidentFound.setConfirmation(conf);

        return incidentFound.getConfirmation();
    }

}
