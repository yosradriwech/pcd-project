package com.pcd.rest.dao.mongodb.document;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document
@CompoundIndexes(value = { @CompoundIndex(name = "ind1", def = "{'type' : 1, 'latitude': 1,'longitude' : 1}", unique = true) })
public class Incident {
    @Id
    private int id;
    private Date dateI;
    private String etat;
    private int confirmation;
    private int t;
    private String type;
    private double latitude;
    private double longitude;
    private User user;
    @CreatedDate
    private Date creationDate;
    private Date activationDate;
    private Date deActivationDate;
    @LastModifiedDate
    private Date lastUpdateDate;

    public User getUser() {
        return user;
    }

    public Incident(String type, double latitude, double longitude, Date dateI, String etat, User user) {
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateI = dateI;
        this.etat = etat;

        this.confirmation = 0;
        this.t = 60;
        this.user = user;
    }
    public Incident copierIncident(Incident objet) {
        this.type = objet.getType();
        this.latitude = objet.getLatitude();
        this.longitude = objet.getLongitude();
        this.dateI = objet.getDateI();
        this.etat = objet.getEtat();
        this.confirmation = objet.getConfirmation();
        this.t = objet.getT();
    return this; }

    public Incident() {
    }

    public Date getDateI() {
        return dateI;
    }

    public void setDateI(Date dateI) {
        this.dateI = dateI;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public int getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(int confirmation) {
        this.confirmation = confirmation;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}