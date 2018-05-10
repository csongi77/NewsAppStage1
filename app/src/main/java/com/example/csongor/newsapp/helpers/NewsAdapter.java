package com.example.csongor.newsapp.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.csongor.newsapp.R;
import com.example.csongor.newsapp.guardian_api.NewsEntity;

import java.util.List;

/**
 * Recycler View adapter instead ArrayAdapter in order to avoid findViewById methods.
 * This was suggested by my Reviewer (Vlad Spreys).
 * Infos, tutorials taken from:
 * http://spreys.com/view-holder-design-pattern-for-android/
 * https://github.com/googlesamples/android-RecyclerView
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();
    private List<NewsEntity> mNewsList;
    private Context mContext;

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
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
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
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
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

        private final TextView title;
        private final TextView author;
        private final TextView section;
        private final TextView datePublished;
        private final LinearLayout layout;
        private final int newsColor, businessColor, environmentColor;

        // constructor for ViewHolder.
        public ViewHolder(final View itemView) {
            super(itemView);
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
            // setting up views
            title = itemView.findViewById(R.id.list_item_txt_title);
            author = itemView.findViewById(R.id.list_item_txt_author);
            section = itemView.findViewById(R.id.list_item_txt_section);
            datePublished = itemView.findViewById(R.id.list_item_txt_date_published);
            layout = itemView.findViewById(R.id.list_item_layout);
            // setting up colors
            newsColor = ContextCompat.getColor(mContext, R.color.color_news);
            businessColor = ContextCompat.getColor(mContext, R.color.color_business);
            environmentColor = ContextCompat.getColor(mContext, R.color.color_environment);
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
