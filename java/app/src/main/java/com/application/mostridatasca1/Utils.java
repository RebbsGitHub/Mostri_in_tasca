package com.application.mostridatasca1;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    public static int getAccountUID(Application application){
        SharedPreferences sharedPreferences = application.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("UID", 0);
    }

    public static String getAccountSID(Application application) {
        SharedPreferences sharedPreferences = application.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE);
        return sharedPreferences.getString("SID", null);
    }





}
