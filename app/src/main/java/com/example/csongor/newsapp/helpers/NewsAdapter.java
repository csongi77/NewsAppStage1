package com.example.csongor.newsapp.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.csongor.newsapp.R;
import com.example.csongor.newsapp.guardian_api.NewsEntity;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Recycler View adapter instead ArrayAdapter in order to avoid findViewById methods.
 * This was suggested by my Reviewer (Vlad Spreys) at my previous project.
 * Infos, tutorials taken from:
 * http://spreys.com/view-holder-design-pattern-for-android/
 * https://github.com/googlesamples/android-RecyclerView
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();
    private List<NewsEntity> mNewsList;
    private Context mContext;
    private Unbinder unbinder;

    /**
     * Constructor for Adapter.
     *
     * @param newsList - the list we want to show
     */
    public NewsAdapter(List<NewsEntity> newsList, Context context) {
        mNewsList = newsList;
        mContext = context;
    }

    /**
     * Default ViewHolder implementation
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(mRootView);
    }


    /**
     * default implementation of ViewHolder
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsEntity entity = mNewsList.get(position);
        holder.getTitle().setText(entity.getTitle());
        holder.getDatePublished().setText(entity.getDatePublished());
        holder.getSection().setText(entity.getSection());
        holder.getAuthor().setText(entity.getAuthor());
        switch (entity.getSection().toLowerCase()) {
            case "news":
                holder.getLayout().setBackgroundColor(holder.newsColor);
                return;
            case "business":
                holder.getLayout().setBackgroundColor(holder.businessColor);
                return;
            default:
                holder.getLayout().setBackgroundColor(holder.environmentColor);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mNewsList.size();
    }


    // ViewHolder pattern implementation in order to avoid the cost of finding views by id
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_txt_title)
        TextView title;
        @BindView(R.id.list_item_txt_author)
        TextView author;
        @BindView(R.id.list_item_txt_section)
        TextView section;
        @BindView(R.id.list_item_txt_date_published)
        TextView datePublished;
        @BindView(R.id.list_item_layout)
        LinearLayout layout;

        @BindColor(R.color.color_news)
        int newsColor;
        @BindColor(R.color.color_business)
        int businessColor;
        @BindColor(R.color.color_environment)
        int environmentColor;

        // constructor for ViewHolder.
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Log.d(LOG_TAG, "----> ViewHolder has been instantiated");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewsEntity entity = mNewsList.get(getAdapterPosition());
                    Uri uri = Uri.parse(entity.getURL());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);
                }
            });
        }

        /**
         * @return - TextView reference of Title field
         */
        public TextView getTitle() {
            return title;
        }

        /**
         * @return - TextView reference of Author field
         */
        public TextView getAuthor() {
            return author;
        }

        /**
         * @return - TextView reference of Section field
         */
        public TextView getSection() {
            return section;
        }

        /**
         * @return - TextView reference of Date of Publication field
         */
        public TextView getDatePublished() {
            return datePublished;
        }

        /**
         * @return - Layout reference of List item
         */
        public LinearLayout getLayout() {
            return layout;
        }
    }

}
