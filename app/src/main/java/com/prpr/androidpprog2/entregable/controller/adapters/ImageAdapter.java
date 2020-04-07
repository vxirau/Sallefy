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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.activities.ImageActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<String> mFiles;
    private OnClickListener ilistener;

    public ImageAdapter(Context mContext, List<String> mFiles){
        this.mContext = mContext;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        String uri = mFiles.get(position);
        Picasso.get().load(uri).placeholder(R.mipmap.ic_launcher_round).fit().centerCrop().into(holder.iButton);

        holder.iButton.setOnClickListener(new View.OnClickListener(){

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

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public ImageButton iButton;

        public ImageViewHolder(View itemView){
            super(itemView);
            iButton = itemView.findViewById(R.id.imageItem);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("What are we going to do?");
            MenuItem SelectPhoto = contextMenu.add(Menu.NONE,1,1,"Select Photo");
            MenuItem Delete = contextMenu.add(Menu.NONE,2,2,"Delete");

            SelectPhoto.setOnMenuItemClickListener(this);
            Delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(ilistener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch(menuItem.getItemId()) {
                        case 1:
                            ilistener.OnSelectedItem(position);
                            return true;
                        case 2:
                            ilistener.onDeleteItem(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnClickListener {

        void OnSelectedItem(int position);

        void onDeleteItem(int position);

    }

    public void onClickedItem(OnClickListener listener){
        ilistener = listener;
    }


}
