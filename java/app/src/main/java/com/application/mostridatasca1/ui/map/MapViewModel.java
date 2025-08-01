package com.application.mostridatasca1.ui.map;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.os.Build;
import android.util.Log;


import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.application.mostridatasca1.R;
import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.database.simplevirtualobjdb.SimpleVirtualObj;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;
import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;

import com.application.mostridatasca1.networkcalls.ApiInterface;
import com.application.mostridatasca1.networkcalls.RetrofitProvider;
import com.application.mostridatasca1.ui.rankedlist.RankedViewModel;
import com.application.mostridatasca1.ui.rankedlist.SimpleUser;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<VirtualObj> item;
    private MutableLiveData<List<SimpleUser>> playerMarker = new MutableLiveData<>();
    private final static String TAG = "MyLog";
    private ApiInterface apiInterface = RetrofitProvider.getApiInterface();
    private MutableLiveData<User> playerMarkerClicked = new MutableLiveData<>();

    public MapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Mappa del Mondo");
        item = new MutableLiveData<>();
    }
    public LiveData<String> getText() {
        return mText;
    }
    public List<MarkerOptions> setUpMarkerList(List<SimpleVirtualObj> listaitems, Context context){
        List<MarkerOptions> listaMarker = new ArrayList<>();

        if(listaitems.isEmpty()) {
            Log.e(TAG, "nessun oggetto nei pagaggi!");
            return listaMarker;
        }
        else
            for (SimpleVirtualObj obj: listaitems) {

                BitmapDescriptor icon = null;
                switch(obj.type){
                    case "weapon":
                         icon = BitmapDescriptorFactory.fromBitmap(getBitmap(context, R.drawable.weapon_map_icon));
                        break;
                    case "armor":
                         icon = BitmapDescriptorFactory.fromBitmap( getBitmap(context, R.drawable.armor_map_icon));
                        break;
                    case "amulet":
                         icon = BitmapDescriptorFactory.fromBitmap(getBitmap(context, R.drawable.amulet_map_icon));
                        break;
                    case "monster":
                         icon = BitmapDescriptorFactory.fromBitmap(getBitmap(context, R.drawable.monster_map_icon));
                        break;
                    case "candy":
                         icon = BitmapDescriptorFactory.fromBitmap(getBitmap(context, R.drawable.candy_map_icon));
                        break;
                    default:
                        icon = BitmapDescriptorFactory.fromBitmap(getBitmap(context, R.drawable.def_map_icon));
                        break;
                }
                LatLng position = new LatLng(obj.lat, obj.lon);
                MarkerOptions markerOption = new MarkerOptions()
                        .position(position)
                        .title(obj.type)
                        .icon(icon)
                        .snippet(String.valueOf(obj.id));

                listaMarker.add(markerOption);
            }
        return listaMarker;
    }



    public void askPlayerMarkerList(String SID,double lat,double lon) {

        Call<List<SimpleUser>> updateUserCall = apiInterface.getPlayersOnMap(SID, lat, lon);
        updateUserCall.enqueue(new Callback<List<SimpleUser>>() {
            @Override
            public void onResponse(Call<List<SimpleUser>> call, Response<List<SimpleUser>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "setUpPlayerMarkerList onResponse ERROR: " + response.code());
                }else{
                    List<SimpleUser> listaPlayer = response.body();
                    playerMarker.postValue(listaPlayer);
                    //for (SimpleUser player: listaPlayer) Log.d(TAG, "PLAYER uid: "+player.uid+"  profileversion:"+player.profileversion);
                }}
            @Override
            public void onFailure(Call<List<SimpleUser>> call, Throwable t) {
                Log.e(TAG, "setUpPlayerMarkerList onFailure ERROR: " + t.getMessage());
            }
        });
    }

    public MutableLiveData<List<SimpleUser>>  getPlayerMarkerList(){
        return playerMarker;
    }




    // SVG to Bitmap
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
    public static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    public boolean troppoLontano(double startLat, double startLong, double endLat, double endLong, int range) {

            final double EARTH_RADIUS = 6371.0;
            double dLat = Math.toRadians((endLat - startLat));
            double dLong = Math.toRadians((endLong - startLong));

            startLat = Math.toRadians(startLat);
            endLat = Math.toRadians(endLat);

            double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distanceKm = EARTH_RADIUS * c;
            int distanceM = (int) (distanceKm * 1000);
            if (distanceM<= range+50)
                return false;
            else
                return true;
    }
    double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public void troppoLontanoWindow(Context context) {
        new AlertDialog.Builder(context)
                .setMessage("Sei troppo lontano!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


    public interface DatabaseCallback {
        void onCallback(VirtualObj item);
    }
    public void getItemData(String snippet, ObjectRepository objectRepository, DatabaseCallback callback) {
        int id = Integer.parseInt(snippet);
        new Thread(() -> {
            VirtualObj item = objectRepository.objectDao().getObjectByID(id);
            callback.onCallback(item);
        }).start();


    }

    public MutableLiveData<Integer> amuletLevel(int UID, UserRepository userRepository, ObjectRepository objectRepository){
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



    public void checkPlayerData(Context context, Marker marker, UserRepository userRepository,ObjectRepository objectRepository, String SID ) {
        String[] snippetData = marker.getSnippet().split(" ");
        int uid = Integer.parseInt(snippetData[0]);
        int profileversion = Integer.parseInt(snippetData[1]);

        new Thread(() -> {
             User playerDB = userRepository.userDao().getProfileByUID(uid);

             if(playerDB == null){
                 Log.e(TAG, "checkPlayerData-MapFragment: NON ESISTE NEL DB");
                 richiestaServerAggiornamentoDB(uid,SID, userRepository, objectRepository);

             }else if(playerDB.profileversion != profileversion){
                 Log.e(TAG, "checkPlayerData-MapFragment: DATI VECCHI - AGGIORNAMENTO DB");
                 updateDBprofile(uid, SID, userRepository, objectRepository);

             }else{
                 Log.e(TAG, "checkPlayerData-MapFragment: TUTTO BENE -> player name"+playerDB.name+"  profileversion: "+playerDB.profileversion);
                 playerMarkerClicked.postValue(playerDB);
             }
        }).start();
    }

    private void richiestaServerAggiornamentoDB(int uid, String SID, UserRepository userRepository,ObjectRepository objectRepository) {
        Call<User> updateUserCall = apiInterface.getUserByID(uid, SID);
        updateUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "richiestaServerAggiornamentoDB-checkPlayerData-MapFragment onResponse ERROR: " + response.code());
                }else{
                    Log.d(TAG, "richiestaServerAggiornamentoDB-checkPlayerData-MapFragment onResponse GOOD_ENDING: "+response.code());
                    User u = response.body();
                    new Thread (() -> {
                        checkItemsPlayers(objectRepository, SID, u);
                        userRepository.userDao().insertAll(u);

                        playerMarkerClicked.postValue(u);
                    }).start();

                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "richiestaServerAggiornamentoDB-checkPlayerData-MapFragment onFailure ERROR: " + t.getMessage());
            }
        });

    }
    private void updateDBprofile(int uid,String SID,UserRepository userRepository, ObjectRepository objectRepository){
        Call<User> updateUserCall = apiInterface.getUserByID(uid, SID);
        updateUserCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "updateDBprofile-checkPlayerData-MapFragment onResponse ERROR: " + response.code());
                }else{
                    Log.d(TAG, "updateDBprofile-checkPlayerData-MapFragment onResponse GOOD_ENDING: "+response.code());
                    User u = response.body();
                    new Thread (() -> {
                        if(u.uid == uid) {
                            checkItemsPlayers(objectRepository, SID, u);
                            userRepository.userDao().deleteUserByUID(uid);
                            userRepository.userDao().insertAll(u);

                            playerMarkerClicked.postValue(u);

                        }else
                            Log.e(TAG, "updateDBprofile-checkPlayerData-MapFragment: ERRORE CORRISPONDENZA: u.uid == uid");
                    }).start();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "setUpRankedList onFailure ERROR: " + t.getMessage());
            }
        });

    }

    public void checkItemsPlayers(ObjectRepository objectRepository, String sid, User user) {
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
                        Call<VirtualObj> virtualObjCall = apiInterface.getVirtualObjectByID(itemID, sid);
                        virtualObjCall.enqueue(new Callback<VirtualObj>() {
                            @Override
                            public void onResponse(Call<VirtualObj> call, Response<VirtualObj> response) {
                                if (!response.isSuccessful()) {
                                    Log.e(TAG, "checkItemsPlayers onResponse ERROR: " + response.code());
                                } else {
                                    Log.d(TAG, "checkItemsPlayers onResponse GOOD_ENDING: " + response.code());
                                    VirtualObj virtualObj = response.body();
                                    new Thread(() -> objectRepository.objectDao().insertAll(virtualObj)).start();
                                }
                            }
                            @Override
                            public void onFailure(Call<VirtualObj> call, Throwable t) {
                                Log.e(TAG, "checkItemsPlayers onFailure ERROR: " + t.getMessage());
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public MutableLiveData<User> getPlayerMarkerClicked(){
        return playerMarkerClicked;
    }
    public void resetPlayerMarkerClicked(){
        playerMarkerClicked.setValue(null);
    }

}

