package com.pcd.rest.dao.mongodb.repository;

import com.pcd.rest.dao.mongodb.document.Incident;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IncidentRepository extends MongoRepository<Incident, String> {
    Incident findOneByTypeAndLatitudeAndLongitude(String type,double latitude,double longitude);
    Incident save(Incident incident);
    void delete(Incident incident);
   }
