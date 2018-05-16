package com.example.csongor.newsapp;

import android.app.Service;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.csongor.newsapp.guardian_api.GuardianQuery;
import com.example.csongor.newsapp.guardian_api.NewsEntity;
import com.example.csongor.newsapp.helpers.NewsAdapter;
import com.example.csongor.newsapp.helpers.NewsLoader;

import java.util.List;

public class NewsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Bundle> {

    private static final String LOG_TAG = NewsListFragment.class.getSimpleName();
    private static final int LOADER_ID = 42;
    private static final String BUNDLE_ENTERED_VALUE = "BUNDLE_ENTERED_VALUE";
    private static final String BUNDLE_IS_SOFTKEYBOARD_ACTIVE = "BUNDLE_IS_SOFTKEYBOARD_ACTIVE";

    private View mRootView;
    private Loader<Bundle> mLoader;
    private GuardianQuery mGuardianQuery;
    private int mPages, mCurrentPage;
    private List<NewsEntity> mNewsList;
    private NewsAdapter mAdapter;
    private ListView mListView;
    private TextView mMessage;
    private ContentLoadingProgressBar mProgressBar;
    private LoaderManager mLoaderManager;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private LinearLayout mListController;
    private LinearLayout mBtnToFirst, mBtnBack, mBtnNext, mBtnToLast;
    private TextView mControllerStatusText;
    private EditText mToPageInput;
    private int mEnteredPageNumber;
    private Bundle mSavedInstanceState;
    private boolean mIsSoftkeyboardActive;
    private InputMethodManager mInputMethodManager;

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
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.news_list, container, false);

        // assigning values to Views
        mMessage = mRootView.findViewById(R.id.news_list_txt_message);

        mRecyclerView = mRootView.findViewById(R.id.news_list_view);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mProgressBar = mRootView.findViewById(R.id.news_list_progressbar);


        // getting arguments from Bundle
        Bundle queryBundle = getArguments();
        mGuardianQuery = queryBundle.getParcelable(BundleKeys.BUNDLE_QUERY);

        if (savedInstanceState != null) mSavedInstanceState = savedInstanceState;

        // set up loaderManager
        mLoaderManager = getLoaderManager();
        mLoader = mLoaderManager.initLoader(LOADER_ID, null, this);
        return mRootView;
    }



    /**
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @NonNull
    @Override
    public Loader<Bundle> onCreateLoader(int id, @Nullable Bundle args) {
        //  if(mLoader==null)
        mLoader = new NewsLoader(getContext(), mGuardianQuery.getQueryString());
        return mLoader;
    }


    /**
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Bundle> loader, Bundle data) {
        mCurrentPage = data.getInt(BundleKeys.BUNDLE_CURRENT_PAGE);
        mPages = data.getInt(BundleKeys.BUNDLE_PAGES);
        mNewsList = data.getParcelableArrayList(BundleKeys.BUNDLE_RESULT_LIST);
        if (mNewsList != null) {
            mAdapter = new NewsAdapter(mNewsList, getContext());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mProgressBar.hide();
            mMessage.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            // if result has more than one page let's set up List controller, else make it invisible
        } else {
            mProgressBar.hide();
            mMessage.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     * <p>
     * <p>This will always be called from the process's main thread.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Bundle> loader) {
        Log.d(LOG_TAG, "-----> onLoaderReset called");
        mProgressBar.show();
        mRecyclerView.setVisibility(View.GONE);
        mMessage.setVisibility(View.GONE);
        mListController.setVisibility(View.GONE);
    }



}
