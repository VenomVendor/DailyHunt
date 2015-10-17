package com.venomvendor.dailyhunt.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.venomvendor.dailyhunt.R;
import com.venomvendor.dailyhunt.core.DHApplication;
import com.venomvendor.dailyhunt.model.ApiHits;
import com.venomvendor.dailyhunt.model.GetPosts;
import com.venomvendor.dailyhunt.network.Connection;
import com.venomvendor.dailyhunt.network.NetworkHandler;
import com.venomvendor.dailyhunt.util.DHHelper;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import vee.android.lib.SimpleSharedPreferences;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final SparseArrayCompat<AlertDialog> mDialogs = new SparseArrayCompat<>(1);
    protected SimpleSharedPreferences mPrefs;

    protected TextView mLogo;
    protected TextView mNav;
    protected FrameLayout mContentHolder;

    protected abstract boolean isDrawerEnabled();

    @LayoutRes
    protected abstract int contentView();

    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBus();
        mPrefs = DHApplication.getSharedPreferences();
        setContentView(baseView());
        initViews();
        if (!Connection.isAvail()) {
            showCustomDialog("No Internet", "Data connectivity is un available.",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
    }

    @LayoutRes
    private int baseView() {
        return R.layout.activity_base;
    }

    private void initViews() {

        if (contentView() == baseView()) {
            throw new IllegalArgumentException("Do not include baseView() in contentView()," +
                    "since it can cause cyclic includes");
        }

        initHolder();
        initFAB();
        initDrawer();
    }

    private void initHolder() {
        mContentHolder = (FrameLayout) findViewById(R.id.content_holder);
        mContentHolder.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(contentView(), mContentHolder, true);
    }

    private void initFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.setVisibility(View.GONE); //TODO-Remove if not required.
    }

    private void initDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLogo = (TextView) toolbar.findViewById(R.id.action_bar_logo);
        mNav = (TextView) toolbar.findViewById(R.id.action_bar_nav);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle.setDrawerIndicatorEnabled(isDrawerEnabled());
        drawer.setEnabled(isDrawerEnabled());
        if (!isDrawerEnabled()) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        if (id == R.id.nav_manage) {
            showToast("Show Settings");
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Demo for Daily Hunt, by VenomVendor™");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Send to"));
        } else if (id == R.id.nav_about) {
            showToast("Demo for Daily Hunt \n Version 1.0.0-beta");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    @Subscribe(sticky = true)
    public void onEventApiCount(ApiHits apiHits) {
        if (!apiHits.isSuccess()) {
            showToast(apiHits.getError());
        }
    }

    @CallSuper
    @Subscribe
    public void onEventPosts(GetPosts posts) {
        if (posts.isSuccess()) {
            DHHelper.removeRetry();
        } else {
            if (DHHelper.hasRetriesLeft()) {
                //RetryHere.
                DHHelper.incrementRetry();
                NetworkHandler.getInstance().getPosts();
            } else {
                showToast(posts.getError());
            }
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

    private EventBus getBus() {
        return EventBus.getDefault();
    }

    @CallSuper
    public void registerBus() {
        if (!getBus().isRegistered(this)) {
            getBus().register(this);
        }
    }

    @CallSuper
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
