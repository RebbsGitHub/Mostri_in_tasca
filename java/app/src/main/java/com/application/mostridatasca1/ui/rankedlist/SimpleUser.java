package com.application.mostridatasca1.ui.rankedlist;

public class SimpleUser {


    public int uid;
    public int life;
    public int experience;
    public int profileversion;
    public boolean positionshare;

    public Double lat;
    public Double lon;

    public SimpleUser(int uid, int life, int experience, int profileversion, boolean positionshare) {
        this.uid = uid;
        this.life = life;
        this.experience = experience;
        this.profileversion = profileversion;
        this.positionshare = positionshare;
        this.lat = null;
        this.lon = null;
    }

    public SimpleUser(int uid, int life, int experience, int profileversion, boolean positionshare, Double lat, Double lon) {
        this.uid = uid;
        this.life = life;
        this.experience = experience;
        this.profileversion = profileversion;
        this.positionshare = positionshare;
        this.lat = lat;
        this.lon = lon;
    }

    // per i player nella mappa
    public SimpleUser(int uid, Double lat, Double lon, int profileversion , int life, int experience, String timestamp){
        this.uid = uid;
        this.life = life;
        this.experience = experience;
        this.profileversion = profileversion;
        this.lat = lat;
        this.lon = lon;
        this.positionshare = true;
    }

}
