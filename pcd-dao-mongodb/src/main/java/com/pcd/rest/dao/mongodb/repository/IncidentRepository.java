package com.pcd.rest.dao.mongodb.repository;

import com.pcd.rest.dao.mongodb.document.Incident;

public interface IncidentRepository {
    Incident findOneByTypeAndLatitudeAndLongitude(String type,double latitude,double longitude);
}
