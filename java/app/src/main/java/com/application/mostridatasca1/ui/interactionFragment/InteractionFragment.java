package com.application.mostridatasca1.ui.interactionFragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.application.mostridatasca1.R;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.databinding.FragmentInteractionBinding;
import com.application.mostridatasca1.networkcalls.ApiInterface;
import com.application.mostridatasca1.networkcalls.RetrofitProvider;

import java.util.Objects;

public class InteractionFragment extends Fragment {

    final String TAG = "MyLog";
    private FragmentInteractionBinding binding;
    //private NavController navController;
    private Button interactButton;
    private ApiInterface apiInterface = RetrofitProvider.getApiInterface();
    private int itemID;
    private String itemName;
    private String itemType;
    private int itemLevel;
    private String itemImage;

    private final String SWORDTEXT="Wow, chi ha lasciato questo spadone qui?\n Il livello delle armi diminuisce i danni che subiresti da uno scontro";
    private final String ARMORTEXT="Questa armatura sembra bella resistente!\n Il livello delle armature aumenta i tuoi punti vita ";
    private final String AMULETTEXT="Un amuleto, ti senti più forte solo a tenerlo in mano!\n Ogni livello dell'aumleto aumenta di un metro la tua area di interazione!";
    private final String CANDYTEXT="Questa caramella è molto buona, ti senti più energico!";
    private final String MONSTERTEXT="Un mostro! Se vuoi diventare più forte devi sconfiggerlo!\n Attento a non creparci!";


    private InteractionViewModel interactionListViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //NavController
        NavController navController = Navigation.findNavController(container);

        //viewModel
        interactionListViewModel = new ViewModelProvider(this.getActivity()).get(InteractionViewModel.class);
        //Binding cose
        binding = FragmentInteractionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // ViewModel: set del SID che serve
        interactionListViewModel.setAccountSID(this.getActivity().getApplication());
        interactionListViewModel.setAccountUID(this.getActivity().getApplication());

        //database Profiles
        UserRepository userRepository = Room.databaseBuilder(getActivity(), UserRepository.class, "profiles").build();

        // cose nel Bundle
        Bundle bundle = getArguments();
        itemID = bundle.getInt("itemID");
        itemName = bundle.getString("itemName");
        itemType = bundle.getString("itemType");
        itemLevel = bundle.getInt("itemLevel");
        itemImage = bundle.getString("itemImage");

        // Settaggio delle TextView
        binding.textViewNameIF.setText(itemName);
        binding.textViewLevelIF.setText(String.valueOf(itemLevel));
        switch (itemType){
            case "weapon":
                String textsword = SWORDTEXT+"\n\nPercentuale danni ridotti: "+itemLevel+"%";
                binding.textViewInfoIF.setText(textsword);
                break;
            case "armor":
                String textarmor = ARMORTEXT+"\n\nPunti vita aggiunti: "+itemLevel;
                binding.textViewInfoIF.setText(textarmor);
                break;
            case "amulet":
                String textamulet = AMULETTEXT+"\n\nArea raggiungibile: "+(100+itemLevel)+"m";
                binding.textViewInfoIF.setText(textamulet);
                break;
            case "candy":
                String textcandy = CANDYTEXT+"\n\nRigenerazione punti vita: "+itemLevel+" - "+(itemLevel*2);
                binding.textViewInfoIF.setText(textcandy);
                break;
            case "monster":
                String textmonster = MONSTERTEXT+"\n\nDanni possibili: "+itemLevel+" - "+itemLevel+"\nPunti esperienza alla vittoria: "+itemLevel;
                binding.textViewInfoIF.setText(textmonster);
                break;
        }
        int doppioItemLevel = itemLevel*2;
        ImageView imageViewItem = binding.imageViewObjInteractionF;
        if(itemImage == null)
            switch (itemType){
                case "weapon":
                    imageViewItem.setImageResource(R.drawable.sword_bigimage_noimage);
                    String textsword = SWORDTEXT+"\n\nPercentuale danni ridotti: "+itemLevel+"%";
                    binding.textViewInfoIF.setText(textsword);
                    break;
                case "armor":
                    imageViewItem.setImageResource(R.drawable.armor_icon_noimage);
                    String textarmor = ARMORTEXT+"\n\nPunti vita aggiunti: "+itemLevel;
                    binding.textViewInfoIF.setText(textarmor);
                    break;
                case "amulet":
                    imageViewItem.setImageResource(R.drawable.amulet_bigimage_noimage);
                    String textamulet = AMULETTEXT+"\n\nArea raggiungibile: "+(100+itemLevel)+"m";
                    binding.textViewInfoIF.setText(textamulet);
                    break;
                case "candy":
                    imageViewItem.setImageResource(R.drawable.candy_bigimage_noimage);
                    String textcandy = CANDYTEXT+"\n\nRigenerazione punti vita: "+itemLevel+" - "+ doppioItemLevel;
                    binding.textViewInfoIF.setText(textcandy);
                    break;
                case "monster":
                    imageViewItem.setImageResource(R.drawable.monster_bigimage_noimage);
                    String textmonster = MONSTERTEXT+"\n\nDanni possibili: "+itemLevel+" - " +  doppioItemLevel + " \nPunti esperienza alla vittoria: "+itemLevel;
                    binding.textViewInfoIF.setText(textmonster);
                    break;
            }
        else
            imageViewItem.setImageBitmap(interactionListViewModel.decodeTobase64(itemImage));

