package com.application.mostridatasca1.networkcalls;

import android.util.Log;

public class UpdatePositionShare {

    public String sid;
    public boolean positionshare;

    public UpdatePositionShare(String sid, boolean positionshare) {
        this.sid = sid;
        this.positionshare = positionshare;
    }

    public void readUP(){
        Log.d("MyLog", "sid: "+sid);
        Log.d("MyLog", "positionshare: "+positionshare);
    }
    //set

}
