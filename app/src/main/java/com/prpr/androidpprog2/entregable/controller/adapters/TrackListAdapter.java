package com.prpr.androidpprog2.entregable.controller.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.activities.AddSongsBunchActivity;
import com.prpr.androidpprog2.entregable.controller.callbacks.TrackListCallback;
import com.prpr.androidpprog2.entregable.model.DB.UtilFunctions;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

    private static final String TAG = "TrackListAdapter";
    private ArrayList<Track> mTracks;
    private Context mContext;
    private TrackListCallback mCallback;
    private Playlist plylst;

    public TrackListAdapter(TrackListCallback callback, Context context, ArrayList<Track> tracks, Playlist playlist) {
        mTracks = tracks;
        mContext = context;
        mCallback = callback;
        this.plylst = playlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called.");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_item, parent, false);
        return new TrackListAdapter.ViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onTrackSelected(position);
            }
        });
        holder.addSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onTrackAddSelected(position, mTracks, plylst);
            }
        });
        holder.tvTitle.setText(mTracks.get(position).getName());
        holder.tvAuthor.setText(mTracks.get(position).getUserLogin());

        if(mTracks.get(position).isLiked()){
            holder.like.setBackgroundResource(R.drawable.ic_heart);
        }else{
            holder.like.setBackgroundResource(R.drawable.ic_heart_no);
        }
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onTrackSelectedLiked(position);
                if(mTracks.get(position).isLiked()){
                    holder.like.setBackgroundResource(R.drawable.ic_heart_no);
                }else{
                    holder.like.setBackgroundResource(R.drawable.ic_heart);
                }
            }
        });

        String segons ="";
        if(mTracks.get(position).getDuration()!=null){
            if(mTracks.get(position).getDuration()%60<10){
                segons = "0" + mTracks.get(position).getDuration()%60;
            }else{
                segons = String.valueOf(mTracks.get(position).getDuration()%60);
            }
            holder.trackLength.setText(mTracks.get(position).getDuration()/60 + ":" + segons);
        }else{
            holder.trackLength.setText("00:00");
        }

        if(UtilFunctions.trackExistsInDatabase(mTracks.get(position))){
            holder.downloaded.setVisibility(View.VISIBLE);
        }else{
            holder.downloaded.setVisibility(View.INVISIBLE);
        }

        if (mTracks.get(position).getThumbnail() != null && !mTracks.get(position).getThumbnail().equals("")) {
            Picasso.get().load(mTracks.get(position).getThumbnail()).into(holder.ivPicture);
        }else{
            Picasso.get().load("https://user-images.githubusercontent.com/48185184/77687559-e3778c00-6f9e-11ea-8e14-fa8ee4de5b4d.png").into(holder.ivPicture);
        }
        //Per carregar foto sense internet desde el local.
        //Picasso.with(context).load(new File(YOUR_FILE_PATH)).into(imageView);

    }

    @Override
    public int getItemCount() {
        return mTracks != null ? mTracks.size():0;
    }

    public void updateTrackLikeStateIcon(int position, boolean isLiked) {
        mTracks.get(position).setLiked(isLiked);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mLayout;
        TextView tvTitle;
        Button addSong;
        Button like;
        TextView tvAuthor;
        TextView trackLength;
        ImageView ivPicture;
        ImageView downloaded;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addSong = itemView.findViewById(R.id.addSong);
            trackLength = itemView.findViewById(R.id.track_duratio);
            mLayout = itemView.findViewById(R.id.track_item_layout);
            like = itemView.findViewById(R.id.add2Favorite);
            tvTitle = (TextView) itemView.findViewById(R.id.track_title);
            tvAuthor = (TextView) itemView.findViewById(R.id.track_author);
            ivPicture = (ImageView) itemView.findViewById(R.id.track_img);
            downloaded = itemView.findViewById(R.id.downloaded);
        }
    }
}
