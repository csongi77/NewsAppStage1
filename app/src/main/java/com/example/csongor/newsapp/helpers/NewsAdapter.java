package com.example.csongor.newsapp.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.csongor.newsapp.R;
import com.example.csongor.newsapp.guardian_api.NewsEntity;

import java.util.List;
import java.util.zip.Inflater;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {


    private List<NewsEntity> mNewsList;

    /**
     * Constructor for Adapter.
     * @param newsList - the list we want to show
     */
    public NewsAdapter(List<NewsEntity> newsList) {
        mNewsList = newsList;
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
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
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
        NewsEntity entity=mNewsList.get(position);
        holder.title.setText(entity.getTitle());
        holder.datePublished.setText(entity.getDatePublished());
        holder.section.setText(entity.getSection());
        holder.author.setText(entity.getAuthor());
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
    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView title;
        private final TextView author;
        private final TextView section;
        private final TextView datePublished;

        // constructor for ViewHolder.
        public ViewHolder(View itemView) {
            super(itemView);
            // todo set up onClickListener for the view
            //setting up views
            title=itemView.findViewById(R.id.list_item_txt_title);
            author=itemView.findViewById(R.id.list_item_txt_author);
            section=itemView.findViewById(R.id.list_item_txt_section);
            datePublished=itemView.findViewById(R.id.list_item_txt_date_published);
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
    }
}
