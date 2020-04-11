package com.prpr.androidpprog2.entregable.controller.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.activities.LoginActivity;
import com.prpr.androidpprog2.entregable.controller.activities.SettingsActivity;
import com.prpr.androidpprog2.entregable.controller.callbacks.LogOutCallback;
import com.prpr.androidpprog2.entregable.controller.callbacks.OptionDialogCallback;
import com.prpr.androidpprog2.entregable.utils.Session;

public class LogOutDialog {

    private static LogOutDialog sManager;
    private static Object mutex = new Object();

    private LogOutCallback logOutCallback;

    private Context mContext;
    private Dialog mDialog;



    private Button btnAccept;
    private Button btnCancel;
    public static LogOutDialog getInstance(Context context) {
        if (sManager == null) {
            sManager = new LogOutDialog(context);
        }
        return sManager;
    }

    private LogOutDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext);
        logOutCallback = (LogOutCallback) context;
    }

    public void showStateDialog() {
        mDialog.setContentView(R.layout.dialog_logout_accept);
        mDialog.setCanceledOnTouchOutside(false);

        btnAccept = (Button) mDialog.findViewById(R.id.dialog_error_button_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutCallback.doLogOut();
                mDialog.cancel();
            }
        });
        btnCancel = (Button) mDialog.findViewById(R.id.dialog_cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.cancel();
            }
        });

        mDialog.show();
    }


}
