package com.venomvendor.dailyhunt.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.venomvendor.dailyhunt.R;
import com.venomvendor.dailyhunt.model.Article;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private static final String TAG = HomeAdapter.class.getSimpleName();
    private final Activity mActivity;
    private final List<Article> mDataSet;
    private OnItemClickListener mItemClickListener;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet the data to populate views to be used by RecyclerView.
     */
    public HomeAdapter(Activity activity, List<Article> dataSet) {
        this.mActivity = activity;
        mDataSet = dataSet;
    }

    public List<Article> getData() {
        return mDataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.list_item_main, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.mTitle.setText(mDataSet.get(position).getTitle());
        holder.mPreview.setText(Html.fromHtml(mDataSet.get(position).getContent()));
        holder.mPublisher.setText(String.format("Published by : %s", mDataSet.get(position).getSource()));

        Uri imgUri = Uri.parse(mDataSet.get(position).getImage());

        Glide.with(mActivity)
                .load(imgUri)
                .fitCenter()
                .override(500, 500)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mThumbnail);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTitle;
        ImageView mThumbnail;
        TextView mPreview;
        TextView mPublisher;

        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.list_home_title);
            mThumbnail = (ImageView) view.findViewById(R.id.list_home_img);
            mPreview = (TextView) view.findViewById(R.id.list_home_cnt);
            mPublisher = (TextView) view.findViewById(R.id.list_home_date);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                // Define click listener for the ViewHolder's View.
                mItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
