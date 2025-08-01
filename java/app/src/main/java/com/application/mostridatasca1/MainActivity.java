package com.application.mostridatasca1;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.application.mostridatasca1.database.simplevirtualobjdb.SimpleVirtualObjRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.application.mostridatasca1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyLog";
    private ActivityMainBinding binding;


    // hander per il refresh della lista delle cose attorno a me
    private Handler handler = new Handler();
    private Runnable runnable;
    final int delay = 5000; // 5 secondi per ora, vedere come vanno le cose

    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // chiediamo il SID
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE);
        final String SID = sharedPreferences.getString("SID", null);

        //MainActivityViewModel call
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        //recupero vecchie coords
        Double oldLat = Double.parseDouble(sharedPreferences.getString("oldLat", "0"));
        Double oldLon = Double.parseDouble(sharedPreferences.getString("oldLon", "0"));
        //Log.e(TAG, "doubles: "+Double.parseDouble(sharedPreferences.getString("oldLat", "0"))+"  "+Double.parseDouble(sharedPreferences.getString("oldLon", "0")) );
        if (oldLon != 0.0 || oldLat != 0.0){
            viewModel.setPlayerLAT(oldLat);
            viewModel.setPlayerLON(oldLon);
            //Log.e(TAG, "onCreate: LETTURA VECCHIE CORDS LAT:"+oldLat+" LON:"+oldLon);
        }

        // Request Location Updates dal ViewModel - LatLon
        viewModel.requestLocationUpdates(this);


        // crea il database da passare al ViewModel di simpleVirtualObj
        SimpleVirtualObjRepository simpleVirtualObjRepository = Room.databaseBuilder(this, SimpleVirtualObjRepository.class, "simpleVirtualObj").build();


        // il primo subito al volo

        viewModel.updateObjectList(simpleVirtualObjRepository,SID);

        //periodicamente aggiorna la lista delle cose attorno a me
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                viewModel.updateObjectList(simpleVirtualObjRepository,SID);
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(runnable, delay);


        // BottomNavigationView controller
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_profile, R.id.navigation_map, R.id.navigation_nearlist).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        // Hide action Bar in all application
        getSupportActionBar().hide();


        // Hide BottomNavigationView in LOADING fragment and CHAMPIONLIST fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // If the current destination is one of the fragments where you want to show the menu,
            // set the visibility of the BottomNavigationView to VISIBLE; otherwise, set it to GONE.
            if (destination.getId() == R.id.navigation_profile || destination.getId() == R.id.navigation_map || destination.getId() == R.id.navigation_nearlist) {
                navView.setVisibility(View.VISIBLE);
            } else {
                navView.setVisibility(View.GONE);
            }
        });




    }// onCreate

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);

        //salva ultime coords
        SharedPreferences.Editor editor = this.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE).edit();
        try {
            editor.putString("oldLat", viewModel.getPlayerLAT().getValue().toString());
            editor.putString("oldLon", viewModel.getPlayerLON().getValue().toString());
            editor.apply();
        }catch (Exception e){
            Log.d(TAG, "Missing oldLat, oldLon - primo avvio" );
        }



    }
    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);

        //salva ultime coords
        SharedPreferences.Editor editor = this.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE).edit();
        try{
            editor.putString("oldLat", viewModel.getPlayerLAT().getValue().toString());
            editor.putString("oldLon", viewModel.getPlayerLON().getValue().toString());
            editor.apply();
        }catch (Exception e){
            Log.d(TAG, "Missing oldLat, oldLon - primo avvio" );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);

        //salva ultime coords
        SharedPreferences.Editor editor = this.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE).edit();

        try{
            editor.putString("oldLat", viewModel.getPlayerLAT().getValue().toString());
            editor.putString("oldLon", viewModel.getPlayerLON().getValue().toString());
            editor.apply();
        }catch (Exception e){
            Log.d(TAG, "Missing oldLat, oldLon - primo avvio" );
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        handler.postDelayed(runnable, delay);
    }


} // MainActivity
