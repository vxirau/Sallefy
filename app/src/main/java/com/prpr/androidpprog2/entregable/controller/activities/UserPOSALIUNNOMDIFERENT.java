package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.prpr.androidpprog2.entregable.R;
public class UserPOSALIUNNOMDIFERENT extends AppCompatActivity implements UserCommunicationInterface{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

    }

    @Override
    public void userMenu(int buttonPressed) {
        Intent intent = new Intent(this, UserBodyActivity.class);
        intent.putExtra("Button Pressed", buttonPressed);
        startActivity(intent);
    }
}
