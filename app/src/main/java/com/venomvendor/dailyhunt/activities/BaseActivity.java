package com.venomvendor.dailyhunt.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.venomvendor.dailyhunt.core.DHApplication;
import com.venomvendor.dailyhunt.model.ApiHits;
import com.venomvendor.dailyhunt.model.GetPosts;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import vee.android.lib.SimpleSharedPreferences;

public class BaseActivity extends AppCompatActivity {

    private final String TAG = BaseActivity.class.getSimpleName();
    private final SparseArrayCompat<AlertDialog> mDialogs = new SparseArrayCompat<>(1);
    protected SimpleSharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerBus();
        super.onCreate(savedInstanceState);
        mPrefs = DHApplication.getSharedPreferences();
    }

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


    @CallSuper
    @Subscribe
    public void onEventApiCount(ApiHits apiHits) {
        if (apiHits.isSuccess()) {
            Log.d(TAG, "posts.getPosts():" + apiHits.getApiHits());
        } else {
            showToast(apiHits.getError());
        }
    }

    @CallSuper
    @Subscribe
    public void onEventPosts(GetPosts posts) {
        if (posts.isSuccess()) {
            Log.d(TAG, "posts.getPosts():" + posts.getArticles().size());
        } else {
            showToast(posts.getError());
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerBus();
    }

    @Override
    protected void onStop() {
        unregisterBus();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterBus();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerBus();
    }

    public EventBus getBus() {
        return EventBus.getDefault();
    }

    public void registerBus() {
        if (!getBus().isRegistered(this)) {
            getBus().register(this);
        }
    }

    public void unregisterBus() {
        getBus().unregister(this);
    }

    protected void showToast(String msg) {
        if (msg == null) {
            msg = "Unknown Error.";
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
