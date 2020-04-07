package com.prpr.androidpprog2.entregable.controller.adapters;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.activities.ImageActivity;
import com.prpr.androidpprog2.entregable.model.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Upload> mFiles;

    public ImageAdapter(Context mContext, List<Upload> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        Upload uCurrent = mFiles.get(position);
        //Picasso.get().load(uCurrent.getImageUrl()).fit().centerCrop().into(holder.iButton);
        Glide.with(mContext).load(uCurrent.getImageUrl()).into(holder.iButton);
        holder.iButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Item " + position, Toast.LENGTH_SHORT).show();
            }
        });
        holder.iButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView iButton;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iButton =  (ImageView) itemView.findViewById(R.id.imageItem);

        }

    }
}
