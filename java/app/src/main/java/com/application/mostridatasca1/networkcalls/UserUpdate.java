package com.application.mostridatasca1.networkcalls;

import android.util.Log;

import com.application.mostridatasca1.database.playerdb.User;

public class UserUpdate {

    public String sid;
    public String name;
    public String picture;
    //public boolean profileshare;

    public UserUpdate(String sid, String name, String picture) {
        this.sid = sid;
        this.name = name;
        this.picture = picture;
        //this.profileshare = profileshare;
    }

    public UserUpdate(String sid, String name){
        this.sid = sid;
        this.name = name;
        this.picture = null;
        //this.profileshare = ;
    }


    public void readUP(){
        Log.d("MyLog", "sid: "+sid);
        Log.d("MyLog", "name: "+name);
        Log.d("MyLog", "picture: "+picture);
        //Log.d("MyLog", "profileshare: "+profileshare);

    }
  //set

}
