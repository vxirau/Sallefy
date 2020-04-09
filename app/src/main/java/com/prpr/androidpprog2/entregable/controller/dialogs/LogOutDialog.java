package com.prpr.androidpprog2.entregable.controller.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.prpr.androidpprog2.entregable.R;

public class LogOutDialog {

    private static LogOutDialog sManager;
    private static Object mutex = new Object();

    private Context mContext;
    private Dialog mDialog;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private ImageView ivIcon;
    private ImageView ivLike;
    private ImageView ivFollow;
    private Button btnAccept;

    public static LogOutDialog getInstance(Context context) {
        if (sManager == null) {
            sManager = new LogOutDialog(context);
        }
        return sManager;
    }

    private LogOutDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext);
    }

    public void showStateDialog(boolean completed) {
        mDialog.setContentView(R.layout.dialog_logout_accept);
        mDialog.setCanceledOnTouchOutside(false);

        tvTitle = (TextView) mDialog.findViewById(R.id.dialog_state_title);
        tvSubtitle = (TextView) mDialog.findViewById(R.id.dialog_state_subtitle);
        ivIcon = (ImageView) mDialog.findViewById(R.id.dialog_state_icon);
        btnAccept = (Button) mDialog.findViewById(R.id.dialog_state_button);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

        if ((completed)) {
            completedTask();
        } else {
            inProgressTask();
        }
        mDialog.show();
    }


    private void inProgressTask() {
        tvTitle.setText(R.string.state_wait);
        tvSubtitle.setText(R.string.state_task_in_progress);
        ivIcon.setImageDrawable(mContext.getDrawable(R.drawable.info_dialog_icon));
        btnAccept.setVisibility(View.GONE);
    }

    private void completedTask() {
        tvTitle.setText(R.string.state_success_title);
        tvSubtitle.setText(R.string.state_task_completed);
        //btnAccept.setBackground(mContext.getDrawable(R.drawable.backgr_blue_radius));
        btnAccept.setVisibility(View.VISIBLE);
    }

    public boolean isDialogShown() {
        return mDialog.isShowing();
    }
}
