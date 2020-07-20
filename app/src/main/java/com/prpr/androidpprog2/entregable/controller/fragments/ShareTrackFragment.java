package com.prpr.androidpprog2.entregable.controller.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
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
import com.prpr.androidpprog2.entregable.model.DB.ObjectBox;
import com.prpr.androidpprog2.entregable.model.DB.SavedPlaylist;
import com.prpr.androidpprog2.entregable.model.DB.SavedTrack;
import com.prpr.androidpprog2.entregable.model.DB.UtilFunctions;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.utils.Session;
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

    //Gmail
    private ImageView icon_gmail;
    private TextView text_gmail;
    private LinearLayout layout_gmail;

    //SMS
    private ImageView icon_sms;
    private TextView text_sms;
    private LinearLayout layout_sms;

    //Copy Link
    private ImageView icon_link;
    private TextView text_link;
    private LinearLayout layout_link;

    //More
    private ImageView icon_more;
    private TextView text_more;
    private LinearLayout layout_more;

    private Track track;
		private User user;
		private Playlist playlist;

    private String url;

    public ShareTrackFragment(Track trck, User user, Playlist playlist) {
        this.track = trck;
				this.user = user;
        this.playlist = playlist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_share_song, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){



			portada = view.findViewById(R.id.SongCover);
			titol = view.findViewById(R.id.SongName);
			artista = view.findViewById(R.id.ArtistName);


            String tipus = "";
            String ending = "";
            if(track!=null && user ==null && playlist == null){
                tipus = "track";
                ending = track.getId()+"";
                titol.setText(track.getName());
                artista.setText(track.getUserLogin());
                if(track.getThumbnail()!=null){
                    if(UtilFunctions.noInternet(getActivity())){
                        if(UtilFunctions.trackExistsInDatabase(track)){
                            SavedTrack p = ObjectBox.get().boxFor(SavedTrack.class).get(track.getId());
                            Bitmap myBitmap = BitmapFactory.decodeFile(p.coverPath);
                            portada.setImageBitmap(myBitmap);
                        }else{
                            Picasso.get().load(R.drawable.default_track_cover).into(portada);
                        }
                    }else{
                     Picasso.get().load(track.getThumbnail()).into(portada);
                    }
                }else{
                    Picasso.get().load(R.drawable.default_track_cover).into(portada);
                }

            }else if(track ==null && user!=null && playlist==null){
                tipus = "user";
                ending = user.getLogin()+"";
                titol.setText(user.getLogin());
                artista.setVisibility(View.GONE);
                if(user.getImageUrl()!=null && !user.getImageUrl().equals("")){
                    if(!UtilFunctions.noInternet(getActivity())){
                        Picasso.get().load(user.getImageUrl()).into(portada);
                    }else{
                        Picasso.get().load(R.drawable.default_user_cover).into(portada);
                    }
                }else{
                    Picasso.get().load(R.drawable.default_user_cover).into(portada);
                }

				}else if(track==null && user==null && playlist!=null){
                    tipus = "playlist";
                    ending = playlist.getId()+"";
                    titol.setText(playlist.getName());
                    artista.setText(playlist.getOwner().getLogin());
                    if(playlist.getThumbnail()!=null){
                        if(UtilFunctions.noInternet(getActivity())){
                            if(UtilFunctions.playlistExistsInDatabase(playlist)){
                                SavedPlaylist p = ObjectBox.get().boxFor(SavedPlaylist.class).get(playlist.getId());
                                Bitmap myBitmap = BitmapFactory.decodeFile(p.coverPath);
                                portada.setImageBitmap(myBitmap);
                            }else{
                                Picasso.get().load(R.drawable.default_cover).into(portada);
                            }
                        }else{
                            Picasso.get().load(playlist.getThumbnail()).into(portada);
                        }
                    }else{
                        Picasso.get().load(R.drawable.default_cover).into(portada);
                    }

                }

        url = "http://sallefy.eu-west-3.elasticbeanstalk.com/"+tipus+"/" + ending;

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

        //Facebook
        icon_face = view.findViewById(R.id.icon_face);
        text_face = view.findViewById(R.id.text_face);
        layout_face = view.findViewById(R.id.layoutFacebook);
        layout_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookIntent();
            }
        });

        //Twitter
        icon_twit = view.findViewById(R.id.icon_twitter);
        text_twit = view.findViewById(R.id.text_twitter);
        layout_twit = view.findViewById(R.id.layoutTwitter);
        layout_twit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterIntent();
            }
        });

        //Gmail
        icon_gmail = view.findViewById(R.id.gmail_icon);
        text_gmail = view.findViewById(R.id.gmail_text);
        layout_gmail = view.findViewById(R.id.layoutGmail);
        layout_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gmailIntent();
            }
        });

        //SMS
        icon_sms = view.findViewById(R.id.icon_SMS);
        text_sms = view.findViewById(R.id.text_SMS);
        layout_sms = view.findViewById(R.id.layoutSMS);
        layout_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMSIntent();
            }
        });

        //Copy
        icon_link = view.findViewById(R.id.button_copy);
        text_link = view.findViewById(R.id.text_copy);
        layout_link = view.findViewById(R.id.layoutCopy);
        layout_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyLink();
            }
        });

        //More
        icon_more = view.findViewById(R.id.button_mes);
        text_more = view.findViewById(R.id.text_mes);
        layout_more = view.findViewById(R.id.layoutMes);
        layout_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                others();
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
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void facebookIntent(){
        Intent share=new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, url);
        share.setPackage("com.facebook.katana");
        startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
    }

    private void gmailIntent(){
        String urlString = "mailto:?subject=Sallefy&body=Check this out!\n\n" + url;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            intent.setPackage(null);
            startActivity(intent);
        }
    }

    private void twitterIntent(){
        String tweetUrl = "https://twitter.com/intent/tweet?text=Follow this link to checkout what amazing things live in Sallefy!&url=" +url;
        Uri uri = Uri.parse(tweetUrl);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void SMSIntent(){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sallefy");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(intent, url));
    }


    private void copyLink() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(url);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", url);
            clipboard.setPrimaryClip(clip);
        }
        Toast toast = Toast.makeText(Session.quinaActivityEsta(), "Link Copied!", Toast.LENGTH_LONG);
        View view = toast.getView();
        view.getBackground().setColorFilter(Color.parseColor("#21D760"), PorterDuff.Mode.SRC_IN);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        text.setTypeface(text.getTypeface(), Typeface.BOLD);
        toast.show();
    }

    private void others(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }




}
