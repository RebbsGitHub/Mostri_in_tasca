package com.application.mostridatasca1.ui.otherfragments;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.networkcalls.ApiInterface;
import com.application.mostridatasca1.networkcalls.RetrofitProvider;
import com.application.mostridatasca1.networkcalls.SignUp;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoadingViewModel extends AndroidViewModel {


    private static final String TAG = "MyLog";
    private ApiInterface apiInterface = RetrofitProvider.getApiInterface();



    public LoadingViewModel(Application application) {
        super(application);

    }




    public void registerNewSID() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("com.application.mostridatasca1",Context.MODE_PRIVATE);
        Call<SignUp> signUpCall = apiInterface.register();
        signUpCall.enqueue(new Callback<SignUp>() {
            @Override
            public void onResponse(Call<SignUp> call, retrofit2.Response<SignUp> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "profileSIDprovider error: " + response.code());
                    return;
                }
                SignUp result = response.body();
                Log.d(TAG, "profileSIDprovider response: " + result.sid+" "+result.uid);

                // applico i valori ottenuti alle SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("SID", result.sid);
                editor.putInt("UID", result.uid);
                editor.apply();

            }
            @Override
            public void onFailure(Call<SignUp> call, Throwable t) {
                Log.e(TAG, "profileSIDprovider error: " + t.getMessage());
            }
        });
    }







} // LoadingViewModel
