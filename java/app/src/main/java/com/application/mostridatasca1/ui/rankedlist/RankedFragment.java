package com.application.mostridatasca1.ui.rankedlist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.application.mostridatasca1.Utils;
import com.application.mostridatasca1.database.playerdb.User;
import com.application.mostridatasca1.database.playerdb.UserRepository;
import com.application.mostridatasca1.database.virtualobjdb.ObjectRepository;
import com.application.mostridatasca1.databinding.FragmentRankedBinding;
import com.application.mostridatasca1.ui.nearlist.NearListViewModel;

import java.util.ArrayList;
import java.util.List;

public class RankedFragment extends Fragment {

    private NavController navController;
    private FragmentRankedBinding binding;
    private static final String TAG = "MyLog";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Binding cose
        binding = FragmentRankedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //viewModel
        RankedViewModel rankedViewModel = new ViewModelProvider(this.getActivity()).get(RankedViewModel.class);
        binding.textViewRankedTitle.setText("TOP 20 Players");

        //Database Profiles
        UserRepository userRepository = Room.databaseBuilder(getActivity(), UserRepository.class, "profiles").build();
        // Database VirtualObj
        ObjectRepository objectRepository = Room.databaseBuilder(this.getActivity(), ObjectRepository.class, "virtualObj").build();
        // NavController
        NavController nvc = Navigation.findNavController(container);

        // SID - UID
        final int UID = Utils.getAccountUID(getActivity().getApplication());
        final String SID = Utils.getAccountSID(getActivity().getApplication());


        // Richiesta al server la lista dei migliori giocatori - Simpleuser
        rankedViewModel.getRankedPlayers(SID).observe(getViewLifecycleOwner(), rankedList -> {
            Log.d("MyLog", "getRankedPlayers: lista ricevuta - simpleuser: "+rankedList.size());
            rankedViewModel.setUpRankedList(userRepository,rankedList, SID, objectRepository);
        });


        RecyclerView recycleView = binding.rankedRecycleView;

        
        final MutableLiveData<List<User>> liveDataModel = rankedViewModel.getRankedListCompletaVM();
        liveDataModel.observe(getViewLifecycleOwner(), L -> {
            //Log.e(TAG, "L: "+L.size() );
            recycleView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            RankedAdapter adapter = new RankedAdapter(this.getContext(), rankedViewModel,  itemClicked -> {
                rankedViewModel.navigateToPlayersProfilesFragment(nvc, itemClicked.uid);
                Log.d(TAG, "ITEM CLICKED: "+ itemClicked.name +" "+ itemClicked.uid);

            });

            recycleView.setAdapter(adapter);


        });
        











        // Close button - torna al fragment precedente
        Button closeButton = binding.buttonCloseRanking;
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(v);
                navController.popBackStack();
            }
        });
        return root;
    }




}
