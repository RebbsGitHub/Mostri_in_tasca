package com.application.mostridatasca1.ui.nearlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.TextView;
import com.application.mostridatasca1.R;
import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;

import org.w3c.dom.Text;


public class NearListViewHolder  extends RecyclerView.ViewHolder{

        private TextView textViewLIV;
        private TextView textViewName;
        private TextView textViewObjectType;
        //private TextView textViewLat;
        //private TextView textViewLon;
        private ImageView imageViewObject;

        private VirtualObj oggettino;

        public NearListViewHolder(@NonNull View itemView,  RecyclerViewNearObjClickListener rvcl) {
            super(itemView);
            textViewLIV = itemView.findViewById(R.id.textViewLiv);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewObjectType = itemView.findViewById(R.id.textViewObjectType);
            //textViewLat = itemView.findViewById(R.id.textViewLat);
            //textViewLon = itemView.findViewById(R.id.textViewLon);
            imageViewObject = itemView.findViewById(R.id.imageViewNearListIcon);

            itemView.setOnClickListener(v -> {
                rvcl.onItemClicked(getItem());
            });

        }

        public void bind(VirtualObj virtualObj){
            oggettino = virtualObj;
            String latStr = "Lat: "+String.valueOf(virtualObj.lat);
            String lonStr = "Lon: "+String.valueOf(virtualObj.lon);
            textViewName.setText(virtualObj.name);
            textViewLIV.setText(String.valueOf(virtualObj.level));
            textViewObjectType.setText(virtualObj.type);
            //textViewLat.setText(latStr);
            //textViewLon.setText(lonStr);
            setImage(virtualObj.image, virtualObj.type);

        }

        private VirtualObj getItem(){
            return oggettino;
        }

        private void setImage(String imageBase64, String type){

            if(imageBase64 == null)
                switch(type){
                    case "weapon":
                        imageViewObject.setImageResource(R.drawable.sword_icon_noimage);
                        break;
                    case "armor":
                        imageViewObject.setImageResource(R.drawable.armor_icon_noimage);
                        break;
                    case "amulet":
                        imageViewObject.setImageResource(R.drawable.amulet_icon_noimage);
                        break;
                    case "candy":
                        imageViewObject.setImageResource(R.drawable.candy_icon_noimage);
                        break;
                    case "monster":
                        imageViewObject.setImageResource(R.drawable.enemy_icon_noimage);
                        break;
                }
            else
                imageViewObject.setImageBitmap(decodeTobase64(imageBase64));
        }

    public Bitmap decodeTobase64(String imageEncoded){
        byte[] decodedString = Base64.decode(imageEncoded, Base64.DEFAULT);
        Bitmap decodedByte = Bitmap.createBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        return decodedByte;
    }
}
