package com.prpr.androidpprog2.entregable.controller.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.PlaylistManager;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.squareup.picasso.Picasso;

public class ShareTrackFragment extends BottomSheetDialogFragment {

    private ImageView portada;
    private TextView titol;
    private TextView artista;

    //Whats
    private ImageView icon_whats;
    private TextView text_whats;
    private LinearLayout layout_whats;

    //Facebook
    private ImageView icon_face;
    private TextView text_face;
    private LinearLayout layout_face;

    //Twitter
    private ImageView icon_twit;
    private TextView text_twit;
    private LinearLayout layout_twit;

    //Instagram
    private ImageView icon_insta;
    private TextView text_insta;
    private LinearLayout layout_insta;

    //Copy Link
    private ImageView icon_link;
    private TextView text_link;
    private LinearLayout layout_link;

    private Track track;

    private String url;

    public ShareTrackFragment(Track trck) {
        track = trck;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_share_song, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){

        url = "http://sallefy.eu-west-3.elasticbeanstalk.com/track/" + track.getId();


        portada = view.findViewById(R.id.SongCover);
        titol = view.findViewById(R.id.SongName);
        artista = view.findViewById(R.id.ArtistName);

        titol.setText(track.getName());
        artista.setText(track.getUserLogin());

        if(track.getThumbnail()!=null){
            Picasso.get().load(track.getThumbnail()).into(portada);
        }else{
            Picasso.get().load("https://community.spotify.com/t5/image/serverpage/image-id/25294i2836BD1C1A31BDF2/image-size/original?v=mpbl-1&px=-1").into(portada);
        }

        //Whatsapp
        icon_whats = view.findViewById(R.id.whats_icon);
        text_whats = view.findViewById(R.id.whats_text);
        layout_whats = view.findViewById(R.id.layoutwhats);

        layout_whats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatsappIntent();
            }
        });


    }

    private void whatsappIntent(){
        PackageManager pm = getActivity().getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = url;

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }



    private void facebookIntent(){
        String url = "https://www.facebook.com/";
        Intent i= new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void twitterIntent(){
        String url = "https://www.twitter.com/";
        Intent i= new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void instagramIntent(){
        String url = "https://www.instagram.com/";
        Intent i= new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    /*private void copyLink(){
        text_link.setText(uri.getText().toString());
    }*/





}
