package com.prpr.androidpprog2.entregable.controller.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.GenreCallback;
import com.prpr.androidpprog2.entregable.model.Genre;


import java.util.ArrayList;

public class GenereAdapter extends RecyclerView.Adapter<GenereAdapter.ViewHolder> {

    private static final String TAG = "GenereAdapter";
    private ArrayList<Genre> generes;
    private Context mContext;
    private GenreCallback mCallback;

    public GenereAdapter(Context context, ArrayList<Genre> generes ) {
        this.generes = generes;
        this.mContext = context;
    }

    public void setGenereCallback(final GenreCallback itemClickCallback) {
        this.mCallback = itemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.genere_item, parent, false);
        return new GenereAdapter.ViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onGenreSelected(generes.get(position));
            }
        });

        holder.nomGenere.setText(generes.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return generes != null ? generes.size():0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mLayout;
        TextView nomGenere;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = (LinearLayout) itemView.findViewById(R.id.genere_item_layout);
            nomGenere = (TextView) itemView.findViewById(R.id.genere_name);
        }
    }
}
