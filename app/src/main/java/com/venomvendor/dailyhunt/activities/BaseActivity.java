package com.venomvendor.dailyhunt.activities;

import android.content.DialogInterface;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private final String TAG = BaseActivity.class.getSimpleName();

    SparseArrayCompat<AlertDialog> mDialogs = new SparseArrayCompat<>(1);

    protected void showCustomDialog(String title, String msg,
                                    final DialogInterface.OnClickListener listener) {
        showCustomDialog(title, msg, listener, null);
    }

    protected void showCustomDialog(String title, String msg,
                                    DialogInterface.OnClickListener listener,
                                    DialogInterface.OnClickListener cancelListener) {
        while (mDialogs.size() > 0) {
            mDialogs.valueAt(0).dismiss();
            mDialogs.removeAt(0);
        }

        if (listener == null) {
            throw new NullPointerException("listener cannot be null");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", listener);
        if (cancelListener != null) {
            builder.setNegativeButton("CANCEL", cancelListener);
        }

        final AlertDialog dialog = builder.create();
        mDialogs.put(0, dialog);
        dialog.show();
    }

}
