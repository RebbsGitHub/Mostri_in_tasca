package com.application.mostridatasca1.ui.otherfragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.application.mostridatasca1.R;
import com.application.mostridatasca1.databinding.FragmentLoadingBinding;


public class LoadingFragment extends Fragment {

    private FragmentLoadingBinding binding;

    private SharedPreferences sharedpreferences;

    private static final String TAG = "MyLog";

    private SharedPreferences.OnSharedPreferenceChangeListener SPlistener;
    //Listener come variabile di classe per poterlo rimuovere nel metodo onDestroyView


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // NavController
        NavController nvc = Navigation.findNavController(container);

        LoadingViewModel LoadingViewModel = new ViewModelProvider(this.getActivity()).get(LoadingViewModel.class);
        binding = FragmentLoadingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // SharedPref
        sharedpreferences = getActivity().getSharedPreferences("com.application.mostridatasca1", getActivity().MODE_PRIVATE);
        if(sharedpreferences.getString("SID", null) == null || sharedpreferences.getInt("UID", 0) == 0){
            LoadingViewModel.registerNewSID();
/*
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("SID", "0Lv6ABRMzIpdGunUrGtb");
            editor.putInt("UID", 29075);
            editor.apply();
*/
        }else{
            Log.d(TAG, "LoadingFragment: Abbiamo gia UID e SID, possiamo andare avanti!");

            //Navigation.findNavController(root).navigate(R.id.navigation_map);
        }


        //momentaneo//
        final Button btn = binding.buttonEnter;
        btn.setOnClickListener(v -> {
            nvc.navigate(R.id.navigation_map);
        });

        final TextView textViewSID = binding.textViewSID;
        final TextView textViewUID = binding.textViewUID;
        //aggiornamento con Observer per i valori delle SharedPreferences sul frammento
        textViewSID.setText(sharedpreferences.getString("SID", "ERRORE"));
        textViewUID.setText(String.valueOf(sharedpreferences.getInt("UID", 9999999)));
        SPlistener = (prefs, key) -> {
            // Do something when a preference value changes
            if (key.equals("SID")) {
                String newSID = prefs.getString("SID", null);
                textViewSID.setText(sharedpreferences.getString("SID", "ERRORE"));
                Log.d(TAG, "SID changed to: " + newSID);
            } else if (key.equals("UID")) {
                int newUID = prefs.getInt("UID", 0);
                Log.d(TAG, "UID changed to: " + newUID);
                textViewUID.setText(String.valueOf(sharedpreferences.getInt("UID", 9999999)));
            }
        };
        sharedpreferences.registerOnSharedPreferenceChangeListener(SPlistener);
         //---






        return root;
    }

    @Override
    public void onDestroyView() {

        sharedpreferences.unregisterOnSharedPreferenceChangeListener(SPlistener);
        super.onDestroyView();
        binding = null;

    }





}