package com.pcd.rest.manager;

import com.pcd.rest.dao.mongodb.document.Incident;
import com.pcd.rest.dao.mongodb.document.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.pcd.rest.dao.mongodb.repository.IncidentRepository;
import com.pcd.rest.dao.mongodb.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DeclarationManager {
    private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(DeclarationManager.class);

    @Autowired
    private IncidentRepository IncidentRepository;
    @Autowired
    private UserRepository userRepository;

    public Incident declare(Incident incident, String login){
        TECHNICAL_LOGGER.debug("Starting declaration business");
        User userSessionFound = userRepository.findOneByLogin(login);
        Incident declarationResponse = new Incident(incident.getType(), incident.getLatitude(), incident.getLongitude(), incident.getDateI(),incident.getEtat(),userSessionFound);

        if (Objects.isNull(userSessionFound)) {
            TECHNICAL_LOGGER.error("No declaration can be held for '{}'", login);
        } else {
            if (userSessionFound.getTrustRank() <= 10) {
                declarationResponse.setConfirmation(-1);
            }
        }
        IncidentRepository.save(declarationResponse);
        return declarationResponse;
    }
}
// houni el verifie el conf ken -1 she need confirmations sinn si c zero c confirmer
