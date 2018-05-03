package com.pcd.rest.manager;

import com.pcd.rest.dao.mongodb.document.User;
import com.pcd.rest.dao.mongodb.repository.UserRepository;
import com.pcd.rest.manager.exception.AbstractPcdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class AuthentificationManager {
        private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(AuthentificationManager.class);

        @Autowired
        private UserRepository userRepository;

        public String authentifier(String login,String password){
        TECHNICAL_LOGGER.debug("Starting authentification business");
        String retour="" ;

        User userSessionFound = userRepository.findOneByLogin(login);

        if (Objects.isNull(userSessionFound)) {
            TECHNICAL_LOGGER.error("No user found for '{}'",  login);
            retour = "Not.Found" ;}
        else if (userSessionFound.getPassword()!=password){retour = "Wrong.Password";}
        else if (userSessionFound.getPassword()==password){ retour=userSessionFound.getStatus();}
        return retour ;
    }
}
