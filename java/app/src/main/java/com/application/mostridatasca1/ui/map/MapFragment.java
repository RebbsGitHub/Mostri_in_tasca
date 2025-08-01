package com.application.mostridatasca1.ui.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.application.mostridatasca1.MainActivityViewModel;
import com.application.mostridatasca1.R;
import com.application.mostridatasca1.Utils;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;

import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;
import com.application.mostridatasca1.databinding.FragmentMapBinding;
import com.application.mostridatasca1.ui.nearlist.NearListViewModel;
import com.application.mostridatasca1.ui.rankedlist.SimpleUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private static final String TAG = "MyLog";
    private MainActivityViewModel mainViewModel;
    private MapViewModel mapViewModel;
    private GoogleMap googleMap;
    private boolean startfflag = true;
    private boolean lockCam = true;
    private ObjectRepository objectRepository;
    private UserRepository userRepository;
    private NavController nvc;
    private MutableLiveData<Bundle> bundleInteraction = new MutableLiveData<>();
    private Integer playerRange = 100;
    private MutableLiveData<LatLng> playerLocationOnCreate = new MutableLiveData<>();
    private MutableLiveData<List<MarkerOptions>> playersMarkerList = new MutableLiveData<>();
    private MutableLiveData<List<MarkerOptions>> itemsMarkerList = new MutableLiveData<>();
    private int UID;
    private String SID;
    private Marker userMarker;
    private LatLng defaultPlayerLocation;




    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Database VirtualObj
        objectRepository = Room.databaseBuilder(this.getActivity(), ObjectRepository.class, "virtualObj").build();
        // Database UserRepository
        userRepository = Room.databaseBuilder(getActivity(), UserRepository.class, "profiles").build();

        // chiedo il NavController
        nvc = Navigation.findNavController(container);

        // Binding
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // viewModel
        mapViewModel = new ViewModelProvider(this.getActivity()).get(MapViewModel.class);

        // SID - UID
        UID = Utils.getAccountUID(getActivity().getApplication());
        SID = Utils.getAccountSID(getActivity().getApplication());

        //MainActivityViewModel call - dati posizione
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class); //requireActivity() ritorna l'Activity a cui il fragment è associato, con THIS si crea credo un'altra activity e di conseguenza un nuovo MainViewModel

        // Richieta amuleto per calcolo del range
        mapViewModel.amuletLevel(UID,userRepository, objectRepository).observe(getViewLifecycleOwner(), amuletLevel -> {
            playerRange = 100+amuletLevel;
            //Log.d("MyLog", "NearListFragment: range finale: "+playerRange);
        });

        //Setup defalutPlayerLocation
        if(mainViewModel.getPlayerLAT().getValue() == null || mainViewModel.getPlayerLON().getValue() == null){
            Log.d(TAG, "onCreateView: nessun dato precedente Lat Lon disponibile");
            defaultPlayerLocation = new LatLng(0.0f,0.0f);
        }else{
            defaultPlayerLocation = new LatLng(mainViewModel.getPlayerLAT().getValue(), mainViewModel.getPlayerLON().getValue());
        }



        // Setup dati per userMarker
        BitmapDescriptor userIcon = BitmapDescriptorFactory.fromBitmap(mapViewModel.getBitmap(getContext(),R.drawable.player_map_icon));


        // Setup mapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(com.application.mostridatasca1.R.id.map);

        // Chiamata asincrona per la mappa - preload stato iniziale
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // set della vecchia location salvata
                //LatLng defaultLocation = new LatLng(mainViewModel.getPlayerLAT().getValue(), mainViewModel.getPlayerLON().getValue());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(defaultPlayerLocation)    //
                        .zoom(19f)                  // Set valori al primo caricamento
                        .tilt(40f)                  //
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.setMinZoomPreference(4.0f);
                googleMap.setMaxZoomPreference(19.5f);
                userMarker = googleMap.addMarker(new MarkerOptions()
                        .position(defaultPlayerLocation)
                        .title("TU!")
                        .zIndex(9999999)
                        .icon(userIcon));
            }
        });

        // Observer per la posizione del player

        mainViewModel.getPlayerLocation().observe(getViewLifecycleOwner(), locationData -> {             //getViewLifecycleOwner() ottiene il ciclo di vita associato alla View del frammento
            mapFragment.getMapAsync(this);

            if (playerLocationOnCreate.getValue() == null) // una volta sola
                playerLocationOnCreate.postValue(new LatLng(locationData.getLatitude(), locationData.getLongitude()));

            Log.d(TAG, "MapFragment observer - playerLocation(lat: "+locationData.getLatitude()+"  lon: "+locationData.getLongitude()+")");

        });

        // Creare icone players - abbiamo la posizione
        Log.d(TAG, "Map Loading: Caricamento players ");
        playerLocationOnCreate.observe(getViewLifecycleOwner(), location -> {
        //1- Chiedere i player semplici al server
            mapViewModel.askPlayerMarkerList(SID, location.latitude, location.longitude);
        });
        //2- Creare i maker dei player riotornati
        mapViewModel.getPlayerMarkerList().observe(getViewLifecycleOwner(), playerList -> {
            List<MarkerOptions> arrayLocalPML = new ArrayList<>();
            if (playerList != null) {
                for (SimpleUser player: playerList) {
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(mapViewModel.getBitmap(getContext(),R.drawable.other_players_map_icon));
                    LatLng position = new LatLng(player.lat, player.lon);
                    MarkerOptions options = new MarkerOptions()
                            .position(position)
                            .title("Player!")
                            .icon(icon)
                            .snippet((player.uid)+" "+ (player.profileversion));

                    //3- Mettere i marker in un array per poterli vedere

                    arrayLocalPML.add(options);
                }
                playersMarkerList.setValue(arrayLocalPML);
            }
        });

