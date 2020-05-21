package com.prpr.androidpprog2.entregable.controller.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.activities.InfoArtistaActivity;
import com.prpr.androidpprog2.entregable.controller.activities.PlaylistActivity;
import com.prpr.androidpprog2.entregable.controller.callbacks.DownloadCallback;
import com.prpr.androidpprog2.entregable.controller.callbacks.OptionDialogCallback;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.dialogs.LoadingDialog;
import com.prpr.androidpprog2.entregable.controller.dialogs.OptionDialog;
import com.prpr.androidpprog2.entregable.model.DB.ObjectBox;
import com.prpr.androidpprog2.entregable.model.DB.SavedPlaylist;
import com.prpr.androidpprog2.entregable.model.DB.SavedTrack;
import com.prpr.androidpprog2.entregable.model.DB.UtilFunctions;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.IOException;

public class InfoPlaylistFragment extends BottomSheetDialogFragment implements DownloadCallback, OptionDialogCallback {

    private Playlist playlist;
    private ImageView playlistCover;
    private TextView playlistName;
    private TextView artistName;
    private TextView playlistArtist;
    private LinearLayout layoutArtist;
    private LinearLayout layoutedit;
    private LinearLayout layoutdelete;
    private LinearLayout layoutShare;
    private LinearLayout layoutDownload;
    private Switch download;
    private int i = 0;
    private Boolean switchState;
    private int[] downloadId;
    private LoadingDialog loading;
    private LoadingDialog deleting;
    private SavedPlaylist p;
    private OptionDialog confirm;
    private Boolean doChange = true;


    public InfoPlaylistFragment(Playlist p) {
        playlist = p;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ((PlaylistActivity)getActivity()).onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_playlist_info, container, false);

        loading = new LoadingDialog(getContext());

        playlistCover = view.findViewById(R.id.playlist_img);
        if (playlist.getThumbnail() != null) {
            Picasso.get().load(playlist.getThumbnail()).into(playlistCover);
        }else{
            Picasso.get().load(R.drawable.default_cover).into(playlistCover);
        }
        playlistName = view.findViewById(R.id.playlistName);
        playlistName.setText(playlist.getName());
        playlistArtist = view.findViewById(R.id.ArtistName);
        playlistArtist.setText(playlist.getUserLogin());



