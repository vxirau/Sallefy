package com.prpr.androidpprog2.entregable.controller.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CoverAdapter extends RecyclerView.Adapter<CoverAdapter.ViewHolder>{

        private static final String TAG = "CoverAdapter";
        private ArrayList<ImageButton> thumbnail;
        private Context mContext;
        private PlaylistCallback mCallback;

        public CoverAdapter(Context context, ArrayList<ImageButton> thumbnail ) {
            this.thumbnail = thumbnail;
            this.mContext = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: called.");
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cover_item, parent, false);
            return new com.prpr.androidpprog2.entregable.controller.adapters.CoverAdapter.ViewHolder(itemView);
        }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.coPicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
              if(position == getItemCount()){
                  //add thumbnail and set as thumbnail
              } else {
                  //ens guardem
              }
            }
        });
    }

    @Override
    public int getItemCount() {
        return thumbnail.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout uLayout;
            ImageButton coPicture;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                uLayout = itemView.findViewById(R.id.cover_item_layout);
                coPicture = (ImageButton) itemView.findViewById(R.id.coverImage);
            }
        }

}