// Creare icone oggetti - abbiamo la posizione 2.0 (ora dovrebbe aspettare l'obs per sicurezza e così caricare le cose copn calma)
        mainViewModel.getItemsAround().observe(getViewLifecycleOwner(), itemsAround -> {
            List<MarkerOptions> iml;
            if (itemsAround == null ||(itemsAround.isEmpty()))
                Log.d(TAG, "mapFragment 186: Nessun oggetto nei paraggi dam getItemsAround() ");
            else {
                try {
                iml = mapViewModel.setUpMarkerList(itemsAround, this.getContext());
                if (iml.isEmpty()) {
                    Log.e(TAG, "onCreateView: Nessun oggetto nei paraggi");
                    itemsMarkerList.setValue(null);
                } else
                    itemsMarkerList.setValue(iml);
                }catch (Exception e){
                    Log.e(TAG, "onCreateView: Errore nel caricamento degli oggetti");
                }
            }

        });
        // Creare icone oggetti - abbiamo la posizione
        /*
        try {
            List<MarkerOptions> iml = mapViewModel.setUpMarkerList(mainViewModel.getItemsAround(), this.getContext());
            if(iml.isEmpty()) {
                Log.e(TAG, "onCreateView: Nessun oggetto nei paraggi");
                itemsMarkerList.setValue(null);
            }
            else
                itemsMarkerList.setValue(iml);
        }catch (Exception e){
            Log.e(TAG, "onCreateView: Errore nel caricamento degli oggetti");
        }
*/









        // observer che inserisce gli oggetti sulla mappa
        Log.d(TAG, "Map Loading: Caricamento Items ");
        itemsMarkerList.observe(getViewLifecycleOwner(), markers -> {
            if (markers != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        // Aggiunta oggetti sulla mappa
                        if (itemsMarkerList.getValue() == null) {
                            Log.e(TAG, "onMapReady-ItemLoad: ERRORE, NON SI VEDONO GLI OGGETTI");
                        }else
                            for (MarkerOptions itemMarker: itemsMarkerList.getValue())
                                googleMap.addMarker(itemMarker);
                    }
                });
            }
        });

        // observer che inserisce i player sulla mappa
        playersMarkerList.observe(getViewLifecycleOwner(), markers -> {
            if (markers != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        // Aggiunta player sulla mappa
                        if (playersMarkerList.getValue() == null) {
                            Log.e(TAG, "onMapReady-PlayerLoad: ERRORE, NON SI VEDONO I PLAYERS");
                        }else
                            for (MarkerOptions playerMarker: playersMarkerList.getValue())
                                googleMap.addMarker(playerMarker);

                    }
                });
            }
        });

        // observer genera la pagina del player
        mapViewModel.getPlayerMarkerClicked().observe(getViewLifecycleOwner(), userData -> {
            if (userData != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("profileUID", userData.uid);
                nvc.navigate(R.id.playersProfileFragment, bundle);
                mapViewModel.resetPlayerMarkerClicked();
            }
        });

        // observer genera la pagina di interazione
        bundleInteraction.observe(getViewLifecycleOwner(), bundle -> {
            if (bundle != null) {
                nvc.navigate(R.id.interactionFragment, bundle);
                bundleInteraction.postValue(null);
            }
        });


        final TextView textView = binding.textViewMapMain;
        mapViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }//onCreateView



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        //googleMap.clear();
        if(startfflag){
            startfflag = false;
        }

        // Aggiornamento LatLng player
        LatLng position = new LatLng(mainViewModel.getPlayerLocation().getValue().getLatitude(), mainViewModel.getPlayerLocation().getValue().getLongitude());
        userMarker.setPosition(position);
        if(lockCam)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        /*





*/

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    // ok Log.d(TAG, "The user is scrolling the map.");
                    lockCam = false;
                }
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                lockCam = true;
                if(marker.getTitle().equals("TU!"))
                    return false;
                else if(marker.getTitle().equals("Player!")){

                    //4- quando lo user preme il marker di un player controllare il database -> se lo abbiamo se no è da chiedere / aggiornare il player
                    Log.e(TAG, "Marker players clicked - UID pf player:"+marker.getSnippet() );
                    mapViewModel.checkPlayerData(getContext(), marker, userRepository, objectRepository, SID);
                    return true;
                }


                if(mapViewModel.troppoLontano(marker.getPosition().latitude, marker.getPosition().longitude, mainViewModel.getPlayerLocation().getValue().getLatitude(), mainViewModel.getPlayerLocation().getValue().getLongitude(), playerRange)){
                    Log.e(TAG, "onMarkerClick: Troppo lontano!");
                    mapViewModel.troppoLontanoWindow(getContext());
                    return false;
                }else{
                    //Log.e(TAG, "Marker: " + marker.getTitle() + " ID: "+marker.getSnippet());
                    mapViewModel.getItemData(marker.getSnippet(), objectRepository, item -> {
                        if (item != null) {
                            Log.d(TAG, "MapFragment observer itemForInteraction - NOME:"+item.name+" TIPO:"+item.type+" LIV:"+item.level);
                            Bundle bundle = new Bundle();
                            bundle.putInt("itemID",item.id);
                            bundle.putString("itemName", item.name);
                            bundle.putString("itemType", item.type);
                            bundle.putInt("itemLevel", item.level);
                            bundle.putString("itemImage", item.image);
                            bundleInteraction.postValue(bundle);
                        }
                    });
                }

                return true;
            }
        });


    }// onMapReady




}