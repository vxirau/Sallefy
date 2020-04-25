package com.prpr.androidpprog2.entregable.controller.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.callbacks.OptionDialogCallback;

public class OptionDialog {

    private static OptionDialog sManager;
    private Object mutex = new Object();

    private Context mContext;
    private Dialog mDialog;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private ImageView ivIcon;
    private Button btnAccept;
    private Button btnDelete;
    private OptionDialogCallback dialogCallback;

    public static OptionDialog getInstance(Context context) {
        if (sManager == null) {
            sManager = new OptionDialog(context);
        }
        return sManager;
    }

    public OptionDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext);
        dialogCallback = (OptionDialogCallback) context;
    }

    public OptionDialog(Context context, OptionDialogCallback call) {
        mContext = context;
        mDialog = new Dialog(mContext);
        dialogCallback = call;
    }

    public void showOptionDialog(String b1, String b2) {
        mDialog.setContentView(R.layout.dialog_option);
        mDialog.setCanceledOnTouchOutside(false);


        tvTitle = (TextView) mDialog.findViewById(R.id.dialog_title);
        tvSubtitle = (TextView) mDialog.findViewById(R.id.dialog_error_subtitle);
        ivIcon = (ImageView) mDialog.findViewById(R.id.dialog_error_icon);

        btnAccept = (Button) mDialog.findViewById(R.id.dialog_edit);
        btnAccept.setText(b1);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCallback.onEdit();
            }
        });

        btnDelete = (Button) mDialog.findViewById(R.id.dialog_delete);
        btnDelete.setText(b2);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCallback.onDelete();
            }
        });
        mDialog.show();
    }

    public void cancelDialog(){
        mDialog.cancel();
    }


    public void showConfirmationDialog(String message) {
        mDialog.setContentView(R.layout.dialog_option);
        mDialog.setCanceledOnTouchOutside(false);

        tvTitle = (TextView) mDialog.findViewById(R.id.dialog_title);
        tvSubtitle = (TextView) mDialog.findViewById(R.id.dialog_error_subtitle);
        ivIcon = (ImageView) mDialog.findViewById(R.id.dialog_error_icon);
        tvTitle.setText("Careful!");
        tvSubtitle.setText(message);
        btnAccept = (Button) mDialog.findViewById(R.id.dialog_edit);
        btnAccept.setText("Confirm");
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCallback.onAccept();
            }
        });

        btnDelete = (Button) mDialog.findViewById(R.id.dialog_delete);
        btnDelete.setText("Cancel");
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCallback.onCancel();
            }
        });
        mDialog.show();
    }




}
