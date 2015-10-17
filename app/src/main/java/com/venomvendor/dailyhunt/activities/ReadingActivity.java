package com.venomvendor.dailyhunt.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.venomvendor.dailyhunt.R;
import com.venomvendor.dailyhunt.model.Article;
import com.venomvendor.dailyhunt.util.Constants;

public class ReadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Bundle bundle = getIntent().getExtras();
        Article articles = bundle.getParcelable(Constants.CATEGORY);
        Log.d("ReadingActivity", articles.getContent());

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(articles.getTitle());

        TextView content = (TextView) findViewById(R.id.reading_content);
        String largeData = articles.getContent();
        StringBuilder stringBuilder = new StringBuilder(largeData);
        for (int i = 0; i < 10; i++) {
            stringBuilder = stringBuilder.append("<br><br>\t\t").append(largeData);
        }
        content.setText(Html.fromHtml(stringBuilder.toString()));

        ImageView backDrop = (ImageView) findViewById(R.id.backdrop);
        Uri imgUri = Uri.parse(articles.getImage());
        Glide.with(this)
                .load(imgUri)
                .fitCenter()
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(backDrop);

    }
}
