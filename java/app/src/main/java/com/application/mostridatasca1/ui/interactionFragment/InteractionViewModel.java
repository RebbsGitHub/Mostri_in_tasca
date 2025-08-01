package com.application.mostridatasca1.ui.interactionFragment;

import static java.lang.String.valueOf;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.application.mostridatasca1.R;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.networkcalls.ApiInterface;

import javax.security.auth.login.LoginException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InteractionViewModel extends ViewModel {

    private String SID;
    private int UID;
    private final String TAG = "MyLog";
    MutableLiveData<Boolean> endingInteraction = new MutableLiveData<>();

    public InteractionViewModel() {

    }




    public Bitmap decodeTobase64(String imageEncoded){
        byte[] decodedString = Base64.decode(imageEncoded, Base64.DEFAULT);
        Bitmap decodedByte = Bitmap.createBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        return decodedByte;
    }

    public void showAlert(int value, int itemID, Context context, LayoutInflater inflater, ApiInterface apiInterface,UserRepository userRepository) {


        //inflater passato, non posso chiamare getlayoutInflater
        View dialogView = inflater.inflate(R.layout.interaction_alert, null);

        // Crea l'AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // setta i componenti del dialog
        ImageView dialogImage = dialogView.findViewById(R.id.dialog_image);
        TextView dialogText = dialogView.findViewById(R.id.dialog_text);
        Button dialogButton1 = dialogView.findViewById(R.id.dialog_button1);
        Button dialogButton2 = dialogView.findViewById(R.id.dialog_button2);
        switch (value){
            case 1: // weapon
                dialogImage.setImageDrawable(context.getDrawable(R.drawable.backpack_alert));
                dialogText.setText("Vuoi equipaggiare questa arma?\nLascerai cadere a terra l'arma attuale.");
                break;
            case 2: // armor
                dialogImage.setImageDrawable(context.getDrawable(R.drawable.backpack_alert));
                dialogText.setText("Vuoi equipaggiare questa armatura?\nLascerai cadere a terra l'armatura attuale.");
                break;
            case 3: // amulet
                dialogImage.setImageDrawable(context.getDrawable(R.drawable.backpack_alert));
                dialogText.setText("Vuoi equipaggiare questo amuleto?\nLascerai cadere a terra l'amuleto attuale.");
                break;
            case 4: // candy
                dialogImage.setImageDrawable(context.getDrawable(R.drawable.healing_alert));
                dialogText.setText("Vuoi consumare questa caramella?");
                break;
            case 5: // monster
                dialogImage.setImageDrawable(context.getDrawable(R.drawable.fight_alert));
                dialogText.setText("Sei sicuro di voler entrare in battaglia?");
                break;
        }
        AlertDialog dialog = builder.create();
        dialog.show();

        // fai la cosa
        dialogButton1.setOnClickListener(v -> {
            dialog.dismiss();
            executeAction(value, itemID, apiInterface, userRepository, inflater, context);

        });
        // esci...
        dialogButton2.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }


    // weapon 1
    // armor 2
    // amulet 3
    // candy 4
    // monster 5
    public void executeAction(int value, int itemID, ApiInterface apiInterface, UserRepository userRepository, LayoutInflater inflater,Context context) {
        Log.e("MyLog", "executeAction: itemID "+itemID );

        // network call - POST oggetto / combattimento


        InteractionDataRequest idrSID = new InteractionDataRequest(SID);
        Call<InteractionData> updateUserCall = apiInterface.activateObjectID(itemID, idrSID);
        updateUserCall.enqueue(new Callback<InteractionData>() {

            @Override
            public void onResponse(Call<InteractionData> call, Response<InteractionData> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "executeAction-POST ITEM onResponse ERROR: " + response.code());
                }else{
                    Log.d(TAG, "executeAction-POST ITEM onResponse GOOD_ENDING: "+response.code());
                    InteractionData interactionData = response.body();
                    // aggiorni il player output(morto, hp, exp, ID tre equipaggiati)
                    Log.d("MyLog", "response:[ died:"+interactionData.died+"  HP: "+interactionData.life+"  EXP: "+interactionData.experience+"  weapon: "+interactionData.weapon+"  armor: "+interactionData.armor+"  amulet: "+interactionData.amulet+"]");

                    new Thread(()->{
                        userRepository.userDao().updateWeaponByID(UID, interactionData.weapon);
                        userRepository.userDao().updateArmorByID(UID, interactionData.armor);
                        userRepository.userDao().updateAmuletByID(UID, interactionData.amulet);
                        userRepository.userDao().updateLifeByID(UID, interactionData.life);
                        userRepository.userDao().updateExperienceByID(UID, interactionData.experience);
                    }).start();

                    if(value == 5){
                        Log.d(TAG, "onResponse: combattimento");
                        alertCombattimento(inflater, context, interactionData.died, interactionData.experience, interactionData.life);
                    }else{
                        endingInteraction.postValue(true);
                    }
                }
            }
            @Override
            public void onFailure(Call<InteractionData> call, Throwable t) {
                Log.e(TAG, "executeAction-POST ITEM onFailure ERROR: " + t.getMessage());
            }
        });


    }

    public void setAccountSID(Application application) {
        SharedPreferences sharedPreferences = application.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE);
        SID = sharedPreferences.getString("SID", null);
    }
    public void setAccountUID(Application application) {
        SharedPreferences sharedPreferences = application.getSharedPreferences("com.application.mostridatasca1", Context.MODE_PRIVATE);
        UID = sharedPreferences.getInt("UID", 0);
    }
    private void alertCombattimento(LayoutInflater inflater,Context context, boolean died, int experience, int life){

        //inflater passato, non posso chiamare getlayoutInflater
        View dialogView = inflater.inflate(R.layout.result_alert, null);
        // Crea l'AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // setta i componenti del dialog
        ImageView imageResult = dialogView.findViewById(R.id.imageViewResult);
        TextView textResult = dialogView.findViewById(R.id.textViewResult);
        TextView textResEXP = dialogView.findViewById(R.id.textViewResultEXP);
        TextView textResHP = dialogView.findViewById(R.id.textViewResultHP);
        Button buttonExit = dialogView.findViewById(R.id.buttonResultExit);

        if(!died){
            imageResult.setImageDrawable(context.getDrawable(R.drawable.win_image));
            textResult.setText("Vittoria!");
            String exp = "Esperienza totale   "+experience;
            textResEXP.setText(exp);
            String hp = "Punti vita rimasti   "+life;
            textResHP.setText(hp);
        }else{
            imageResult.setImageDrawable(context.getDrawable(R.drawable.loss_image));
            textResult.setText("Sconfitta...");
            textResEXP.setText("Esperienza totale   0");
            textResHP.setText("Punti vita rimasti   0");
        }

        AlertDialog dialog = builder.create();
        dialog.show();


            // esci...
            buttonExit.setOnClickListener(v -> {
                endingInteraction.postValue(true);
                dialog.dismiss();
            });
    }// alert combattimento

    public LiveData<Boolean> getEndingInteraction() {
        return endingInteraction;
    }
    public void setTrueEndingInteraction(){
        endingInteraction.postValue(false);
    }
}