        layoutdelete = view.findViewById(R.id.layoutEliminar);
        layoutdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PlaylistActivity)getActivity()).onDelete();
            }
        });

        layoutedit = view.findViewById(R.id.layoutedit);
        layoutedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Session.getInstance(((PlaylistActivity)getActivity()).getApplicationContext()).getUser().getLogin().equals(playlist.getUserLogin())) {
                    dismiss();
                    ((PlaylistActivity)getActivity()).showUIEdit();
                }else{
                    ErrorDialog.getInstance(((PlaylistActivity)getActivity())).showErrorDialog("This playlist is not yours to edit");
                }
            }
        });

        layoutArtist = view.findViewById(R.id.layoutUser);
        layoutDownload = view.findViewById(R.id.layoutDownload);

        layoutShare = view.findViewById(R.id.layoutShare);
        layoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                ShareTrackFragment bottomSheetDialog = new ShareTrackFragment(null, null, playlist);
                bottomSheetDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
                bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "track_info");
            }
        });


        layoutArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Session.getInstance(((PlaylistActivity)getActivity()).getApplicationContext()).getUser().getLogin().equals(playlist.getUserLogin())) {
                    ErrorDialog.getInstance(((PlaylistActivity)getActivity())).showErrorDialog("You cannot check yourself out!");
                }else if(UtilFunctions.noInternet(getActivity())){
                    ErrorDialog.getInstance(((PlaylistActivity)getActivity())).showErrorDialog("You need internet for this feature");

                }else{
                    Intent intent = new Intent(((PlaylistActivity)getActivity()), InfoArtistaActivity.class);
                    intent.putExtra("User", playlist.getOwner());
                    startActivity(intent);
                }
            }
        });
        artistName =  view.findViewById(R.id.text_user);
        artistName.setText("See " + playlist.getUserLogin());

        downloadId = new int [playlist.getTracks().size()*2];
        download = (Switch) view.findViewById(R.id.simpleSwitch);
        if(UtilFunctions.playlistExistsInDatabase(playlist)){
            download.setChecked(true);
        }
        switchState = download.isChecked();
        download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && doChange){
                    loading.showProgressBarDialog("Offline Download", "Downloading "+playlist.getName(),0, playlist.getTracks().size());
                    File path= getActivity().getFilesDir();
                    p = new SavedPlaylist();
                    p.setId(playlist.getId());
                    p.savePlaylist(playlist);
                    if(playlist.getThumbnail()!=null){
                        p.setCoverPath(path.toString() + "/Sallefy/covers/playlists/"+ playlist.getName() + "--" + playlist.getUserLogin()+".jpeg");
                    }else{
                        p.setCoverPath(null);
                    }

                    i=0;
                    if(playlist.getThumbnail()!=null){
                        int id = PRDownloader.download(playlist.getThumbnail(), path.toString() + "/Sallefy/covers/playlists/", playlist.getName() + "--" + playlist.getUserLogin()+".jpeg")
                                .build()
                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        System.out.println("Finished: " + playlist.getTracks().get(i).getName());
                                    }
                                    @Override
                                    public void onError(Error error) {
                                        System.out.println("Error en descarrega");
                                        System.out.println(error.getServerErrorMessage());
                                    }
                                });
                    }
                    try {
                        if(playlist.getTracks().size()>0){
                            doNext();
                        }else{
                            loading.cancelLoadingDialog();
                            ObjectBox.get().boxFor(SavedPlaylist.class).put(p);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(!isChecked && doChange){
                    confirm = new OptionDialog(getContext(), InfoPlaylistFragment.this);
                    confirm.showConfirmationDialog("Are you sure you want to delete your downloads?");
                }
                doChange = true;

            }
        });

        if(!Session.getInstance(getActivity().getApplicationContext()).getUser().getLogin().equals(playlist.getOwner().getLogin()) || UtilFunctions.noInternet(getActivity())){
            layoutedit.setAlpha((float) 0.30);
            layoutedit.setEnabled(false);
            layoutdelete.setAlpha((float) 0.30);
            layoutdelete.setEnabled(false);
        }
        if(UtilFunctions.noInternet(getActivity())){
            layoutDownload.setAlpha((float) 0.30);
            layoutDownload.setEnabled(false);
            download.setEnabled(false);
        }


        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void doNext() throws IOException {
        File path= getActivity().getFilesDir();

        if(!UtilFunctions.trackExistsInDatabase(playlist.getTracks().get(i))){
            SavedTrack t = createTrackDB(path);
            downloadTrack(path, t);
            t = downloadTrackCover(path, t);
            ObjectBox.get().boxFor(SavedTrack.class).attach(t);
            t.playlist.add(p);
            ObjectBox.get().boxFor(SavedPlaylist.class).attach(p);
            p.tracks.add(t);
        }else{
            if(i<playlist.getTracks().size()-1){
                i++;
                try {
                    InfoPlaylistFragment.this.doNext();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                loading.cancelLoadingDialog();
                ObjectBox.get().boxFor(SavedPlaylist.class).put(p);
            }
        }

    }

    private SavedTrack downloadTrackCover(File path, SavedTrack t) {
        if(playlist.getTracks().get(i).getThumbnail()!=null){
            downloadId[(playlist.getTracks().size())+i] = PRDownloader.download(playlist.getTracks().get(i).getThumbnail(), path.toString() + "/Sallefy/covers/tracks/", playlist.getTracks().get(i).getName() + "--" + playlist.getTracks().get(i).getUserLogin()+".jpeg")
                    .build()
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            System.out.println("Finished: " + playlist.getTracks().get(i).getName());
                        }
                        @Override
                        public void onError(Error error) {
                            System.out.println("Error en descarrega");
                            System.out.println(error.getServerErrorMessage());
                        }
                    });
            t.setCoverPath(path.toString() + "/Sallefy/covers/tracks/"+playlist.getTracks().get(i).getName() + "--" + playlist.getTracks().get(i).getUserLogin()+".jpeg");
        }else{
            t.setCoverPath(null);
        }
        return t;
    }

    private void downloadTrack(File path, SavedTrack t) {
        downloadId[i] = PRDownloader.download(playlist.getTracks().get(i).getUrl(), path.toString() + "/Sallefy/tracks/", playlist.getTracks().get(i).getName() + "--" + playlist.getTracks().get(i).getUserLogin())
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        System.out.println("Started: " + playlist.getTracks().get(i).getName());
                    }
                })
                .start(new OnDownloadListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDownloadComplete() {
                        System.out.println("Finished: " + playlist.getTracks().get(i).getName());
                        loading.updateProgress(i, playlist.getTracks().size());
                        if(i<playlist.getTracks().size()-1){
                            i++;
                            try {
                                InfoPlaylistFragment.this.doNext();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            loading.cancelLoadingDialog();
                            ObjectBox.get().boxFor(SavedPlaylist.class).put(p);
                        }
                        ObjectBox.get().boxFor(SavedTrack.class).put(t);
                    }

                    @Override
                    public void onError(Error error) {
                        System.out.println("Error en descarrega");
                        System.out.println(error.getServerErrorMessage());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private SavedTrack createTrackDB(File path){
        SavedTrack t = new SavedTrack();
        t.setId(playlist.getTracks().get(i).getId());
        t.setTrackPath(path.toString() + "/Sallefy/tracks/"+playlist.getTracks().get(i).getName() + "--" + playlist.getTracks().get(i).getUserLogin());
        try {
            t.setTrack(t.saveTrack(playlist.getTracks().get(i)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }


    @Override
    public void onDelete() {

    }

    @Override
    public void onEdit() {

    }

    @Override
    public void onAccept() {
        confirm.cancelDialog();
        deleting = new LoadingDialog(getContext());
        deleting.showLoadingDialog("Deleting playlist from database...");
        UtilFunctions.deletePlaylist(playlist);
        deleting.cancelLoadingDialog();
    }

    @Override
    public void onCancel() {
        confirm.cancelDialog();
        doChange = false;
        download.setChecked(true);
    }
}
