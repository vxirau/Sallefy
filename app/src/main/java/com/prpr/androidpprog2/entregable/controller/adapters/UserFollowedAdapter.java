package com.prpr.androidpprog2.entregable.controller.adapters;


import android.content.Context;
import android.text.TextUtils;
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
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UserFollowedAdapter extends RecyclerView.Adapter<UserFollowedAdapter.ViewHolder> {

    private static final String TAG = "UserAdapter";
    private ArrayList<User> users;
    private Context mContext;
    private UserCallback mCallback;

    public UserFollowedAdapter(Context context, ArrayList<User> users ) {
        this.users = users;
        this.mContext = context;
    }

    public void setUserCallback(final UserCallback itemClickCallback) {
        this.mCallback = itemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_followed_item, parent, false);
        return new UserFollowedAdapter.ViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mCallback!=null){
                    mCallback.onUserSelected(users.get(position));
                }
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mCallback!=null){
                    mCallback.onUserSelected(users.get(position));
                }
            }
        });


        holder.userlogin.setText(users.get(position).getLogin());

        holder.userlogin.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.userlogin.setSelected(true);
        holder.userlogin.setSingleLine(true);

        if(users.get(position).getTracks() != null){
            holder.userTracks.setText(String.valueOf(users.get(position).getTracks()) + " Tracks");
        }else{
            holder.userTracks.setText("N/A tracks by this user");
        }

        if(users.get(position).getFollowers() != null) {
            holder.userNumFollowers.setText(String.valueOf(users.get(position).getFollowers()) + " Followers");
        }else{
            holder.userNumFollowers.setText("N/A followers");
        }

        if(users.get(position).getFollowing() != null) {
            holder.userNumFollowing.setText(String.valueOf(users.get(position).getFollowing()) + " Following");
        }else {
            holder.userNumFollowing.setText("N/A following");
        }

        if(users.get(position).getPlaylists() != null) {
            holder.userNumPlaylists.setText(String.valueOf(users.get(position).getPlaylists()) + " Playlists");
        }else {
            holder.userNumPlaylists.setText("N/A playlists by this user");
        }

        if (users.get(position).getImageUrl() != null && !users.get(position).getImageUrl().isEmpty()) {
            Picasso.get().load(users.get(position).getImageUrl()).into(holder.image);
        }else{
            Picasso.get().load("https://user-images.githubusercontent.com/48185184/77792597-e939a400-7068-11ea-8ade-cd8b4e4ab7c9.png").into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mLayout;
        TextView userlogin;
        TextView userTracks;
        TextView userNumFollowers;
        TextView userNumFollowing;
        TextView userNumPlaylists;
        ImageButton image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mLayout = itemView.findViewById(R.id.user_followed_item_layout);
            userlogin = (TextView) itemView.findViewById(R.id.userLogin);
            userTracks = (TextView) itemView.findViewById(R.id.userNumTracks);
            userNumFollowers= (TextView) itemView.findViewById(R.id.numUsersFollowed);;
            userNumFollowing= (TextView) itemView.findViewById(R.id.numUsersFollowing);;
            userNumPlaylists= (TextView) itemView.findViewById(R.id.numUsersPlaylists);;
            image = (ImageButton) itemView.findViewById(R.id.userImage);
        }
    }
}
