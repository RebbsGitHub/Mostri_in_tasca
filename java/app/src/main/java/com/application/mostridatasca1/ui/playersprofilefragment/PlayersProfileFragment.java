package com.application.mostridatasca1.ui.playersprofilefragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.application.mostridatasca1.R;
import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;
import com.application.mostridatasca1.databinding.FragmentPlayersprofileBinding;
import com.application.mostridatasca1.ui.profile.ProfileViewModelFactory;

public class PlayersProfileFragment extends Fragment {

    private FragmentPlayersprofileBinding binding;
    private static String TAG = "MyLog";
    private boolean isUserTriggered = true;
    private User profile = null;
    private NavController navController;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //database Profiles
        UserRepository userRepository = Room.databaseBuilder(getActivity(), UserRepository.class, "profiles").build();

        // Istanza del Database
        ObjectRepository objectRepository = Room.databaseBuilder(this.getActivity(), ObjectRepository.class, "virtualObj").build();

        PlayersProfileViewModel viewModel = new ViewModelProvider(this, new PlayersProfileModelFactory(userRepository)).get(PlayersProfileViewModel.class);


        //binding cose
        binding = FragmentPlayersprofileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // chiedo gentilmente il NavController
        NavController nvc = Navigation.findNavController(container);
        navController = nvc;

        // chiedo le cose al Bundle
        // richiesta Item -> aggiornamento UI
        Bundle bundle = getArguments();
        viewModel.getProfile(bundle.getInt("profileUID")).observe(getViewLifecycleOwner(), myprofile -> {
            profile = myprofile;
            int paddingDpNoItem = 11;
            int paddingPxNoItem = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDpNoItem, getResources().getDisplayMetrics());
            int paddingDpItem = 10;
            int paddingPxItem = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDpItem, getResources().getDisplayMetrics());
            int lightBlueForItems = ContextCompat.getColor(getContext(), R.color.lightBlueForItems);

            binding.textViewUserNameSPEC.setText(myprofile.name);
            binding.textViewPlayerHPSPEC.setText(String.valueOf(myprofile.life));
            binding.textViewPlayerEXPSPEC.setText(String.valueOf(myprofile.experience));
            binding.textViewPlayerEXPSPEC.setText(String.valueOf(myprofile.experience));
            binding.textViewPlayerHPSPEC.setText(String.valueOf(myprofile.life));
            isUserTriggered = false;
            binding.switchPositionShareSPEC.setChecked(myprofile.positionshare);
            isUserTriggered = true;
            if(myprofile.picture != null)
                binding.imageViewPlayerImageSPEC.setImageBitmap(viewModel.decodeTobase64(myprofile.picture));
            else
                binding.imageViewPlayerImageSPEC.setImageResource(R.drawable.profilepic_noimage);


            if(myprofile.weapon != 0) {
                binding.imageViewWeaponSPEC.setBackgroundColor(lightBlueForItems);
                binding.imageViewWeaponSPEC.setPadding(paddingPxItem, paddingPxItem, paddingPxItem, paddingPxItem);
                viewModel.ObjectImage(objectRepository, profile.weapon).observe(getViewLifecycleOwner(), s -> {
                    if(s == null)
                        binding.imageViewWeaponSPEC.setImageResource(R.drawable.sword_icon_noimage);
                    else
                        binding.imageViewWeaponSPEC.setImageBitmap(viewModel.decodeTobase64(s));
                });
            }else {
                binding.imageViewWeaponSPEC.setPadding(paddingPxNoItem, paddingPxNoItem, paddingPxNoItem, paddingPxNoItem);
                binding.imageViewWeaponSPEC.setImageResource(R.drawable.sword_icon);
            }
            if(myprofile.armor != 0){
                binding.imageViewArmourSPEC.setBackgroundColor(lightBlueForItems);
                binding.imageViewArmourSPEC.setPadding(paddingPxItem, paddingPxItem, paddingPxItem, paddingPxItem);
                viewModel.ObjectImage(objectRepository, profile.armor).observe(getViewLifecycleOwner(), s -> {
                    if(s == null)
                        binding.imageViewArmourSPEC.setImageResource(R.drawable.armor_icon_noimage);
                    else
                        binding.imageViewArmourSPEC.setImageBitmap(viewModel.decodeTobase64(s));
                });
            }else {
                binding.imageViewArmourSPEC.setPadding(paddingPxNoItem, paddingPxNoItem, paddingPxNoItem, paddingPxNoItem);
                binding.imageViewArmourSPEC.setImageResource(R.drawable.armor_icon);
            }
            if(myprofile.amulet != 0){
                binding.imageViewAmuletSPEC.setBackgroundColor(lightBlueForItems);
                binding.imageViewAmuletSPEC.setPadding(paddingPxItem, paddingPxItem, paddingPxItem, paddingPxItem);
                viewModel.ObjectImage(objectRepository, profile.amulet).observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if(s == null)
                            binding.imageViewAmuletSPEC.setImageResource(R.drawable.amulet_icon_noimage);
                        else
                            binding.imageViewAmuletSPEC.setImageBitmap(viewModel.decodeTobase64(s));
                    }
                });
            }else {
                binding.imageViewAmuletSPEC.setPadding(paddingPxNoItem, paddingPxNoItem, paddingPxNoItem, paddingPxNoItem);
                binding.imageViewAmuletSPEC.setImageResource(R.drawable.amulet_icon);
            }
        });


        // ClickListener Weapon
        binding.imageViewWeaponSPEC.setOnClickListener(v -> {
        if(profile.weapon == 0)
            Log.d(TAG, "onCreateView: nessuna arma!");
        else {
            Log.d(TAG, "onCreateView: Weapon clicked");
            navigateToShowItem(nvc, profile.weapon);
        }
        });
        // ClickListener Armour
        binding.imageViewArmourSPEC.setOnClickListener(v -> {
        if(profile.armor == 0)
            Log.d(TAG, "onCreateView: nessuna armatura!");
        else {
            Log.d(TAG, "onCreateView: Armour clicked");
            navigateToShowItem(nvc, profile.armor);
        }
        });
        // ClickListener Amulet
        binding.imageViewAmuletSPEC.setOnClickListener(v -> {
        if(profile.amulet == 0)
            Log.d(TAG, "onCreateView: nessun amuleto!");
        else {
            Log.d(TAG, "onCreateView: Amulet clicked");
            navigateToShowItem(nvc, profile.amulet);
        }
        });
        // ClickListener Exit
        Button closeButton = binding.buttonExitSPEC;
        closeButton.setOnClickListener(v -> {
            navController = Navigation.findNavController(v);
            navController.popBackStack();
        });


        return root;
        }// onCreateView

    @Override
    public void onDestroyView() {
            super.onDestroyView();
            binding = null;
            }



    private void navigateToShowItem(NavController nvc, int itemID){
            Bundle bundle = new Bundle();
            bundle.putInt("itemID", itemID);
            nvc.navigate(R.id.showItemFragment, bundle);
            }


    private void navigateToRankedList(NavController nvc){
            nvc.navigate(R.id.rankedFragment);
            }



}
