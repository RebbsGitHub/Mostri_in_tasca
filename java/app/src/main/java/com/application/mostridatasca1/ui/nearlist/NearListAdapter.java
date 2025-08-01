package com.application.mostridatasca1.ui.nearlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mostridatasca1.R;

public class NearListAdapter extends RecyclerView.Adapter<NearListViewHolder>{

    private LayoutInflater inflater;
    private NearListViewModel viewModel;
    private RecyclerViewNearObjClickListener rcvl;

    public NearListAdapter(Context context, NearListViewModel viewModel, RecyclerViewNearObjClickListener rcvl) {
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.rcvl = rcvl;
    }

    //Implementare i metodi dell'adapter

    @Override
    public NearListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_nearlist, parent, false);
        return new NearListViewHolder(view,rcvl);
    }

    @Override
    public void onBindViewHolder(@NonNull NearListViewHolder holder, int position) {
        holder.bind(viewModel.getOggettoCompleto(position));
    }

    @Override
    public int getItemCount() {
        return viewModel.getOggettiComppletiNumber();
    }





}
