package com.application.mostridatasca1.ui.rankedlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.mostridatasca1.R;

public class RankedAdapter extends RecyclerView.Adapter<RankedViewHolder>{

    private LayoutInflater inflater;
    private RankedViewModel viewModel;
    private RecyclerViewRanked rcvl;


    public RankedAdapter(Context context, RankedViewModel viewModel, RecyclerViewRanked rcvl) {
        this.inflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.rcvl = rcvl;

    }

    // Metodi adapter

    @Override
    public RankedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_rankedlist, parent, false);
        return new RankedViewHolder(view,rcvl);
    }

    @Override
    public void onBindViewHolder(@NonNull RankedViewHolder holder, int position) {
        holder.bind(viewModel.getUserCompleto(position));
    }

    @Override
    public int getItemCount() {
        return viewModel.getRankedListCompletaVMNumber();
    }
}
