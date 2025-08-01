package com.application.mostridatasca1.ui.playersprofilefragment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;
import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;
import com.application.mostridatasca1.networkcalls.ApiInterface;
import com.application.mostridatasca1.networkcalls.RetrofitProvider;
import com.application.mostridatasca1.networkcalls.UpdatePositionShare;
import com.application.mostridatasca1.networkcalls.UserUpdate;

import java.io.ByteArrayOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayersProfileViewModel extends ViewModel {

    private ApiInterface apiInterface = RetrofitProvider.getApiInterface();     // get interfaccia per chiamate STATICA e non capisco il come mai
    private UserRepository userRepository;
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private static final String TAG = "MyLog";

    public PlayersProfileViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }






    public int checkDatabase(Application application, int UID, String SID) {

        new Thread(() -> {
            List<User> users;
            users = userRepository.userDao().getAll();
            if (users.isEmpty()) {
                Log.e(TAG, "outputDatabase: database vuoto - Primo avvio, istanziamento profilo utente!");
                Call<User> userCall = apiInterface.getUserByID(UID, SID);
                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (!response.isSuccessful()) {
                            Log.d("MyLog", "Error: " + response.code());
                            return;
                        }
                        new Thread(() -> {
                            User user = response.body();
                            userRepository.userDao().insertAll(user);
                        }).start();
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Error: " + t.getMessage());
                    }
                });
                Log.d(TAG, "outputDatabase: Fine istanziamento utente");

            }else
                for (User user : users) {
                    scriviBene(user);
                }
        }).start();

        return (UID);
    }//outputDatabase
    private void scriviBene(User user) {
        Log.d(TAG, "outputDatabase: user[ UID:"+user.uid + " Nome:" + user.name + " HP:" + user.life + " XP:" + user.experience + " weaponID:" + user.weapon + " armorID:" + user.armor + " amuletID:" + user.amulet + "  (PICTURE)  profileVersion:" + user.profileversion + " positionShare:" + user.positionshare + " ]");
    }


    public int getAccountUID(Application application){
        SharedPreferences sharedPreferences = application.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("UID", 0);
    }
    public static String getAccountSID(Application application) {
        SharedPreferences sharedPreferences = application.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE);
        return sharedPreferences.getString("SID", null);
    }
    public LiveData<User> getProfile(int UID) {
        new Thread(() -> {
            User user = userRepository.userDao().getProfileByUID(UID);
            if (user == null) {Log.e(TAG, "outputDatabase: profilo non salvato - richiesta getProfileByUID() annullata!");
            }else
                userLiveData.postValue(user);
        }).start();
        return userLiveData;
    }




    public Bitmap decodeTobase64(String imageEncoded){
        byte[] decodedString = Base64.decode(imageEncoded, Base64.DEFAULT);
        try {
            Bitmap decodedByte = Bitmap.createBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            return decodedByte;
        } catch (Exception e) {
            Log.e(TAG, "Errore nel caricamento dell'immagine - something's null");
            return null;
        }
    }

    public MutableLiveData<String> ObjectImage(ObjectRepository objectRepository, int itemID) {

        MutableLiveData<String> itemImage = new MutableLiveData<>();

            new Thread(() -> {
                VirtualObj i =  objectRepository.objectDao().getObjectByID(itemID);
                if(i.image!= null){
                    itemImage.postValue(i.image);
                }else{
                    itemImage.postValue(null);
                }
            }).start();



        return itemImage;
    }


}
