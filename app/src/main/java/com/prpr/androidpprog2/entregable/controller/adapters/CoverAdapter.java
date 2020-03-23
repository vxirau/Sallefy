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
        private ArrayList<Playlist> playlist;
        private Context mContext;
        private PlaylistCallback mCallback;

        public CoverAdapter(Context context, ArrayList<Playlist> playlists ) {
            this.playlist = playlists;
            this.mContext = context;
        }

        public void setPlaylistCallback(final PlaylistCallback itemClickCallback) {
            this.mCallback = itemClickCallback;
        }

        @NonNull
        @Override
        public com.prpr.androidpprog2.entregable.controller.adapters.CoverAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: called.");
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
            return new com.prpr.androidpprog2.entregable.controller.adapters.CoverAdapter.ViewHolder(itemView);
        }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    public void onBindViewHolder(@NonNull com.prpr.androidpprog2.entregable.controller.adapters.PlaylistAdapter.ViewHolder holder, final int position) {
            holder.ivPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mCallback!=null){
                        mCallback.onPlaylistSelected(playlist.get(position));
                    }
                }
            });
            holder.nomPlaylist.setText(playlist.get(position).getName());
            int size = playlist.get(position).getTracks() != null ? playlist.get(position).getTracks().size() : 0 ;
            holder.totalCancons.setText( size + " cançons");

            if (playlist.get(position).getThumbnail() != null) {
                Picasso.get().load(playlist.get(position).getThumbnail()).into(holder.ivPicture);
            }else{
                Picasso.get().load("https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1").into(holder.ivPicture);
            }
        }

        @Override
        public int getItemCount() {
            return playlist != null ? playlist.size():0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout mLayout;
            TextView nomPlaylist;
            TextView totalCancons;
            ImageButton ivPicture;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                mLayout = itemView.findViewById(R.id.playlist_item_layout);
                nomPlaylist = (TextView) itemView.findViewById(R.id.playlist_title);
                totalCancons = (TextView) itemView.findViewById(R.id.totalSongs);
                ivPicture = (ImageButton) itemView.findViewById(R.id.playlistImatge);
            }
        }

}
