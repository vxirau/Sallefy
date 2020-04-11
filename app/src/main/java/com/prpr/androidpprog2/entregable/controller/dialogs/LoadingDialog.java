package com.prpr.androidpprog2.entregable.controller.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prpr.androidpprog2.entregable.R;

public class LoadingDialog {

    private static LoadingDialog sManager;
    private Object mutex = new Object();

    private Context mContext;
    private Dialog mDialog;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private ProgressBar loading;

    public static LoadingDialog getInstance(Context context) {
        if (sManager == null) {
            sManager = new LoadingDialog(context);
        }
        return sManager;
    }

    public LoadingDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext);
    }

    public void cancelLoadingDialog(){
        mDialog.cancel();
    }


    public void showLoadingDialog(String message) {
        mDialog.setContentView(R.layout.dialog_loading);
        mDialog.setCanceledOnTouchOutside(false);

        tvTitle = (TextView) mDialog.findViewById(R.id.dialog_title);
        tvTitle.setText("Loading");
        tvSubtitle = (TextView) mDialog.findViewById(R.id.dialog_subtitle);
        tvSubtitle.setText(message);
        loading = mDialog.findViewById(R.id.progress);

        mDialog.show();
    }
}
