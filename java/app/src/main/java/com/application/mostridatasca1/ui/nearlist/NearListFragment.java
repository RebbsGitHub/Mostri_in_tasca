package com.application.mostridatasca1.ui.nearlist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.application.mostridatasca1.MainActivityViewModel;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.database.simplevirtualobjdb.SimpleVirtualObj;
import com.application.mostridatasca1.database.simplevirtualobjdb.SimpleVirtualObjRepository;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;
import com.application.mostridatasca1.database.virtualobjdb.VirtualObj;
import com.application.mostridatasca1.databinding.FragmentNearlistBinding;

import java.util.List;

public class NearListFragment extends Fragment {
    private FragmentNearlistBinding binding;
    private static final String TAG = "MyLog";

    private Double playerLAT = 0.0;
    private Double playerLON = 0.0;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Database SimpleVirtualObj
        SimpleVirtualObjRepository simpleVirtualObjRepository = Room.databaseBuilder(getActivity(), SimpleVirtualObjRepository.class, "simpleVirtualObj").build();
        // Database VirtualObj
        ObjectRepository objectRepository = Room.databaseBuilder(this.getActivity(), ObjectRepository.class, "virtualObj").build();
        //database Profiles
        UserRepository userRepository = Room.databaseBuilder(getActivity(), UserRepository.class, "profiles").build();

        // chiedo gentilmente il NavController
        NavController nvc = Navigation.findNavController(container);

        //Binding cose
        binding = FragmentNearlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //viewModel
        NearListViewModel nearListViewModel = new ViewModelProvider(this.getActivity()).get(NearListViewModel.class);
        //MainActivityViewModel call (requireActivity())
        MainActivityViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);


       // SID - UID
        final int UID = NearListViewModel.getAccountUID(getActivity().getApplication());
        final String SID = NearListViewModel.getAccountSID(getActivity().getApplication());

        // coordinate player
        mainViewModel.getPlayerLAT().observe(getViewLifecycleOwner(), latdouble -> {playerLAT = latdouble;});
        mainViewModel.getPlayerLON().observe(getViewLifecycleOwner(), londouble -> {playerLON = londouble;});

        // liv ciondolo +100 -> range di interesse
        // funzione che tiene tutto e setta lke cose per il viewModel
        //
        // otteniamo i dati completi dei VirtuaObj a portata di mano in un array
        // livello amuleto indossato
        NearListViewModel.amuletLevel(UID,userRepository, objectRepository).observe(getViewLifecycleOwner(), level -> {
            int range = 100+level;

            // OK       Log.d("MyLog", "NearListFragment: range finale: "+range);

            nearListViewModel.setUpCloseList(playerLAT,playerLON, simpleVirtualObjRepository, range, objectRepository, SID );

        });// observe amuletLevel


        RecyclerView recycleView = binding.objectsRecycleView;

        final MutableLiveData<List<VirtualObj>> liveDataModel = nearListViewModel.getOggettiCompletiVM();
        liveDataModel.observe(getViewLifecycleOwner(), L -> {
            //Log.e(TAG, "L: "+L.size() );
            recycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            NearListAdapter adapter = new NearListAdapter(this.getContext(), nearListViewModel,  itemClicked -> {

                nearListViewModel.navigateToInteractionFragment(nvc, itemClicked);

                Log.d(TAG, "ITEM CLICKED: "+ itemClicked.name + itemClicked.id);
            });


            recycleView.setAdapter(adapter);


        });













        //int x = nearListViewModel.calculateDistance(45.4769,9.2321 ,45.6533 ,8.8267 );
        //Log.d(TAG, "onCreateView: PROVA CALCOLO DISTANZA FUNZIONAAAAA indicato in XX.XXX sono i metri, poi valori piccoli: "+x);

        final TextView textView = binding.textViewNearList;
        nearListViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }
}