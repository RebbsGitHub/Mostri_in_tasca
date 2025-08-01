package com.application.mostridatasca1.ui.rankedlist;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;

import com.application.mostridatasca1.R;
import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;
import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;
import com.application.mostridatasca1.networkcalls.ApiInterface;
import com.application.mostridatasca1.networkcalls.RetrofitProvider;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankedViewModel extends ViewModel {

    private static final String TAG = "MyLog";
    // API
    private ApiInterface apiInterface = RetrofitProvider.getApiInterface();
    private List<User> listaPlayer = new ArrayList<>();
    // Lista che si riempie poco alla volta
    MutableLiveData<List<User>> listaPlayerCompletaViewModel;


    public RankedViewModel() {
        listaPlayerCompletaViewModel = new MutableLiveData<>();
    }



    public MutableLiveData<List<SimpleUser>> getRankedPlayers(String sid) {

    MutableLiveData<List<SimpleUser>> simpleRankedPlayers = new MutableLiveData<>();

        Call<List<SimpleUser>> updateUserCall = apiInterface.getRanking(sid);
        updateUserCall.enqueue(new Callback<List<SimpleUser>>() {

            @Override
            public void onResponse(Call<List<SimpleUser>> call, Response<List<SimpleUser>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "getRankedPlayers onResponse ERROR: " + response.code());
                }else{
                    simpleRankedPlayers.postValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<SimpleUser>> call, Throwable t) {
                Log.e(TAG, "getRankedPlayers onFailure ERROR: " + t.getMessage());

            }
        });
    return simpleRankedPlayers;
    }

    public void setUpRankedList(UserRepository userRepository, List<SimpleUser> rankedList, String SID, ObjectRepository objectRepository) {
        listaPlayer.clear();

        new Thread(() -> {

            // richiesta Database User che abbiamo - OK
            List<User> listaBigUsers = userRepository.userDao().getAll();
            Log.e(TAG, "listaBigUsers: "+listaBigUsers.size() );
            Log.e(TAG, "rankedList: "+rankedList.size() );

            // Iterazione sulla lista dei migliori giocatori
            new Thread(()->{
                for (SimpleUser user : rankedList) {
                    boolean flag = false;
                    for (User bigUser: listaBigUsers)
                         if (user.uid == bigUser.uid) {
                             // ESISTE NEL DB: salvataggio nella lista
                             Log.d(TAG, "setUpRankedList: "+user.uid+" esiste!");

                             if (bigUser.profileversion == user.profileversion) {
                                 listaPlayer.add(bigUser);
                                 checkItemsRankedPlayer(objectRepository, SID, bigUser); // da togliere dopo la prima iterazione completa, per fare un check
                             }else{// AGGIORNAMENTO DEL DB
                                 User newprofile = updateDBprofile(user.uid, SID, userRepository);
                                 listaPlayer.add(newprofile);
                                 checkItemsRankedPlayer(objectRepository, SID, newprofile);
                             }

                             flag = true;
                             break;
                         }
                    if (!flag) {
                        // INESISTENTE NEL DB - aggiorno il db e salvataggio nella lista
                        richiestaServerAggiornamentoDB(user.uid,SID, userRepository, objectRepository);
                    }
                }
            }).start();
            // aggiornamento listaPlayerCompletaViewModel MutableLiveData
            listaPlayerCompletaViewModel.postValue(new ArrayList<>());
            listaPlayerCompletaViewModel.postValue(listaPlayer);
        }).start();

    }
    private void richiestaServerAggiornamentoDB(int uid, String SID, UserRepository userRepository,ObjectRepository objectRepository) {

        Call<User> updateUserCall = apiInterface.getUserByID(uid, SID);
        updateUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "setUpRankedList onResponse ERROR: " + response.code());
                }else{
                    Log.d(TAG, "setUpRankedList onResponse GOOD_ENDING: "+response.code());
                    User user = response.body();
                    new Thread (() -> userRepository.userDao().insertAll(user)).start();

                    // aggiunta alla Mutable del Viewmodel
                    listaPlayer.add(user);
                    checkItemsRankedPlayer(objectRepository, SID, user);

                }//---
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "setUpRankedList onFailure ERROR: " + t.getMessage());
            }
        });
    }
    public MutableLiveData<List<User>> getRankedListCompletaVM() {
        return listaPlayerCompletaViewModel;
    }
    public int getRankedListCompletaVMNumber(){
        return listaPlayer.size();
    }
    public User getUserCompleto(int position) {
        return listaPlayer.get(position);
    }
    private User updateDBprofile(int uid,String SID,UserRepository userRepository){
        Log.d(TAG, "updateDBprofile: Profilo da aggiornare: "+uid);
        MutableLiveData<User> user = new MutableLiveData<>();
        Call<User> updateUserCall = apiInterface.getUserByID(uid, SID);
        updateUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "setUpRankedList onResponse ERROR: " + response.code());
                }else{
                    Log.d(TAG, "setUpRankedList onResponse GOOD_ENDING: "+response.code());
                    User u = response.body();
                    user.postValue(u);
                    new Thread (() -> {
                        if(u.uid == uid) {
                            Log.d(TAG, "updateDBprofile: TUTTO CORRETTO");
                            userRepository.userDao().deleteUserByUID(uid);
                            userRepository.userDao().insertAll(u);
                        }else
                            Log.e(TAG, "updateDBprofile: ERRORE CORRISPONDENZA: u.uid == uid");
                    }).start();

                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "setUpRankedList onFailure ERROR: " + t.getMessage());
            }
        });
    return user.getValue();
    }

    public void navigateToPlayersProfilesFragment(NavController nvc, int profileUID) {
        Bundle bundle = new Bundle();
        bundle.putInt("profileUID", profileUID);
        nvc.navigate(R.id.playersProfileFragment, bundle);

    }

    public void checkItemsRankedPlayer(ObjectRepository objectRepository, String sid, User user) {
        // METODO per controllare che le cose che hanno i player sono nel db, prima che le hanno trovate
        // a lontano e non le ho mai incontrate
        int[] itemsToCheck = {user.weapon, user.armor, user.amulet};
        new Thread(()->{
            List<VirtualObj> vobjlist = objectRepository.objectDao().getAll();

            for (int itemID : itemsToCheck){
                if(itemID != 0) {
                    boolean flag = false;
                    for (VirtualObj vobj : vobjlist)
                        if (vobj.id == itemID) {
                            Log.d(TAG, "- " + user.name + ": abbiamo l'oggetto");
                            flag = true;
                            break;
                        }
                    if (!flag) {
                        Log.d(TAG, "- " + user.name + "manca item... aggiornamento db - item.id: " + itemID);
                        //--
                        Call<VirtualObj> virtualObjCall = apiInterface.getVirtualObjectByID(itemID, sid);
                        virtualObjCall.enqueue(new Callback<VirtualObj>() {
                            @Override
                            public void onResponse(Call<VirtualObj> call, Response<VirtualObj> response) {
                                if (!response.isSuccessful()) {
                                    Log.e(TAG, "checkItemsRankedPlayer onResponse ERROR: " + response.code());
                                } else {
                                    Log.d(TAG, "checkItemsRankedPlayer onResponse GOOD_ENDING: " + response.code());
                                    VirtualObj virtualObj = response.body();
                                    //objectRepository.objectDao().insertAll(virtualObj);
                                    new Thread(() -> objectRepository.objectDao().insertAll(virtualObj)).start();
                                }
                            }

                            @Override
                            public void onFailure(Call<VirtualObj> call, Throwable t) {
                                Log.e(TAG, "checkItemsRankedPlayer onFailure ERROR: " + t.getMessage());
                            }
                        });
                        //--
                    }
                }
            }// for int[]
        }).start();
    }
}
