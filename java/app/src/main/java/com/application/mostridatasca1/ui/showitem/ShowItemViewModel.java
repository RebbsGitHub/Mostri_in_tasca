package com.application.mostridatasca1.ui.showitem;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;
import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;
import com.application.mostridatasca1.networkcalls.ApiInterface;
import com.application.mostridatasca1.networkcalls.RetrofitProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowItemViewModel extends ViewModel {


    private ApiInterface apiInterface = RetrofitProvider.getApiInterface();
    private ObjectRepository objectRepository;
    private static final String TAG = "MyLog";

    private MutableLiveData<VirtualObj> virtualObjLiveData = new MutableLiveData<>();

    public ShowItemViewModel(ObjectRepository objectRepository) {this.objectRepository = objectRepository;}


    public LiveData<VirtualObj> getItem(int itemID,String SID) {

        //Chiamata ad DataBase per vedere se abbiamo l'oggetto in questione
        new Thread(() -> {
            VirtualObj item = objectRepository.objectDao().getObjectByID(itemID);

            if(item == null){
                Log.d(TAG, "getItem: Item chiesto al Server...");
                // chiamata server
                apiInterface.getVirtualObjectByID(itemID, SID).enqueue(new Callback<VirtualObj>() {
                    @Override
                    public void onResponse(Call<VirtualObj> call, Response<VirtualObj> response) {
                        if(response.isSuccessful()){
                            VirtualObj virtualObj = response.body();
                            virtualObjLiveData.postValue(virtualObj);
                            new Thread(() -> objectRepository.objectDao().insertAll(virtualObj)).start();
                        }
                    }
                    @Override
                    public void onFailure(Call<VirtualObj> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t.getMessage());
                    }
                });
            }else{
                Log.d(TAG, "getItem: Item trovato nel DB");
                virtualObjLiveData.postValue(item);
            }
        }).start();
        return virtualObjLiveData;

    }

    public static String getAccountSID(Application application) {
        SharedPreferences sharedPreferences = application.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE);
        return sharedPreferences.getString("SID", null);
    }

    public Bitmap decodeTobase64(String imageEncoded){
        byte[] decodedString = Base64.decode(imageEncoded, Base64.DEFAULT);
        Bitmap decodedByte = Bitmap.createBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        return decodedByte;
    }

}


// sid 0Lv6ABRMzIpdGunUrGtb
// id  29075
/*
{ VOGLIIO PRENDERE QEUSTO
        "id": 22,
        "lat": 45.48,
        "lon": 9.26,
        "type": "armor"
    },
*
* */