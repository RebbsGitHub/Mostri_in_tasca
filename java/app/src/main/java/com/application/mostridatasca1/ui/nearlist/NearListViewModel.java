package com.application.mostridatasca1.ui.nearlist;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.fragment.DialogFragmentNavigatorDestinationBuilder;

import com.application.mostridatasca1.R;
import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.database.simplevirtualobjdb.SimpleVirtualObj;
import com.application.mostridatasca1.database.simplevirtualobjdb.SimpleVirtualObjRepository;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;
import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;
import com.application.mostridatasca1.networkcalls.ApiInterface;
import com.application.mostridatasca1.networkcalls.RetrofitProvider;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearListViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private static final String TAG = "MyLog";
    // API
    private ApiInterface apiInterface = RetrofitProvider.getApiInterface();

    private List<VirtualObj> oggettiRichiestiClass = new ArrayList<>();
    // lista che si riempie con gli oggetti vicini completi poco a poco

    MutableLiveData<List<VirtualObj>> listaOggettiCompletaViewModel;
    // Lista degli oggetti Completi da mandare alla Recylce dal fragment - DIMANICA


    public NearListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Oggetti Vicini a te!");
        listaOggettiCompletaViewModel = new MutableLiveData<>();
        // istanziamo quando si crea il viewmodel
    }

    public static int getAccountUID(Application application){
        SharedPreferences sharedPreferences = application.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("UID", 0);
    }
    public static String getAccountSID(Application application) {
        SharedPreferences sharedPreferences = application.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE);
        return sharedPreferences.getString("SID", null);
    }
    public static MutableLiveData<Integer> amuletLevel(int UID, UserRepository userRepository, ObjectRepository objectRepository){
        MutableLiveData<Integer> amulet = new MutableLiveData<>();
        amulet.setValue(0);
        new Thread(() -> {
            User me = userRepository.userDao().getProfileByUID(UID);
            try{
                if(me.amulet != 0)
                    amulet.postValue(objectRepository.objectDao().getObjectByID((me.amulet)).level);
            }catch (Exception e){
                Log.d(TAG, "amuletLevel: Nessun amuleto equipaggiato");
            }
        }).start();
        return amulet;
    }





    public void setUpCloseList(Double playerLAT, Double playerLON, SimpleVirtualObjRepository simpleVirtualObjRepository, int range, ObjectRepository objectRepository, String SID) {
        oggettiRichiestiClass.clear();
        // iniziamo il thread secondario
        new Thread(() -> {
            List<SimpleVirtualObj> listaSimpleRichiestiDB = new ArrayList<>();


            // richiesta Database simpleObj - OK
            List<SimpleVirtualObj> simpleOggettiDB = simpleVirtualObjRepository.simpleVirtualObjDao().getAll();
            Log.d(TAG, "setUpCloseList: Oggetti attorno a me Completi   : "+simpleOggettiDB.size() );

            for (SimpleVirtualObj item: simpleOggettiDB )
                if (calculateDistance(playerLAT, playerLON, item.lat, item.lon)<= range)
                    listaSimpleRichiestiDB.add(item);


            Log.d(TAG, "setUpCloseList: Oggetti attorno a me  range : "+listaSimpleRichiestiDB.size() );

            // richiesta Database VirtualObj - OK
            List<VirtualObj> listaoggettiGrossi = objectRepository.objectDao().getAll();
            Log.d(TAG, "setUpCloseList: Oggetti nel Database TOTALMENTE : "+listaoggettiGrossi.size() );

            // thread che aggiorna il DB dei VirtualObj con SOLO I VICINI  a (range)
            new Thread(()->{
                for (SimpleVirtualObj item : listaSimpleRichiestiDB) {
                    boolean flag = false;
                    for (VirtualObj BigItem: listaoggettiGrossi)
                         if (item.id == BigItem.id) {
                             // ESISTE NEL DB: salvataggio nella lista
                             oggettiRichiestiClass.add(BigItem);
                             flag = true;
                             break;
                         }
                    if (!flag) {
                        // INESISTENTE NEL DB - aggiorno il db e salvataggio nella lista
                        richiestaServerAggiornamentoDB(item.id,SID, objectRepository);
                    }
                }
                // check oggetti oggettiRichiesti
                //for (VirtualObj FINALITEM: oggettiRichiesti) {
                //    Log.d(TAG, "!! ITEM GROSSO "+FINALITEM.id+" "+FINALITEM.name);
                //}

                // check oggetti
                //List<VirtualObj> FINETEST = objectRepository.objectDao().getAll();
                //for (VirtualObj FINALITEM: FINETEST) {
                //    Log.d(TAG, "!! ITEM GROSSO "+FINALITEM.id+" "+FINALITEM.name);
                //}

            }).start();

            // aggiornamento listaOggettiCompleti MutableLiveData

            listaOggettiCompletaViewModel.postValue(new ArrayList<>());
            listaOggettiCompletaViewModel.postValue(oggettiRichiestiClass);

        }).start();



    }// setUpCloseList

    private void richiestaServerAggiornamentoDB(int id, String SID,ObjectRepository objectRepository) {

        Call<VirtualObj> updateUserCall = apiInterface.getVirtualObjectByID(id, SID);
        updateUserCall.enqueue(new Callback<VirtualObj>() {
            @Override
            public void onResponse(Call<VirtualObj> call, Response<VirtualObj> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "setUpCloseList onResponse ERROR: " + response.code());
                }else{
                    Log.d(TAG, "setUpCloseList onResponse GOOD_ENDING: "+response.code());
                    VirtualObj virtualObj = response.body();
                    new Thread (() -> objectRepository.objectDao().insertAll(virtualObj)).start();

                    // aggiunta alla Mutable del Viewmodel
                    oggettiRichiestiClass.add(virtualObj);
                }
            }
            @Override
            public void onFailure(Call<VirtualObj> call, Throwable t) {
                Log.e(TAG, "setUpCloseList onFailure ERROR: " + t.getMessage());
            }
        });

    }


    // Calcolo spazio tra coordinate
    int calculateDistance(double startLat, double startLong, double endLat, double endLong) {
        final double EARTH_RADIUS = 6371.0;
        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = EARTH_RADIUS * c;
        int distanceM = (int) (distanceKm * 1000);

        return distanceM;
    }
    double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }


    // Funzioni gestione richieste RecylcleView per popolare la lista tramite il Mutable<List<VirtualObj>>
    public MutableLiveData<List<VirtualObj>> getOggettiCompletiVM() {
        return listaOggettiCompletaViewModel;
    }
    public int getOggettiComppletiNumber(){
        return oggettiRichiestiClass.size();
    }
    public VirtualObj getOggettoCompleto(int position) {
        return oggettiRichiestiClass.get(position);
    }
    // Fine funzioni gestione RecylcleView


    public LiveData<String> getText() {
        return mText;
    }


    public void navigateToInteractionFragment(NavController nvc, VirtualObj item){
        Bundle bundle = new Bundle();

        bundle.putInt("itemID",item.id);
        bundle.putString("itemName", item.name);
        bundle.putString("itemType", item.type);
        bundle.putInt("itemLevel", item.level);
        bundle.putString("itemImage", item.image);
        nvc.navigate(R.id.interactionFragment, bundle);
    }
}