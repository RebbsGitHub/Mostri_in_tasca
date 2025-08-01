package com.application.mostridatasca1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.application.mostridatasca1.database.simplevirtualobjdb.SimpleVirtualObj;
import com.application.mostridatasca1.database.simplevirtualobjdb.SimpleVirtualObjRepository;
import com.application.mostridatasca1.networkcalls.ApiInterface;
import com.application.mostridatasca1.networkcalls.RetrofitProvider;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel extends ViewModel {
    private static final String TAG = "MyLog";
    private MutableLiveData<Double> playerLAT = new MutableLiveData<>();
    private MutableLiveData<Double> playerLON = new MutableLiveData<>();
    private MutableLiveData<Location> playerLocation = new MutableLiveData<>();
    private MutableLiveData<List<SimpleVirtualObj>> itemAround = new MutableLiveData<>(); // lista degli item attorno sempre aggiornata
    private ApiInterface apiInterface = RetrofitProvider.getApiInterface();
    private List<SimpleVirtualObj> oldObj = null;

    public MainActivityViewModel() {
        super();
        playerLAT.setValue(null);
        playerLON.setValue(null);

    }


//------------------Chiamata LocationRequest----------------------------------
    public void requestLocationUpdates(Activity activity) {
        Context context = activity.getApplicationContext();

        CurrentLocationRequest clr = new CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        Task<Location> task = fusedLocationClient.getCurrentLocation(clr, null);
        task.addOnSuccessListener(location -> {
                    if (location != null)
                        Log.d("MyLog", "First acquisition location time : " +location.getTime());
                    else
                        Log.e("MyLog", "Somethings wrong with location acquisition! un bel casino!");
                });
        LocationRequest locationRequest = new LocationRequest.Builder(2000)
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .build();
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    private final LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.d(TAG, "onLocationAvailability " + locationAvailability.isLocationAvailable());
        }

        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            //Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation().toString());
            playerLAT.setValue(locationResult.getLastLocation().getLatitude());
            playerLON.setValue(locationResult.getLastLocation().getLongitude());
            playerLocation.setValue(locationResult.getLastLocation());
        }
    };

//---------------------------fine locationRequest-------------------------------------

    public MutableLiveData<Double> getPlayerLAT() {
        return playerLAT;
    }
    public MutableLiveData<Double> getPlayerLON() {
        return playerLON;
    }
    public void updateItemsAround(List<SimpleVirtualObj> svoList) {
        itemAround.postValue(null);
        itemAround.postValue(svoList);
    }
    public MutableLiveData<List<SimpleVirtualObj>> getItemsAround() {
        return itemAround;
    }
    public MutableLiveData<Location> getPlayerLocation() {
        return playerLocation;
    }

//------------------Aggiornamento lista oggetti----------------------------------

    public void updateObjectList(SimpleVirtualObjRepository simpleVirtualObjRepository, String sid) {
        if( playerLAT.getValue() == null || playerLON.getValue() == null) {
            Log.e(TAG, "updateObjectList:  LAT LON VUOTI - SKIP AGGIORNAMENTO" );
            return;
        }
        double lat = playerLAT.getValue();
        double lon = playerLON.getValue();

        new Thread(() ->{
            Call<List<SimpleVirtualObj>> nearObjects = apiInterface.getNearObjects(sid, lat,lon);
            nearObjects.enqueue(new Callback<List<SimpleVirtualObj>>() {
                @Override
                public void onResponse(Call<List<SimpleVirtualObj>> call, Response<List<SimpleVirtualObj>> response) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "updateObjectList: onResponse ERROR " + response.code());
                        return;
                    }
                    List<SimpleVirtualObj> simpleVirtualObjList = response.body();
                    if (simpleVirtualObjList == null) {
                        Log.e(TAG, "updateObjectList: simpleVirtualObjList è NULL");
                        return;
                    }
                    //TRUE: c'è stato un cambiamento - FALSE; successo un catso
                    if(checkSVOUpdates(simpleVirtualObjList)) {
                        Log.d(TAG, "updateObjectList: CAMBIAMENTO LISTA OGGETTI");
                        new Thread(() -> {
                            simpleVirtualObjRepository.simpleVirtualObjDao().deleteAll();
                            simpleVirtualObjRepository.simpleVirtualObjDao().insertAll(simpleVirtualObjList);

                        }).start();
                    }

                    // lista per mappa Log.e(TAG, "MainActivityModel onResponse: update itemsAround (riga 150)");
                    updateItemsAround(simpleVirtualObjList);

                }

                @Override
                public void onFailure(Call<List<SimpleVirtualObj>> call, Throwable t) {
                    Log.e(TAG, "updateObjectList: onFailure ERROR " + t.getMessage());
                }
            });
        }).start();
    }

    private boolean checkSVOUpdates(List<SimpleVirtualObj> newSVOList) {

        if (oldObj == null) {
            Log.d(TAG, "checkSVOUpdates: SKIP CONTROLLO - database non aggiornato");
            oldObj = newSVOList;
            return false;
        }
        if(newSVOList.size() != oldObj.size()) {
            Log.d(TAG, "checkSVOUpdates: CAMBIAMENTO DIMENSIONE LISTA");
            oldObj = newSVOList;
            return true;
        }

        for (int i = 0; i < newSVOList.size(); i++) {
            if(newSVOList.get(i).id != oldObj.get(i).id) {
                Log.d(TAG, "checkSVOUpdates: CAMBIAMENTO ID elemento " + i);
                oldObj = newSVOList;
                return true;
            }
        }
        oldObj = newSVOList;
        return false;
    }

//------------------Fine aggiornamento lista oggetti----------------------------------

    public void setPlayerLAT(Double oldLat) {
        playerLAT.setValue(oldLat);
    }
    public void setPlayerLON(Double oldLon) {
        playerLON.setValue(oldLon);
    }




}// MainActivityViewModel

