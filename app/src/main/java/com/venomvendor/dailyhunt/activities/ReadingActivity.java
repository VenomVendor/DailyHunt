package com.venomvendor.dailyhunt.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.venomvendor.dailyhunt.R;
import com.venomvendor.dailyhunt.core.DHApplication;
import com.venomvendor.dailyhunt.model.Article;
import com.venomvendor.dailyhunt.model.GetPosts;
import com.venomvendor.dailyhunt.network.NetworkHandler;
import com.venomvendor.dailyhunt.util.Constants;
import com.venomvendor.dailyhunt.util.DHHelper;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import vee.android.lib.SimpleSharedPreferences;

public class ReadingActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton mFav;
    private Article mArticle;
    private SimpleSharedPreferences mPref;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        mPref = DHApplication.getSharedPreferences();

        Bundle bundle = getIntent().getExtras();
        mArticle = bundle.getParcelable(Constants.CATEGORY);
        if (mArticle == null) {
            finish();
            return;
        }

        initActionBar();

        initContent();

        NetworkHandler.getInstance().getPosts();

        initFAB();

        updateFavIcon();
    }

    private void initFAB() {
        mFav = (FloatingActionButton) findViewById(R.id.action_fav);
        FloatingActionButton mLink = (FloatingActionButton) findViewById(R.id.action_link);
        FloatingActionButton mShare = (FloatingActionButton) findViewById(R.id.action_share);

        mFav.setOnClickListener(this);
        mLink.setOnClickListener(this);
        mShare.setOnClickListener(this);
    }

    private void initContent() {
        TextView contentTitle = (TextView) findViewById(R.id.reading_content_title);
        TextView mFlashNews = (TextView) findViewById(R.id.flash_news);
        mFlashNews.setText(null);

        contentTitle.setText(String.format("%s under %s by %s", mArticle.getTitle(),
                mArticle.getCategory(), mArticle.getSource()));

        TextView content = (TextView) findViewById(R.id.reading_content);
        String largeData = mArticle.getContent();
        StringBuilder stringBuilder = new StringBuilder(largeData);
        for (int i = 0; i < 10; i++) {
            stringBuilder = stringBuilder.append("<br><br>\t\t").append(largeData);
        }
        content.setText(Html.fromHtml(stringBuilder.toString()));

        ImageView backDrop = (ImageView) findViewById(R.id.backdrop);
        Uri imgUri = Uri.parse(mArticle.getImage());
        Glide.with(this)
                .load(imgUri)
                .fitCenter()
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(backDrop);
    }

    private void initActionBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        final String title = mArticle.getCategory();
        collapsingToolbar.setTitle(title);
    }

    private void updateFavIcon() {
        final String key = String.format("%s%s", Constants.FAV, mArticle.getUrl());
        boolean isFav = mPref.getBoolean(key, false);
        if (isFav) {
            mFav.setIcon(R.drawable.ic_favorite);
            mFav.setTitle("Un Favorite?");
        } else {
            mFav.setIcon(R.drawable.ic_un_favorite);
            mFav.setTitle("Favorite?");
        }
    }

    private void doFavUnFav() {
        final String key = String.format("%s%s", Constants.FAV, mArticle.getUrl());
        boolean isFav = mPref.getBoolean(key, false);
        mPref.putBoolean(key, !isFav);
        updateFavIcon();
    }

    private void openLink() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mArticle.getUrl()));
        startActivity(intent);
    }

    private void shareArticle() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                String.format("%s Read more at %s", mArticle.getTitle(), mArticle.getUrl()));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Send via..."));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_fav) {
            doFavUnFav();
        } else if (v.getId() == R.id.action_link) {
            openLink();
        } else if (v.getId() == R.id.action_share) {
            shareArticle();
        }
    }

    @CallSuper
    @Subscribe
    public void onEventPosts(GetPosts posts) {
        if (posts.isSuccess()) {
            DHHelper.removeRetry();
        } else if (DHHelper.hasRetriesLeft()) {
            //RetryHere.
            DHHelper.incrementRetry();
            NetworkHandler.getInstance().getPosts();
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

}
