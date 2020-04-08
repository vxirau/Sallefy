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


    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public ImageView iButton;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iButton =  (ImageView) itemView.findViewById(R.id.imageItem);

            iButton.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem Select = contextMenu.add(Menu.NONE, 1, 1, "Select");
            MenuItem Delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");
            Select.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1:
                        Toast.makeText(mContext, "Select Thumbnail", Toast.LENGTH_SHORT).show();

                        break;

                    case 2:
                        Toast.makeText(mContext, "Delete Thumbnail" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        };

    }
}
