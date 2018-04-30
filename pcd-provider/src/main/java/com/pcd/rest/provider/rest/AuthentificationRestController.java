package com.pcd.rest.provider.rest;

import com.pcd.rest.dao.mongodb.document.User;
import com.pcd.rest.manager.AuthentificationManager;
import com.pcd.rest.manager.exception.AbstractPcdException;
import org.apache.catalina.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("Authentifications")
public class AuthentificationRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthentificationRestController.class);

    @Autowired
    private AuthentificationManager manager;

    @GetMapping("/users/{login}")
    public ResponseEntity<Void> authentifier(HttpServletRequest request, @PathVariable String login, @RequestBody(required = true) User body) throws AbstractPcdException {
        String status="";
        try { status = manager.authentifier(login,body.getPassword());

        } catch (AbstractPcdException e) {
            throw e;
        }
        if (status=="Not.Found"){return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        else if (status=="Wrong.Password"){return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        else {return new ResponseEntity<>(HttpStatus.OK);}
    }
}
//jawou behi