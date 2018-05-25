package com.example.csongor.newsapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.csongor.newsapp.guardian_api.NewsEntity;
import com.example.csongor.newsapp.helpers.BundleStates;
import com.example.csongor.newsapp.helpers.NewsAdapter;
import com.example.csongor.newsapp.helpers.NewsLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NewsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Bundle> {

    private static final String LOG_TAG = NewsListFragment.class.getSimpleName();
    private static final int LOADER_ID = 42;

    @BindView(R.id.list_controller_back_page)
    LinearLayout mBtnBack;
    @BindView(R.id.list_controller_next_page)
    LinearLayout mBtnNext;
    @BindView(R.id.list_controller_last_page)
    LinearLayout mBtnToLast;
    @BindView(R.id.ic_first_page)
    ImageView mImageToFirst;
    @BindView(R.id.ic_previous_page)
    ImageView mImageBack;
    @BindView(R.id.ic_next_page)
    ImageView mImageNext;
    @BindView(R.id.ic_last_page)
    ImageView mImageToLast;
    @BindView(R.id.list_controller_txt_first_page)
    TextView mTxtToFirst;
    @BindView(R.id.list_controller_txt_back)
    TextView mTxtBack;
    @BindView(R.id.list_controller_txt_next)
    TextView mTxtNext;
    @BindView(R.id.list_controller_txt_last_page)
    TextView mTxtToLast;
    @BindView(R.id.list_controller_to_page_message)
    TextView mControllerStatusText;
    @BindView(R.id.list_controller_first_page)
    LinearLayout mBtnToFirst;
    @BindView(R.id.news_list_txt_message)
    TextView mMessage;
    private View mRootView;
    private Loader<Bundle> mLoader;
    private String mGuardianQuery;
    private int mPages, mCurrentPage;
    @BindView(R.id.news_list_progressbar)
    ContentLoadingProgressBar mProgressBar;
    private LoaderManager mLoaderManager;
    @BindView(R.id.news_list_view)
    RecyclerView mRecyclerView;
    private LinearLayout mListController;
    private List<NewsEntity> mNewsList;
    private Button mReloadBtn;
    private Unbinder unbinder;
    private Uri mUri;

    // Default constructor
    public NewsListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.news_list, container, false);
        unbinder = ButterKnife.bind(this, mRootView);

        // assigning values to Views
        //mRecyclerView = mRootView.findViewById(R.id.news_list_view);
        RecyclerView.LayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setVisibility(View.GONE);
        // mProgressBar = mRootView.findViewById(R.id.news_list_progressbar);
        mProgressBar.show();
        mListController = mRootView.findViewById(R.id.list_controller_menu);
        mListController.setVisibility(View.GONE);
        // mMessage = mRootView.findViewById(R.id.news_list_txt_message);
        mMessage.setVisibility(View.VISIBLE);
        mReloadBtn = mRootView.findViewById(R.id.news_list_btn_reload);
        mReloadBtn.setVisibility(View.GONE);

        // getting arguments from Bundle
        Bundle queryBundle = getArguments();
        mGuardianQuery = queryBundle.getString(BundleKeys.BUNDLE_QUERY);
        mUri = Uri.parse(mGuardianQuery);

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
        mLoader = new NewsLoader(getContext(), mUri.toString());
        return mLoader;
    }

    /**
     * loader callback when load finishes. Depending on BundleStates result code it
     * will change the UI with some helper methods (setupErrorView and setupResultView)
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Bundle> loader, Bundle data) {

        // checking result status first because it always exists
        @BundleStates int mResult;
        mResult = data.getInt(BundleKeys.BUNDLE_STATUS);
        Log.d(LOG_TAG, "mReturnedResult=" + mResult);

        // calling helper methods depending on result.
        switch (mResult) {
            case BundleStates.CONNECTION_ERROR:
                mMessage.setText(getText(R.string.connestion_error_message));
                setupErrorView();
                break;
            case BundleStates.JSON_PARSE_ERROR:
                mMessage.setText(getText(R.string.json_parse_error_message));
                setupErrorView();
                break;
            case BundleStates.NO_RESULTS:
                mMessage.setText(getText(R.string.no_results_error_message));
                setupErrorView();
                break;
            default:
                // getting return values since it's sure there is at least 1 result which can be displayed
                mCurrentPage = data.getInt(BundleKeys.BUNDLE_CURRENT_PAGE);
                mPages = data.getInt(BundleKeys.BUNDLE_PAGES);
                mNewsList = data.getParcelableArrayList(BundleKeys.BUNDLE_RESULT_LIST);
                setupResultView();
        }
    }

    /**
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

    /**
     * Helper method for setting up result list and display them
     */
    private void setupResultView() {

        NewsAdapter mAdapter = new NewsAdapter(mNewsList, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mProgressBar.hide();
        mMessage.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        // if result has more than one page let's set up List controller, else make it invisible
        if (mPages > 1) {
            setUpControllerView();
        } else {
            mListController.setVisibility(View.GONE);
        }
    }

    /**
     * Helper method for showing error messages which were set up at onLoadFinished callback
     */
    private void setupErrorView() {
        mProgressBar.hide();
        mReloadBtn.setOnClickListener(v -> {
            mLoaderManager.restartLoader(LOADER_ID, null, this);
            mProgressBar.show();
            mReloadBtn.setVisibility(View.GONE);
        });
        mReloadBtn.setVisibility(View.VISIBLE);
        mMessage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mListController.setVisibility(View.GONE);
    }

    /**
     * helper method for setting up Controller View depending on current page:
     * 1) First page -> Back and ToFirstPage buttons are inactive
     * 2) Last page -> Next and ToLastPage buttons are inactive
     * 3) Every other case -> every buttons are active
     */
    private void setUpControllerView() {

        mListController.setVisibility(View.VISIBLE);
        Uri.Builder builder = mUri.buildUpon();

        // Set up default text and icon colors
        mImageToFirst.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_first_page));
        mTxtToFirst.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mImageBack.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_navigate_before));
        mTxtBack.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mImageNext.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_navigate_next));
        mTxtNext.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mImageToLast.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_last_page));
        mTxtToLast.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        // Set up "of XX" pages text in controller
        mControllerStatusText.setText(String.format(getString(R.string.list_controller_of_pages), mCurrentPage, mPages));

        /**
         * Setting up onClickListeners for buttons. Cliciking on them will modify the
         * GuardianQuery object in order to download the appropriate page.
         * We don't have to check the limits (whether is current page is the first or last) because
         * we'll do it at next step.
         */
        mBtnToLast.setOnClickListener(v -> {
            mUri=builder.appendQueryParameter("page",String.valueOf(mPages)).build();
            //mGuardianQuery.setPage(mPages);
            mLoader = mLoaderManager.restartLoader(LOADER_ID, null, NewsListFragment.this);
        });
        mBtnNext.setOnClickListener(v -> {
            //mGuardianQuery.setPage(++mCurrentPage);
            mUri=builder.appendQueryParameter("page",String.valueOf(++mCurrentPage)).build();
            mLoader = mLoaderManager.restartLoader(LOADER_ID, null, NewsListFragment.this);
        });
        mBtnToFirst.setOnClickListener(v -> {
            //mGuardianQuery.setPage(1);
            mUri=builder.appendQueryParameter("page","1").build();
            mLoader = mLoaderManager.restartLoader(LOADER_ID, null, NewsListFragment.this);
        });
        mBtnBack.setOnClickListener(v -> {
            //mGuardianQuery.setPage(--mCurrentPage);
            mUri=builder.appendQueryParameter("page",String.valueOf(--mCurrentPage)).build();
            mLoader = mLoaderManager.restartLoader(LOADER_ID, null, NewsListFragment.this);
        });

        /**
         * Checking state of current page. In first and last page state we disable the buttons
         * and set up inactive colors for them.
         */
        if (mCurrentPage == 1) {

            // make appropriate buttons inactive
            mBtnToFirst.setClickable(false);
            mBtnToFirst.setFocusable(false);
            mImageToFirst.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_first_page_inactive));
            mTxtToFirst.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_grey));
            mBtnBack.setClickable(false);
            mBtnBack.setFocusable(false);
            mImageBack.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_navigate_before_inactive));
            mTxtBack.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_grey));

        } else if (mCurrentPage == mPages) {
            // this is the last page state
            mBtnNext.setClickable(false);
            mBtnNext.setFocusable(false);
            mImageNext.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_navigate_next_inactive));
            mTxtNext.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_grey));
            mBtnToLast.setClickable(false);
            mBtnToLast.setFocusable(false);
            mImageToLast.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_last_page_inactive));
            mTxtToLast.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_grey));
        }
    }
}
