package com.application.mostridatasca1.database.simplevirtualobjdb;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SimpleVirtualObj {

    @PrimaryKey
    public int id;
    public Double lat;
    public Double lon;
    public String type;

    public SimpleVirtualObj(int id, Double lat, Double lon, String type) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
    }

    public void scriviBene(){
        Log.d("MyLog", "SimpleVirtualObj:[id:"+id+" lat:"+lat+" lon:"+lon+" type:"+type+"]");
    }
}