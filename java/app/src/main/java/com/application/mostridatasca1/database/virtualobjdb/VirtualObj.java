package com.application.mostridatasca1.database.virtualobjdb;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class VirtualObj {

    @PrimaryKey
    public int id;
    public String type;
    public int level;
    public Double lat;
    public Double lon;
    public String image;

    public String name;


    public VirtualObj(int id, String type, int level, Double lat, Double lon, String image, String name) {
        this.id = id;
        this.type = type;
        this.level = level;
        this.lat = lat;
        this.lon = lon;
        this.image = image;
        this.name = name;
    }

    public void scriviBene(){
        Log.d("MyLog", "VirtualObj:[id:"+id+"  type:"+type+"  level:"+level+"  lat:"+lat+"  lon:"+lon+"  image:(IMAGE)  name:"+name+"]");
    }
}

