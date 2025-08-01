package com.application.mostridatasca1.database.playerdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    public int uid;

    public String name;
    public int life;
    public int experience;
    public int weapon;
    public int armor;
    public int amulet;
    public String picture;
    public int profileversion;
    public boolean positionshare;

    public User(int uid, String name, int life, int experience, int weapon, int armor, int amulet, String picture, int profileversion, boolean positionshare) {
        this.uid = uid;
        this.name = name;
        this.life = life;
        this.experience = experience;
        this.weapon = weapon;
        this.armor = armor;
        this.amulet = amulet;
        this.picture = picture;
        this.profileversion = profileversion;
        this.positionshare = positionshare;
    }
}

