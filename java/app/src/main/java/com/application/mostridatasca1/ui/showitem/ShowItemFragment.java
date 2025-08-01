package com.application.mostridatasca1.ui.showitem;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.application.mostridatasca1.R;
import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;
import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;
import com.application.mostridatasca1.databinding.FragmentShowitemBinding;
import com.application.mostridatasca1.networkcalls.ApiInterface;

import java.util.List;

public class ShowItemFragment extends Fragment {

    private static final String SWORD_TEXT = "Le armi permettono di sconfiggere i mostri subendo meno danni. I danni subiti sono diminuiti in una percentuale definita dal livello dell’arma";
    private static final String ARMOR_TEXT = "Le armature permettono di aumentare il numero massimo di punti vita. L'aumento è pari al livello dell’armatura";
    private static final String AMULET_TEXT = "L'amuleto permette di aumentare la distanza alla quale gli oggetti virtuali sono raggiungibili, di base 100 metri. L'aumento è pari al livello dell’amuleto in metri";
    private static final String CANDY_TEXT = "Che fortuna! Hai trovato un caramella! Le caramelle sono oggetti speciali che permettono di recuperare punti vita";
    private static final String MONSTER_TEXT = "Uno dei tanti nemici di questo mondo. Sconfiggilo per ottenere punti esperienza! Ma attento a non creparci... ";


    private NavController navController;
    private FragmentShowitemBinding binding;
    private static final String TAG = "MyLog";

    private VirtualObj virtualObj;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Istanza del Database
        ObjectRepository objectRepository = Room.databaseBuilder(this.getActivity(), ObjectRepository.class, "virtualObj").build();

        // Istanza del ViewModel
        ShowItemViewModel showItemViewModel = new ViewModelProvider(this, new ShowItemViewModelFactory(objectRepository)).get(ShowItemViewModel.class);

        // bindings
        binding = FragmentShowitemBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Recupero SID
        final String SID = showItemViewModel.getAccountSID(this.getActivity().getApplication());
        int lightBlueForItems = ContextCompat.getColor(getContext(), R.color.lightBlueForItems);
        int paddingDpItem = 10;
        int paddingPxItem = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDpItem, getResources().getDisplayMetrics());


        // richiesta Item -> aggiornamento UI
        Bundle bundle = getArguments();
        showItemViewModel.getItem(bundle.getInt("itemID"),SID).observe(getViewLifecycleOwner(), item -> {
            binding.textViewItemName.setText(item.name);

            binding.textViewLevel.setText("Livello "+item.level);
            binding.imageViewItem.setBackgroundColor(lightBlueForItems);
            binding.imageViewItem.setPadding(paddingPxItem, paddingPxItem, paddingPxItem, paddingPxItem);

            if(item.image == null)
                switch (item.type){
                    case "weapon":
                        binding.imageViewItem.setImageResource(R.drawable.sword_icon_noimage);
                        break;
                    case "armor":
                        binding.imageViewItem.setImageResource(R.drawable.armor_icon_noimage);
                        break;
                    case "amulet":
                        binding.imageViewItem.setImageResource(R.drawable.amulet_icon_noimage);
                        break;
                }
            else
                binding.imageViewItem.setImageBitmap(showItemViewModel.decodeTobase64(item.image));

            String infoText = "";
            // testo
            switch (item.type) {
                case "weapon":
                    infoText = SWORD_TEXT +"\n\nCon quest'arma  subirai il "+item.level+"% in meno di danni dai mostri!";
                    binding.textViewItemInfo.setText(infoText);
                    break;
                case "armor":
                    infoText = ARMOR_TEXT +"\n\nEquipaggiando questa armatura i tuoi punti vita massimi saliranno a "+(100+item.level)+"!";
                    binding.textViewItemInfo.setText(infoText);
                    break;
                case "amulet":
                    infoText = AMULET_TEXT +"\n\nIndossando questo Amuleto il tuo raggio di azione diventerà di "+(100+item.level)+" metri!";
                    binding.textViewItemInfo.setText(infoText);
                    break;
                case "candy":
                    infoText = CANDY_TEXT +"\n\nQuesta caramella ti permetterà di recuperare tra i "+item.level+"e"+(item.level*2)+" punti vita!";
                    binding.textViewItemInfo.setText(infoText);
                    break;
                case "monster":
                    infoText = MONSTER_TEXT +"\n\nSembra forte questo mostro, potresti perdere tra i "+item.level+"e"+(item.level*2)+" punti vita combattendo... ";
                    binding.textViewItemInfo.setText(infoText);
                    break;
            }

        });






        // Close button - torna al fragment precedente
        Button closeButton = binding.buttonClose;
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(v);
                navController.popBackStack();
            }
        });

        return root;
    }//onCreateView
}
