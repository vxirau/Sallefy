package com.prpr.androidpprog2.entregable.controller.activities;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.os.IBinder;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.callbacks.LogOutCallback;
import com.prpr.androidpprog2.entregable.controller.dialogs.ErrorDialog;
import com.prpr.androidpprog2.entregable.controller.dialogs.LoadingDialog;
import com.prpr.androidpprog2.entregable.controller.dialogs.LogOutDialog;
import com.prpr.androidpprog2.entregable.controller.dialogs.StateDialog;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.UserManager;
import com.prpr.androidpprog2.entregable.controller.music.ReproductorService;
import com.prpr.androidpprog2.entregable.model.DB.ObjectBox;
import com.prpr.androidpprog2.entregable.model.DB.SavedCache;
import com.prpr.androidpprog2.entregable.model.DB.SavedPlaylist;
import com.prpr.androidpprog2.entregable.model.DB.SavedTrack;
import com.prpr.androidpprog2.entregable.model.DB.UtilFunctions;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.passwordChangeDto;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;
import com.prpr.androidpprog2.entregable.utils.Session;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends AppCompatActivity implements UserCallback, LogOutCallback {


    private static final int UPLOAD_IMAGE = 1;



    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;

    private EditText oldPassword;
    private EditText newPassword;
    private Button openChangePassword;
    private Button closeChangePassword;
    private LinearLayout passwordLayout;
    private boolean passwordChanged;

    private ImageButton imgBtnUserPic;

    private Button btnUpdate;
    private Button btnLogOut;

    private Boolean pictureUpdated;

    private ScrollView settingsScrollView;
    private User myUser;

    private UserManager userManager;

    private LoginActivity LoginActivity;
    private Context context;
    private TextView username;
    private Button followers;

    private ArrayList<User> followerList;


    //----------------------------------------------------------------PART DE SERVICE--------------------------------------------------------------------------------
    private TextView trackTitle;
    private TextView followingTxt;
    private TextView trackAuthor;
    private SeekBar mSeekBar;
    private Button play;
    private Button pause;
    private Button btnBack;
    private ImageView im;
    private LinearLayout playing;
    private ReproductorService serv;
    private LoadingDialog loading;
    private boolean servidorVinculat=false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ReproductorService.LocalBinder binder = (ReproductorService.LocalBinder) service;
            serv = binder.getService();
            context = getApplicationContext();
            //serv.setmSeekBar(mSeekBar);
            servidorVinculat = true;
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            servidorVinculat = false;
        }
    };


    void doUnbindService() {
        if (servidorVinculat) {
            unbindService(serviceConnection);
            servidorVinculat = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }


    @Override
    public void onStart() {
        super.onStart();
        if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.updateUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!servidorVinculat){
            Intent intent = new Intent(this, ReproductorService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            serv.setUIControls(mSeekBar, trackTitle, trackAuthor, play, pause, im);
            serv.updateUI();
        }
    }





    //----------------------------------------------------------------FIN DE LA PART DE SERVICE--------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        myUser = Session.getUser();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        UserManager.getInstance(getApplicationContext()).getFollowers(myUser.getLogin(), this);
        initViews();

    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case UPLOAD_IMAGE:
                    Uri selectedImage = data.getData();
                    imgBtnUserPic.setImageURI(selectedImage);
                    pictureUpdated = true;
                    break;
            }
    }

    void initViews(){

        loading = new LoadingDialog(this);

        play = findViewById(R.id.playButton);
        play.setEnabled(true);
        play.bringToFront();
        play.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                serv.resumeMedia();
            }
        });
        pictureUpdated = false;
        pause = findViewById(R.id.playPause);
        pause.setEnabled(true);
        pause.bringToFront();
        pause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                serv.pauseMedia();
            }
        });

        trackAuthor = findViewById(R.id.dynamic_artist);
        trackAuthor.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        trackAuthor.setSelected(true);
        trackAuthor.setSingleLine(true);
        trackTitle = findViewById(R.id.dynamic_title);
        trackTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        trackTitle.setSelected(true);
        trackTitle.setSingleLine(true);
        mSeekBar = (SeekBar) findViewById(R.id.dynamic_seekBar);

        playing = findViewById(R.id.reproductor);
        playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(serv!=null && !trackTitle.getText().toString().equals("")){
                    Intent intent = new Intent(getApplicationContext(), ReproductorActivity.class);
                    startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                }else{
                    ErrorDialog.getInstance(SettingsActivity.this).showErrorDialog("You haven't selected a song yet!");
                }
            }
        });

        passwordLayout  = findViewById(R.id.passwordLayout);
        passwordLayout.setVisibility(View.GONE);
        openChangePassword = findViewById(R.id.changePasswordBtn);
        openChangePassword.setVisibility(View.VISIBLE);
        openChangePassword.setEnabled(true);
        openChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordChanged = true;
                passwordLayout.setVisibility(View.VISIBLE);
                closeChangePassword.setVisibility(View.VISIBLE);
                openChangePassword.setVisibility(View.GONE);
            }
        });
        closeChangePassword = findViewById(R.id.backBtn);
        closeChangePassword.setVisibility(View.GONE);
        closeChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordChanged = false;
                closeChangePassword.setVisibility(View.GONE);
                openChangePassword.setVisibility(View.VISIBLE);
                passwordLayout.setVisibility(View.GONE);
            }
        });
        oldPassword = findViewById(R.id.text_settingsCurrentPassword);
        newPassword = findViewById(R.id.text_settingsNewPassword);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.menu);
        navigation.setSelectedItemId(R.id.perfil);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.buscar:
                        Intent intent2 = new Intent(getApplicationContext(), SearchActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent2, Constants.NETWORK.LOGIN_OK);
                        return true;
                    case R.id.perfil:
                        return true;
                }
                return false;
            }
        });



        btnBack = findViewById(R.id.back2User);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.nothing,R.anim.nothing);
            }
        });


        username = findViewById(R.id.settings_update_profile_pic);
        username.setText(myUser.getLogin());

        followers = findViewById(R.id.numFollowers);
        followers.setText(myUser.getFollowers() +" Followers");
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);
                intent.putExtra("followers", SettingsActivity.this.followerList);
                startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });


        etFirstName = (EditText) findViewById(R.id.textview_settings_change_first_name);
        if(myUser.getFirstName()!=null){
            etFirstName.setText(myUser.getFirstName());
        }

        etLastName = (EditText) findViewById(R.id.textview_settings_change_last_name);
        if(myUser.getLastName()!=null){
            etLastName.setText(myUser.getLastName());
        }
        etEmail = (EditText) findViewById(R.id.textview_settings_change_email);
        if(myUser.getEmail()!=null){
            etEmail.setText(myUser.getEmail());
        }
        btnUpdate = findViewById(R.id.update_button);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(UtilFunctions.noInternet(getApplicationContext())){
                    ErrorDialog.getInstance(SettingsActivity.this).showErrorDialog("You have no internet connection!");
                }else{
                    loading.showLoadingDialog("Updating user");
                    if(passwordChanged){
                        doUpdatePassword();
                    }
                    try {
                        doUpdateUser();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        btnLogOut =  (Button) findViewById(R.id.log_out_button);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LogOutDialog.getInstance(SettingsActivity.this).showStateDialog();

            }
        });





        imgBtnUserPic = findViewById(R.id.userImage);

        if(myUser.getImageUrl()!=null){
            Picasso.get().load(myUser.getImageUrl()).into(imgBtnUserPic);
        }else{
            Picasso.get().load(R.drawable.default_user_cover).into(imgBtnUserPic);
        }

        imgBtnUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();

            }
        });

        settingsScrollView = findViewById(R.id.settings_scrollview);

    }

    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, UPLOAD_IMAGE);
    }


    private void doUpdateUser() throws MalformedURLException {


        if(etFirstName.getText().length() > 0 || etFirstName.getText() != null){
            this.myUser.setFirstName(etFirstName.getText().toString());
        }
        if(etLastName.getText().length() > 0 || etLastName.getText() != null){
            this.myUser.setLastName(etLastName.getText().toString());
        }
        if(etEmail.getText().length() > 0 || etEmail.getText() != null){
            this.myUser.setEmail(etEmail.getText().toString());
        }
        if(pictureUpdated){
            this.myUser.setImageUrl(imgBtnUserPic.toString());
        }

        userManager = new UserManager(this);
        userManager.saveAccount(myUser, this);

    }


    private void doUpdatePassword(){
        if(oldPassword.getText().length() > 0 && newPassword.getText().length() > 0){
            passwordChangeDto pd = new passwordChangeDto(oldPassword.getText().toString(), newPassword.getText().toString());
            userManager = new UserManager(this);
            userManager.updatePassword(pd, this);
        }
    }



    @Override
    public void onUserInfoReceived(User userData) {

        this.myUser = userData;


    }

    @Override
    public void onUserUpdated(User body) {
        finish();
    }

    @Override
    public void onAccountSaved(User body) {
        loading.cancelLoadingDialog();
        StateDialog.getInstance(this).informTask("Great", "Update succesful!");
    }


    @Override
    public void onTopUsersRecieved(List<User> body) {

    }

    @Override
    public void onUserUpdateFailure(Throwable throwable) {

    }

    @Override
    public void onUserSelected(User user) {

    }

    @Override
    public void onAllUsersSuccess(List<User> users) {

    }

    @Override
    public void onFollowedUsersSuccess(List<User> users) {

    }

    @Override
    public void onAllUsersFail(Throwable throwable) {

    }

    @Override
    public void onFollowedUsersFail(Throwable throwable) {

    }

    @Override
    public void onFollowSuccess(Follow body) {

    }

    @Override
    public void onAccountSavedFailure(Throwable throwable) {
        loading.cancelLoadingDialog();
        StateDialog.getInstance(this).informTask("Warning", "There has been a problem updating user");
    }

    @Override
    public void onFollowFailure(Throwable throwable) {

    }

    @Override
    public void onCheckSuccess(Follow body) {

    }

    @Override
    public void onCheckFailure(Throwable throwable) {

    }

    @Override
    public void onTopUsersFailure(Throwable throwable) {

    }

    @Override
    public void onFollowedUsersFailure(Throwable t) {

    }

    @Override
    public void onFollowersRecieved(ArrayList<User> body) {
        this.followerList = body;
    }

    @Override
    public void onFollowersFailed(Throwable throwable) {

    }

    @Override
    public void onFollowersFailure(Throwable throwable) {

    }

    @Override
    public void onPasswordUpdated(passwordChangeDto pd) {
        Toast.makeText(getApplicationContext(),"Password updated!", Toast.LENGTH_SHORT).show();
        SavedCache c = ObjectBox.get().boxFor(SavedCache.class).get(1);
        c.setPassword(pd.getNewPassword());
        ObjectBox.get().boxFor(SavedCache.class).put(c);
        final SharedPreferences prefs = getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("password", pd.getNewPassword());
        editor.commit();
    }

    @Override
    public void onPasswordUpdatedFailure(Throwable throwable) {
        Toast.makeText(getApplicationContext(),"Password couldn't be updated!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSallefySectionRecieved(List<User> body, boolean recieved) {

    }

    @Override
    public void onSallefySectionFailure(Throwable throwable) {

    }


    @Override
    public void onFailure(Throwable throwable) {

    }

    @Override
    public void doLogOut() {
        Toast.makeText(SettingsActivity.this, "You logged out succesfully", Toast.LENGTH_SHORT).show();
        Session.getInstance(this).resetValues();
        ObjectBox.get().boxFor(SavedTrack.class).removeAll();
        ObjectBox.get().boxFor(SavedPlaylist.class).removeAll();

        SharedPreferences preferences = getSharedPreferences("RememberMe",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        finish();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("LoggedOut", true);
        startActivity(intent);
    }
}

