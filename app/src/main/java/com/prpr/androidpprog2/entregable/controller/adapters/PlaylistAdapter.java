package com.prpr.androidpprog2.entregable.controller.adapters;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.util.Util;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.PlaylistCallback;
import com.prpr.androidpprog2.entregable.model.DB.ObjectBox;
import com.prpr.androidpprog2.entregable.model.DB.SavedPlaylist;
import com.prpr.androidpprog2.entregable.model.DB.UtilFunctions;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private static final String TAG = "PlaylistListAdapter";
    private ArrayList<Playlist> playlist;
    private Context mContext;
    private PlaylistCallback mCallback;


    public PlaylistAdapter(Context context, ArrayList<Playlist> playlists ) {
        this.playlist = playlists;
        this.mContext = context;
    }

    public void setPlaylistCallback(final PlaylistCallback itemClickCallback) {
        this.mCallback = itemClickCallback;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new PlaylistAdapter.ViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback!=null){
                    mCallback.onPlaylistSelected(playlist.get(position));
                }
            }
        });
        holder.nomPlaylist.setText(playlist.get(position).getName());
        holder.nomPlaylist.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.nomPlaylist.setSelected(true);
        holder.nomPlaylist.setSingleLine(true);
        int size = playlist.get(position).getTracks() != null ? playlist.get(position).getTracks().size() : 0 ;
        holder.totalCancons.setText( size + " songs");

        if(UtilFunctions.playlistExistsInDatabase(playlist.get(position))){
            holder.downloaded.setVisibility(View.VISIBLE);
        }else{
            holder.downloaded.setVisibility(View.INVISIBLE);
        }

        if(UtilFunctions.noInternet(mContext)){
            if(UtilFunctions.playlistExistsInDatabase(playlist.get(position))){
                Picasso.get().load(ObjectBox.get().boxFor(SavedPlaylist.class).get(playlist.get(position).getId()).coverPath).into(holder.ivPicture);
            }else{
                Picasso.get().load(R.drawable.default_cover).into(holder.ivPicture);
            }
        }else{
            if (playlist.get(position).getThumbnail() != null) {
                Picasso.get().load(playlist.get(position).getThumbnail()).into(holder.ivPicture);
            }else {
                Picasso.get().load(R.drawable.default_cover).into(holder.ivPicture);
            }
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
        ImageView downloaded;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mLayout = itemView.findViewById(R.id.playlist_item_layout);
            nomPlaylist = (TextView) itemView.findViewById(R.id.playlist_title);
            totalCancons = (TextView) itemView.findViewById(R.id.totalSongs);
            ivPicture = (ImageButton) itemView.findViewById(R.id.playlistImatge);
            downloaded = itemView.findViewById(R.id.downloaded);
        }
    }
}
