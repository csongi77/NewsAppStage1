package com.example.csongor.newsapp.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.csongor.newsapp.R;
import com.example.csongor.newsapp.guardian_api.NewsEntity;

import java.util.List;
import java.util.zip.Inflater;

public class NewsAdapter extends ArrayAdapter {

    private View mRootView;
    private List<NewsEntity> mNewsList;
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public NewsAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, R.layout.news_list, objects);
        mNewsList=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        mRootView=convertView;
        if (mRootView==null){
            mRootView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }
        NewsEntity newsEntity=mNewsList.get(position);

        // setting up views
        // todo apply ViewHolder pattern
        TextView title=mRootView.findViewById(R.id.list_item_txt_title);
        title.setText(newsEntity.getTitle());

        TextView author=mRootView.findViewById(R.id.list_item_txt_author);
        author.setText(newsEntity.getAuthor());

        TextView section=mRootView.findViewById(R.id.list_item_txt_section);
        section.setText(newsEntity.getSection());

        TextView datePublished = mRootView.findViewById(R.id.list_item_txt_date_published);
        datePublished.setText(newsEntity.getDatePublished());

        return mRootView;
    }
}
