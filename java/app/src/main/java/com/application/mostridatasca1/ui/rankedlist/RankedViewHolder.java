package com.application.mostridatasca1.ui.rankedlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mostridatasca1.R;
import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;

public class RankedViewHolder extends RecyclerView.ViewHolder {

    private TextView textViewRankedListName;
    private TextView textViewRankedEXP;

    private ImageView imageViewRankedImage;

    private ImageView imageRankedWeaponBlock;
    private ImageView imageRankedArmorBlock;
    private ImageView imageRankedAmuletBlock;

    private User userPassed;


    public RankedViewHolder(@NonNull View itemView, RecyclerViewRanked rcvl) {
        super(itemView);
        textViewRankedListName = itemView.findViewById(R.id.textViewRankedListName);
        textViewRankedEXP = itemView.findViewById(R.id.textViewRankedEXP);

        imageViewRankedImage = itemView.findViewById(R.id.imageViewRankedImage);
        //immaginette
        imageRankedWeaponBlock = itemView.findViewById(R.id.imageRankedWeaponBlock);
        imageRankedArmorBlock = itemView.findViewById(R.id.imageRankedArmorBlock);
        imageRankedAmuletBlock = itemView.findViewById(R.id.imageRankedAmuletBlock);

        itemView.setOnClickListener(v -> {
            rcvl.onItemClicked(getItem());
        });
    }

    public void bind(User user){
        userPassed = user;
        textViewRankedListName.setText(user.name);
        String expStr = "Exp: "+String.valueOf(user.experience);
        textViewRankedEXP.setText(expStr);
        String hpStr = "HP: "+String.valueOf(user.life);

        setImage(user.picture);




        setBlocks(user.weapon, user.armor, user.amulet);
    }

    private void setImage(String imageBase64){

        if(imageBase64 == null) {
            imageViewRankedImage.setImageResource(R.drawable.profilepic_noimage);
        }
        else
            imageViewRankedImage.setImageBitmap(decodeTobase64(imageBase64));
    }
    private Bitmap decodeTobase64(String imageEncoded){
        byte[] decodedString = Base64.decode(imageEncoded, Base64.DEFAULT);
        try {
            Bitmap decodedByte = Bitmap.createBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            return decodedByte;
        } catch (Exception e) {
            Log.e("MyLog", "Errore nel caricamento dell'immagine - something's null");
            return null;
        }
    }

    private void setBlocks(int weapon, int armor, int amulet){

        if(weapon == 0) {
            imageRankedWeaponBlock.setBackgroundColor(0xFF9aa3a6);
            imageRankedWeaponBlock.setImageResource(R.drawable.sword_icon);
        }
        else {
            imageRankedWeaponBlock.setBackgroundColor(0xFF3b8fa8);
            imageRankedWeaponBlock.setImageResource(R.drawable.sword_icon);
        }
        if(armor == 0) {
            imageRankedArmorBlock.setBackgroundColor(0xFF9aa3a6);
            imageRankedArmorBlock.setImageResource(R.drawable.armor_icon);
        }
        else {
            imageRankedArmorBlock.setBackgroundColor(0xFF3b8fa8);
            imageRankedArmorBlock.setImageResource(R.drawable.armor_icon);
        }

        if(amulet == 0) {
            imageRankedAmuletBlock.setBackgroundColor(0xFF9aa3a6);
            imageRankedAmuletBlock.setImageResource(R.drawable.amulet_icon);

        }
        else {
            imageRankedAmuletBlock.setBackgroundColor(0xFF3b8fa8);
            imageRankedAmuletBlock.setImageResource(R.drawable.amulet_icon);

        }
    }

    private User getItem(){
        return userPassed;
    }
}
