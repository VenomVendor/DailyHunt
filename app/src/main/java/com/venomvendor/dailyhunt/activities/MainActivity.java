package com.venomvendor.dailyhunt.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.venomvendor.dailyhunt.R;
import com.venomvendor.dailyhunt.adapter.HomeAdapter;
import com.venomvendor.dailyhunt.model.ApiHits;
import com.venomvendor.dailyhunt.model.Article;
import com.venomvendor.dailyhunt.model.GetPosts;
import com.venomvendor.dailyhunt.network.Connection;
import com.venomvendor.dailyhunt.network.NetworkHandler;
import com.venomvendor.dailyhunt.util.AppUtils;
import com.venomvendor.dailyhunt.util.Constants;
import com.venomvendor.dailyhunt.util.DHHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int RUNTIME_PERMISSIONS_CODE = 255;
    private static final int UPDATE_PERMISSIONS = 254;
    private final String[] requiredPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Spinner mFilter;
    private TextView mFeedCount;
    private TextView mApiCount;

    private RecyclerView mArticleView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Article> cacheArticles = new ArrayList<>();
    private List<String> mCategories = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLocalViews();

        if (isPermissionGranted()) {
            sendDataRequest();
        } else {
            showPermissions();
        }

    }

    @Override
    protected int contentView() {
        return R.layout.activity_main;
    }


    @Override
    protected boolean isDrawerEnabled() {
        return true;
    }


    private void initLocalViews() {

        initSearchViews();

        initFilter();

        initArticleView();

    }

    private void initSearchViews() {
        final EditText search = (EditText) findViewById(R.id.search);
        final ImageView clear = (ImageView) findViewById(R.id.search_remove);

        search.setCursorVisible(false);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!AppUtils.isEmpty(s)) {
                    doFilter(s.toString());
                } else {
                    setOldData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                clear.setVisibility(AppUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });


        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH ||
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ||
                        actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search.setCursorVisible(false);
                    DHHelper.hideKeyboard(MainActivity.this);
                    return true;
                }
                return false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setCursorVisible(true);
            }
        });

    }

    private void setOldData() {
        updateAdapter(new HomeAdapter(this, cacheArticles));
    }

    private void doFilter(String query) {
        //TODO-Animate WhileAdding.
        updateAdapter(new HomeAdapter(this, filter(cacheArticles, query.toLowerCase().trim())));
    }

    private List<Article> filter(List<Article> articles, String query) {
        final List<Article> filteredArticles = new ArrayList<>();
        for (Article article : articles) {
            final String title = article.getTitle().toLowerCase();
            final String source = article.getSource().toLowerCase();
            final String category = article.getCategory().toLowerCase();
            final String content = article.getContent().toLowerCase();
            if (title.contains(query) | source.contains(query) |
                    category.contains(query) | content.contains(query)) {
                filteredArticles.add(article);
            }
        }
        return filteredArticles;
    }


    private List<Article> filterCategory(List<Article> articles, String query) {
        final List<Article> filteredArticles = new ArrayList<>();
        for (Article article : articles) {
            final String category = article.getCategory();
            if (category.equalsIgnoreCase(query)) {
                filteredArticles.add(article);
            }
        }
        return filteredArticles;
    }

    private void initFilter() {
        Button openBookmarks = (Button) findViewById(R.id.main_open_bookmarks);
        openBookmarks.setOnClickListener(this);

        mFilter = (Spinner) findViewById(R.id.main_select_category);
        mFeedCount = (TextView) findViewById(R.id.main_feed_count);
        mApiCount = (TextView) findViewById(R.id.main_api_count);

        mCategories.add("Please wait...");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mCategories);
        mFilter.setAdapter(adapter);
        mFilter.setEnabled(false);
        mFilter.setOnItemSelectedListener(this);
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
    }

    private void sendDataRequest() {
        if (!Connection.isAvail()) {
            mSwipeRefreshLayout.setRefreshing(false);
            showCustomDialog("No Internet", "Data connectivity is un available.",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            return;
        }
        mSwipeRefreshLayout.setRefreshing(true);

        mCategories.clear();
        mCategories.add("Please wait...");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mCategories);
        mFilter.setAdapter(adapter);
        mFilter.setEnabled(false);

        NetworkHandler.getInstance().getPosts();
        NetworkHandler.getInstance().getApiCount();
    }

    @Override
    public void onEventApiCount(ApiHits apiHits) {
        super.onEventApiCount(apiHits);
        if (apiHits.isSuccess()) {
            mApiCount.setText(String.format(Locale.getDefault(), "API Hits : %s",
                    apiHits.getApiHits()));
            int color = mApiCount.getCurrentTextColor() == getResources().getColor(R.color.colorPrimary)
                    ? getResources().getColor(R.color.colorAccent) : getResources().getColor(R.color.colorPrimary);
            mApiCount.setTextColor(color);
            ScaleAnimation animation = new ScaleAnimation(1.5f, 1.0f, 0.5f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(200);
            animation.setFillAfter(true);
            mApiCount.startAnimation(animation);
        }
    }

    @Override
    public void onEventPosts(GetPosts posts) {
        super.onEventPosts(posts);
        mSwipeRefreshLayout.setRefreshing(false);
        if (posts.isSuccess()) {
            populateData(posts.getArticles());
        }
    }

    private void populateData(List<Article> articles) {
        HashSet<String> uniqueCategory = new HashSet<>();
        uniqueCategory.add(" All ");
        for (Article article : articles) {
            uniqueCategory.add(article.getCategory());
        }
        mCategories = new ArrayList<>(uniqueCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mCategories);
        mFilter.setAdapter(adapter);
        mFilter.setEnabled(true);

        mFeedCount.setText(String.format(Locale.getDefault(), "Feed Source : %s",
                mCategories.size() - 1));

        CustomAnimator customAnimator = new CustomAnimator();
        mArticleView.setItemAnimator(customAnimator);

        cacheArticles = articles;
        updateAdapter(new HomeAdapter(this, articles));

    }

    private void updateAdapter(final HomeAdapter adapter) {
        mArticleView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, ReadingActivity.class);
                intent.putExtra(Constants.CATEGORY, cacheArticles.get(position));
                startActivity(intent);
            }
        });
    }

    private boolean isPermissionGranted() {
        return Build.VERSION.SDK_INT < 23 ||
                ContextCompat.checkSelfPermission(this, requiredPermissions[0])
                        == PackageManager.PERMISSION_GRANTED;
    }

    private void showPermissions() {
        //Explain user if required.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            //Educate User
            showCustomDialog("Required Permission", "External storage access is required to cache " +
                            "images for your smooth experience.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //ask permissions.
                            ActivityCompat.requestPermissions(MainActivity.this, requiredPermissions,
                                    RUNTIME_PERMISSIONS_CODE);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            deadLock();
                        }
                    }
            );

        } else {
            //ask permissions.
            ActivityCompat.requestPermissions(this, requiredPermissions, RUNTIME_PERMISSIONS_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case RUNTIME_PERMISSIONS_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendDataRequest();
                } else {
                    //Disable the functionality.
                    permissionsDenied();
                }
            }
        }
    }

    private void permissionsDenied() {
        showCustomDialog("Required Permission", "DailyHunt requires access to \"External Storage\"" +
                " to load images.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, UPDATE_PERMISSIONS);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int ignoreResultCode, Intent data) {
        super.onActivityResult(requestCode, ignoreResultCode, data);

        //don't worry about result code.
        if (requestCode == UPDATE_PERMISSIONS) {
            if (isPermissionGranted()) {
                sendDataRequest();
            } else {
                deadLock();
            }
        }
    }

    private void deadLock() {
        showCustomDialog("Permissions Revoked", "Without External Storage permission, app cannot run.\n" +
                        " App will exit on press of \"OK\".", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_open_bookmarks:
                Intent intent = new Intent(MainActivity.this, BookMarkActivity.class);
                intent.putExtra(Constants.ARTICLES, (Serializable) cacheArticles);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            setOldData();
            return;
        }
        updateAdapter(new HomeAdapter(this, filterCategory(cacheArticles, mCategories.get(position))));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class CustomAnimator extends DefaultItemAnimator {

        //TODO-Custom Animate.
    }
}