        interactButton = binding.buttonUse;
        switch(Objects.requireNonNull(bundle.getString("itemType"))){
            case "weapon":
                Log.d(TAG, "onCreateView: weapon");
                interactButton.setText("Equipaggia !");
                break;
            case "armor":
                Log.d(TAG, "onCreateView: armor");
                interactButton.setText("Equipaggia !");
                break;
            case "amulet":
                Log.d(TAG, "onCreateView: amulet");
                interactButton.setText("Equipaggia !");
                break;
            case "candy":
                Log.d(TAG, "onCreateView: candy");
                interactButton.setText("Consuma");
                break;
            case "monster":
                Log.d(TAG, "onCreateView: monster");
                interactButton.setText("COMBATTI !!");
                break;


        }


        // ClickListener Weapon
        interactButton.setOnClickListener(v -> {
            switch(Objects.requireNonNull(bundle.getString("itemType"))){
                case "weapon":
                    interactionListViewModel.showAlert(1,itemID, this.getContext(), getLayoutInflater(), apiInterface, userRepository);
                    break;
                case "armor":
                    interactionListViewModel.showAlert(2,itemID, this.getContext(), getLayoutInflater(), apiInterface, userRepository);
                    break;
                case "amulet":
                    interactionListViewModel.showAlert(3,itemID, this.getContext(), getLayoutInflater(), apiInterface, userRepository);
                    break;
                case "candy":
                    interactionListViewModel.showAlert(4,itemID, this.getContext(), getLayoutInflater(), apiInterface, userRepository);
                    break;
                case "monster":
                    interactionListViewModel.showAlert(5,itemID, this.getContext(), getLayoutInflater(), apiInterface, userRepository);
                    break;
            }
        });



        //observer endingInteraction
        interactionListViewModel.getEndingInteraction().observe(getViewLifecycleOwner(), endingInteraction -> {
            if(endingInteraction){
                interactionListViewModel.setTrueEndingInteraction();
                navController.popBackStack();
            }
        });


        // Close button - torna al fragment precedente
        Button closeButton = binding.buttonExit;

        closeButton.setOnClickListener(v -> {
            boolean x = navController.popBackStack();
            if(x)
                Log.e(TAG, "INTERACTION FRAGMENT IN CHIUSURA: True" );
            else
                Log.e(TAG, "INTERACTION FRAGMENT IN CHIUSURA: False" );
            //Navigation.findNavController(root).navigate(R.id.navigation_profile);
        });


        return root;
    }





}
