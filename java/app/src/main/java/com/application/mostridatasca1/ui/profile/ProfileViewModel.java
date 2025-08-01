package com.application.mostridatasca1.ui.profile;


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
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository_Impl;
import com.application.mostridatasca1.networkcalls.ApiInterface;
import com.application.mostridatasca1.networkcalls.RetrofitProvider;
import com.application.mostridatasca1.networkcalls.UpdatePositionShare;
import com.application.mostridatasca1.networkcalls.UserUpdate;

import java.io.ByteArrayOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {

    private ApiInterface apiInterface = RetrofitProvider.getApiInterface();     // get interfaccia per chiamate STATICA e non capisco il come mai
    private static final String TAG = "MyLog";
    private UserRepository userRepository;
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();


    public ProfileViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    public int checkDatabase(Application application, int UID,String SID) {

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
            if (user == null) {Log.e(TAG, "outputDatabase: database vuoto - richiesta getProfileByUID() annullata!");}else
                userLiveData.postValue(user);
        }).start();
        return userLiveData;
    }


    public void updateProfileNameVM(String SID, int UID, String newName) {
        new Thread(() -> {
            UserUpdate userUpdate = new UserUpdate(SID, newName);
            userUpdate.readUP();

            Call<Void> updateUserCall = apiInterface.updateUser(UID, userUpdate);
            updateUserCall.enqueue(new Callback<Void>() {

                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                        Log.e(TAG, "updateProfileNameVM onResponse ERROR: " + response.code());
                    }else{
                        Log.d(TAG, "updateProfileNameVM onResponse GOOD_ENDING: "+response.code());
                        }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "updateProfileNameVM onFailure ERROR: " + t.getMessage());
                }
            });
            Call<User> userCall = apiInterface.getUserByID(UID,SID);
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "updateProfileNameVM onResponse ERROR: " + response.code());
                    }else{
                        new Thread(() -> {
                            User user = response.body();
                            userRepository.userDao().updateProfileName(UID, user.name);
                            userRepository.userDao().updateProfileVersion(UID, user.profileversion);
                            Log.d(TAG, "Aggiornamento dati del database: "+user.name+" "+user.profileversion);
                        }).start();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "updateProfileNameVM onFailure ERROR: " + t.getMessage());
                }
            });
        }).start();
    }


    public void updateProfileImgVM(String SID,int UID, String image){

        new Thread(() -> {
            UserUpdate userUpdate = new UserUpdate(SID, null, image);
            userUpdate.readUP();

            Call<Void> updateUserCall = apiInterface.updateUser(UID, userUpdate);
            updateUserCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "updateProfileImgVM onResponse ERROR: " + response.code());
                    }else{
                        Log.d(TAG, "updateProfileImgVM onResponse GOOD_ENDING: "+response.code());
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "updateProfileImgVM onFailure ERROR: " + t.getMessage());
                }

            });
            Call<User> userCall = apiInterface.getUserByID(UID,SID);
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "updateProfileImgVM onResponse ERROR: " + response.code());
                    }else{
                        new Thread(() -> {
                            User user = response.body();
                            userRepository.userDao().updateProfileImg(UID, user.picture);
                            userRepository.userDao().updateProfileVersion(UID, user.profileversion);
                            Log.d(TAG, "Aggiornamento dati del database: +user.picture+ "+user.profileversion);
                        }).start();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "updateProfileImgVM onFailure ERROR: " + t.getMessage());
                }
            });
        }).start();

    }
    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

        Log.d(TAG, "encodeTobase64: image "+imageEncoded);
        return imageEncoded;
    }
    public boolean isBitmapBiggerThan10KB(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        int sizeInKB = byteArray.length / 1024;
        return sizeInKB > 100;
    }
    public Bitmap decodeTobase64(String imageEncoded){
        byte[] decodedString = Base64.decode(imageEncoded, Base64.DEFAULT);
        Bitmap decodedByte = Bitmap.createBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        return decodedByte;
    }


    public void checkAndChangePostionShareVM(String SID, int UID, boolean state) {

        new Thread(() -> {
            User user = userRepository.userDao().getProfileByUID(UID);
            if(user.positionshare != state){
                UpdatePositionShare updatePositionShare = new UpdatePositionShare(SID, state);
                updatePositionShare.readUP();

                Call<Void> updateUserCall = apiInterface.updatePositionShare(UID, updatePositionShare);
                updateUserCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful())
                            Log.e(TAG, "checkAndChangePostionShareVM onResponse ERROR: " + response.code());
                        else
                            Log.d(TAG, "checkAndChangePostionShareVM onResponse GOOD_ENDING: "+response.code());

                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "checkAndChangePostionShareVM onFailure ERROR: " + t.getMessage());
                    }

                });
                Call<User> userCall = apiInterface.getUserByID(UID,SID);
                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "checkAndChangePostionShareVM onResponse ERROR: " + response.code());
                        }else{
                            new Thread(() -> {
                                User user = response.body();
                                userRepository.userDao().updatePositionShare(UID, user.positionshare);
                                userRepository.userDao().updateProfileVersion(UID, user.profileversion);
                            }).start();
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "updateProfileNameVM onFailure ERROR: " + t.getMessage());
                    }
                });
            }


        }).start();

    }

    public MutableLiveData<String> ObjectImage(ObjectRepository objectRepository, int itemID) {
        MutableLiveData<String> itemImage = new MutableLiveData<>();
        new Thread(() -> {itemImage.postValue(objectRepository.objectDao().getObjectByID(itemID).image);}).start();
    return itemImage;
    }


}//ProfileViewModel 0Lv6ABRMzIpdGunUrGtb 29075