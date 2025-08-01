package com.application.mostridatasca1.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.text.InputFilter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.application.mostridatasca1.R;
import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.database.simplevirtualobjdb.SimpleVirtualObjRepository;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;
import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;
import com.application.mostridatasca1.databinding.FragmentProfileBinding;

import java.io.IOException;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private static String TAG = "MyLog";
    private boolean isUserTriggered = true;

    private User profile = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //database Profiles
        UserRepository userRepository = Room.databaseBuilder(getActivity(), UserRepository.class, "profiles").build();

        // Istanza del Database
        ObjectRepository objectRepository = Room.databaseBuilder(this.getActivity(), ObjectRepository.class, "virtualObj").build();

        // aggiungi alla istanza del VM nel ViewModelProvider il richiamo(?) al database tramite la classe ProfileViewModelFactory
        ProfileViewModel profileViewModel = new ViewModelProvider(this, new ProfileViewModelFactory(userRepository)).get(ProfileViewModel.class);

        //binding cose
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // reqeust SID - UID per cose dopo
        final int UID = profileViewModel.getAccountUID(getActivity().getApplication());
        final String SID = profileViewModel.getAccountSID(getActivity().getApplication());

        // funzione di setup del profilo
        profileViewModel.checkDatabase(getActivity().getApplication(), UID, SID);

        // chiedo gentilmente il NavController
        NavController nvc = Navigation.findNavController(container);


        // carica il profilo utente dal database
        profileViewModel.getProfile(UID).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User myprofile) {
                profile = myprofile;
                //Log.d(TAG, "outputDatabase: user[ " + myprofile.uid+ " " + myprofile.name + " " + myprofile.life + " " + myprofile.experience + " " + myprofile.weapon + " " + myprofile.armor + " " + myprofile.amulet + " " + myprofile.picture + " " + myprofile.profileversion + " " + myprofile.positionshare+ " ]");
                int paddingDpNoItem = 11;
                int paddingPxNoItem = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDpNoItem, getResources().getDisplayMetrics());
                int paddingDpItem = 10;
                int paddingPxItem = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDpItem, getResources().getDisplayMetrics());
                int lightBlueForItems = ContextCompat.getColor(getContext(), R.color.lightBlueForItems);

                binding.textViewUserName.setText(myprofile.name);
                binding.textViewPlayerHP.setText(String.valueOf(myprofile.life));
                binding.textViewPlayerEXP.setText(String.valueOf(myprofile.experience));
                binding.textViewPlayerEXP.setText(String.valueOf(myprofile.experience));
                binding.textViewPlayerHP.setText(String.valueOf(myprofile.life));
                isUserTriggered = false;
                binding.switchPositionShare.setChecked(myprofile.positionshare);
                isUserTriggered = true;
                if(myprofile.picture != null)
                    binding.imageViewPlayerImage.setImageBitmap(profileViewModel.decodeTobase64(myprofile.picture));

                /* Thread(()->{
                    String weaponImage = objectRepository.objectDao().getObjectByID(profile.weapon).image;
                    String armorImage  = objectRepository.objectDao().getObjectByID(profile.armor).image;
                    String amuletImage = objectRepository.objectDao().getObjectByID(profile.amulet).image;
                }).start();*/


                if(myprofile.weapon != 0) {
                    binding.imageViewWeapon.setBackgroundColor(lightBlueForItems);
                    binding.imageViewWeapon.setPadding(paddingPxItem, paddingPxItem, paddingPxItem, paddingPxItem);
                    profileViewModel.ObjectImage(objectRepository, profile.weapon).observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {

                            if(s == null)
                                binding.imageViewWeapon.setImageResource(R.drawable.sword_icon_noimage);
                            else
                                binding.imageViewWeapon.setImageBitmap(profileViewModel.decodeTobase64(s));
                        }
                    });
                }else {
                    binding.imageViewWeapon.setPadding(paddingPxNoItem, paddingPxNoItem, paddingPxNoItem, paddingPxNoItem);
                    binding.imageViewWeapon.setImageResource(R.drawable.sword_icon);
                }
                if(myprofile.armor != 0){
                    binding.imageViewArmour.setBackgroundColor(lightBlueForItems);
                    binding.imageViewArmour.setPadding(paddingPxItem, paddingPxItem, paddingPxItem, paddingPxItem);
                    profileViewModel.ObjectImage(objectRepository, profile.armor).observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {

                            if(s == null)
                                binding.imageViewArmour.setImageResource(R.drawable.armor_icon_noimage);
                            else
                                binding.imageViewArmour.setImageBitmap(profileViewModel.decodeTobase64(s));
                        }
                    });
                }else {
                    binding.imageViewArmour.setPadding(paddingPxNoItem, paddingPxNoItem, paddingPxNoItem, paddingPxNoItem);
                    binding.imageViewArmour.setImageResource(R.drawable.armor_icon);
                }
                if(myprofile.amulet != 0){
                    binding.imageViewAmulet.setBackgroundColor(lightBlueForItems);
                    binding.imageViewAmulet.setPadding(paddingPxItem, paddingPxItem, paddingPxItem, paddingPxItem);
                    profileViewModel.ObjectImage(objectRepository, profile.amulet).observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {

                            if(s == null)
                                binding.imageViewAmulet.setImageResource(R.drawable.amulet_icon_noimage);
                            else
                                binding.imageViewAmulet.setImageBitmap(profileViewModel.decodeTobase64(s));
                        }
                    });
                }else {
                    binding.imageViewAmulet.setPadding(paddingPxNoItem, paddingPxNoItem, paddingPxNoItem, paddingPxNoItem);
                    binding.imageViewAmulet.setImageResource(R.drawable.amulet_icon);
                }
            }
        });

        // Modifica Nome!
        binding.imageViewEditName.setOnClickListener(v -> {
              windowChangeProfileName(profileViewModel, SID, UID);
        });


        // Modifica Immagine!!!     (istanzi pickMedia)
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback invocata quando utente seleziona la foto o chiude il photo picker.
                    if (uri != null) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(profileViewModel.isBitmapBiggerThan10KB(bitmap)){
                            Log.e(TAG, "PickVisualMediaRequest: Immagine troppo grossa, più di 100 KB" );
                        }else{
                            String imageStringBase64 = profileViewModel.encodeTobase64(bitmap);
                            profileViewModel.updateProfileImgVM(SID,UID,imageStringBase64);
                            binding.imageViewPlayerImage.setImageBitmap(bitmap);
                        }
                    } else {
                        Log.d(TAG, "No media selected");
                    }
                });
        binding.imageViewEditProfileImg.setOnClickListener(v -> {
            showImageInputDialog(pickMedia);
        });

        // Modifica PositionShare
        binding.switchPositionShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isUserTriggered)
                profileViewModel.checkAndChangePostionShareVM(SID, UID, isChecked);

        }
    });


        // ClickListener Weapon
        binding.imageViewWeapon.setOnClickListener(v -> {
            if(profile.weapon == 0)
                showAlert(1, this.getView());
            else {
                Log.d(TAG, "onCreateView: Weapon clicked");
                navigateToShowItem(nvc, profile.weapon);
            }
        });
        // ClickListener Armour
        binding.imageViewArmour.setOnClickListener(v -> {
            if(profile.armor == 0)
                showAlert(2, this.getView());
            else {
                Log.d(TAG, "onCreateView: Armour clicked");
                navigateToShowItem(nvc, profile.armor);
            }
        });
        // ClickListener Amulet
        binding.imageViewAmulet.setOnClickListener(v -> {
            if(profile.amulet == 0)
                showAlert(3, this.getView());
            else {
                Log.d(TAG, "onCreateView: Amulet clicked");
                navigateToShowItem(nvc, profile.amulet);
            }
        });

        // ClickListener Ranked
        binding.buttonRankedList.setOnClickListener(v -> {
            navigateToRankedList(nvc);
        });

        return root;
    }// onCreateView


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void windowChangeProfileName(ProfileViewModel profileViewModel, String SID, int UID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cambia Nickname");
        builder.setMessage("Inserisci il nuovo nickname");

        EditText inputField = new EditText(getActivity());
        inputField.setHint("Nickname");
        inputField.setPadding(100, 30, 100, 30);
        inputField.setSingleLine();
        // Creo filtro per la lunghezza del campo
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(15);  // NUMERO CARATTERI MASSIMI
        inputField.setFilters(filters);
        builder.setView(inputField);

        builder.setPositiveButton("Conferma", (dialog, which) -> {
            String newname = inputField.getText().toString();
            if(!newname.isEmpty()) {
                profileViewModel.updateProfileNameVM(SID, UID, newname);
                binding.textViewUserName.setText(newname);
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void showImageInputDialog(ActivityResultLauncher<PickVisualMediaRequest> pickMedia){
        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    public void showAlert(int value, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (value){
            case 1: // weapon
                builder.setTitle("Non hai un'arma!");
                builder.setMessage("Cerca nel mondo un'arma, ti aiuterà a battere i nemici.");
                break;
            case 2: // armor
                builder.setTitle("Non hai un'armatura!");
                builder.setMessage("Cerca nel mondo un'armatura, ti proteggerà dai danni degli scontri.");
                break;
            case 3: // amulet
                builder.setTitle("Non hai un amuleto!");
                builder.setMessage("Cerca nel mondo un amuleto, guadagnerai più esperienza equipaggiandolo.");
                break;
        }
        builder.setPositiveButton("OK", (dialog, id) -> {
            // User clicked OK button
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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


