package com.prpr.androidpprog2.entregable.controller.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.prpr.androidpprog2.entregable.R;

import org.w3c.dom.Text;

public class LoadingDialog {

    private static LoadingDialog sManager;
    private Object mutex = new Object();

    private Context mContext;
    private Dialog mDialog;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private ProgressBar loading;
    private ProgressBar bar;
    private LinearLayout linearLoading;
    private TextView totalDownloaded;

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
        linearLoading= mDialog.findViewById(R.id.linearProgress);
        linearLoading.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        mDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showProgressBarDialog(String title, String message, int actual , int total) {
        mDialog.setContentView(R.layout.dialog_loading);
        mDialog.setCanceledOnTouchOutside(false);

        tvTitle = (TextView) mDialog.findViewById(R.id.dialog_title);
        tvTitle.setText(title);
        tvSubtitle = (TextView) mDialog.findViewById(R.id.dialog_subtitle);
        tvSubtitle.setText(message);
        loading = mDialog.findViewById(R.id.progress);
        loading.setVisibility(View.GONE);
        linearLoading= mDialog.findViewById(R.id.linearProgress);
        linearLoading.setVisibility(View.VISIBLE);
        totalDownloaded= mDialog.findViewById(R.id.totalDownloaded);
        bar= mDialog.findViewById(R.id.bar);
        bar.setMax(total);
        totalDownloaded.setText(actual+"/"+total);
        mDialog.show();
    }

    public void updateProgress(int progress, int total){
        bar.setProgress(progress, true);
        totalDownloaded.setText(progress+"/"+total);
    }


}
