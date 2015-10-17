package com.venomvendor.dailyhunt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.venomvendor.dailyhunt.R;
import com.venomvendor.dailyhunt.adapter.HomeAdapter;
import com.venomvendor.dailyhunt.core.DHApplication;
import com.venomvendor.dailyhunt.model.Article;
import com.venomvendor.dailyhunt.util.Constants;

import java.util.ArrayList;
import java.util.List;

import vee.android.lib.SimpleSharedPreferences;

public class BookMarkActivity extends BaseActivity {

    private static final String TAG = BookMarkActivity.class.getSimpleName();

    protected RecyclerView mArticleView;
    protected RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Article> cacheArticles = new ArrayList<>();
    private List<Article> mBookMarkedArticles = new ArrayList<>();

    SimpleSharedPreferences mPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = DHApplication.getSharedPreferences();
        cacheArticles = (List<Article>) getIntent().getSerializableExtra(Constants.ARTICLES);
        initLocalViews();
    }

    @Override
    protected int contentView() {
        return R.layout.activity_bm;
    }


    @Override
    protected boolean isDrawerEnabled() {
        return false;
    }


    private void initLocalViews() {
        initArticleView();
        sendDataRequest();
    }

    private List<Article> filterBookMark() {
        mBookMarkedArticles = new ArrayList<>();
        for (Article article : cacheArticles) {
            final String key = String.format("%s%s", Constants.FAV, article.getUrl());
            if (mPref.getBoolean(key, false)) {
                mBookMarkedArticles.add(article);
            }
        }
        return mBookMarkedArticles;
    }


    private void initArticleView() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mArticleView = (RecyclerView) findViewById(R.id.article_view);
        mLayoutManager = new LinearLayoutManager(this);
        mArticleView.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendDataRequest();
            }
        });

        mNav.setText("Bookmarks");
        mNav.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark, 0, 0, 0);

    }

    private void sendDataRequest() {
        if (filterBookMark().isEmpty()) {
            showToast("No Bookmarks/Favorite available.");
            finish();
        } else {
            updateAdapter(new HomeAdapter(this, filterBookMark()));
        }
    }

    private void updateAdapter(final HomeAdapter adapter) {
        mArticleView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(BookMarkActivity.this, ReadingActivity.class);
                intent.putExtra(Constants.CATEGORY, mBookMarkedArticles.get(position));
                startActivity(intent);
            }
        });
    }

    public void goBack(View v) {
        super.onBackPressed();
    }
}
