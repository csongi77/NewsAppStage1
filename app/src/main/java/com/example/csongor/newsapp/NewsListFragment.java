package com.example.csongor.newsapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.csongor.newsapp.guardian_api.GuardianQuery;

public class NewsListFragment extends Fragment {

    private static final String BUNDLE_QUERY = "BUNDLE_QUERY";

    // Default constructor
    public NewsListFragment() {
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mRootView=LayoutInflater.from(getContext()).inflate(R.layout.news_list,container,false);
        TextView message = mRootView.findViewById(R.id.news_list_txt_message);
        Bundle queryBundle = getArguments();
        GuardianQuery query=queryBundle.getParcelable(BUNDLE_QUERY);
        query.setPage(2);
        message.setText(query.getQueryString());
        return mRootView;
    }
}
