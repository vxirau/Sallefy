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


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private static final String TAG = "UserAdapter";
    private ArrayList<User> users;
    private Context mContext;
    private UserCallback mCallback;

    public UserAdapter(Context context, ArrayList<User> users ) {
        this.users = users;
        this.mContext = context;
    }

    public void setUserCallback(final UserCallback itemClickCallback) {
        this.mCallback = itemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback!=null){
                    mCallback.onUserSelected(users.get(position));
                }
            }
        });
        if(users.get(position).getFirstName() != null & users.get(position).getLastName()!=null){
            holder.username.setText(users.get(position).getFirstName() + " " + users.get(position).getLastName());
        }else if(users.get(position).getFirstName() != null & users.get(position).getLastName()==null){
            holder.username.setText(users.get(position).getFirstName());
        }else{
            holder.username.setText("-- --");
        }
        holder.username.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.username.setSelected(true);
        holder.username.setSingleLine(true);
        holder.userlogin.setText(users.get(position).getLogin());

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
        TextView username;
        TextView userlogin;
        ImageButton image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mLayout = itemView.findViewById(R.id.user_item_layout);
            username = (TextView) itemView.findViewById(R.id.userName);
            userlogin = (TextView) itemView.findViewById(R.id.userLogin);
            image = (ImageButton) itemView.findViewById(R.id.userImage);
        }
    }
}
