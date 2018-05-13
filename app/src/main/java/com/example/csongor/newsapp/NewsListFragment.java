package com.example.csongor.newsapp;

import android.content.Context;
import android.content.CursorLoader;
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
        mListController = mRootView.findViewById(R.id.list_controller_menu);
        mListController.setVisibility(View.GONE);

        // getting arguments from Bundle
        Bundle queryBundle = getArguments();
        mGuardianQuery = queryBundle.getParcelable(BundleKeys.BUNDLE_QUERY);
// todo load more pages
        // set up loaderManager
        mLoaderManager = getLoaderManager();
        mLoader = mLoaderManager.initLoader(LOADER_ID, null, this);
        return mRootView;
    }


    /**
     * Instantiate and return a new Loader for the given ID.
     * <p>
     * <p>This will always be called from the process's main thread.
     *
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
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context, * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     * <p>
     * <p>This will always be called from the process's main thread.
     *
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
            mProgressBar.hide();
            mMessage.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            // if result has more than one page let's set up List controller, else make it invisible
            if (mPages > 1) {
                setUpControllerView();
            } else {
                mListController.setVisibility(View.GONE);
            }
        } else {
            mProgressBar.hide();
            mMessage.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mListController.setVisibility(View.GONE);
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


    /**
     * helper method for setting up Controller View depending on current page:
     * 1) First page -> Back and ToFirstPage buttons are inactive
     * 2) Last page -> Next and ToLastPage buttons are inactive
     * 3) Every other case -> every buttons are active
     */
    private void setUpControllerView() {
        /**
         *  assigning values to views (here because if there are only one result page
         *  we don't have to make list_controller visible
         */
        mListController.setVisibility(View.VISIBLE);
        mBtnToFirst = mRootView.findViewById(R.id.list_controller_first_page);
        mBtnBack = mRootView.findViewById(R.id.list_controller_back_page);
        mBtnNext = mRootView.findViewById(R.id.list_controller_next_page);
        mBtnToLast = mRootView.findViewById(R.id.list_controller_last_page);
        mControllerStatusText = mRootView.findViewById(R.id.list_controller_to_page_message);
        mToPageInput = mRootView.findViewById(R.id.list_controller_to_page_input);

        // Set up "of XX" pages text in controller
        mControllerStatusText.setText(String.format(getString(R.string.list_controller_of_pages), mPages));

        // Implementing the navigate to selected page part
        mToPageInput.setText(null);
        mToPageInput.setHint(String.valueOf(mCurrentPage));

        if(mToPageInput.hasFocus()){
            InputMethodManager inputMethodManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
        }

        mToPageInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    navigateToPage();
                    //mToPageInput.setCursorVisible(false);
                    mToPageInput.clearFocus();
                    InputMethodManager inputMethodManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(inputMethodManager.isActive()){
                        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                    }
                }
                return false;
            }
        });

        /**
         * Setting up onClickListeners for buttons. Cliciking on them will modify the
         * GuardianQuery object in order to download the appropriate page.
         * We don't have to check the limits (whether is current page is the first or last) because
         * we'll do it at next step.
         */
        mBtnToLast.setOnClickListener(v -> {
            mGuardianQuery.setPage(mPages);
            mLoader = mLoaderManager.restartLoader(LOADER_ID, null, NewsListFragment.this);
        });
        mBtnNext.setOnClickListener(v -> {
            mGuardianQuery.setPage(++mCurrentPage);
            mLoader = mLoaderManager.restartLoader(LOADER_ID, null, NewsListFragment.this);
        });
        mBtnToFirst.setOnClickListener(v -> {
            mGuardianQuery.setPage(1);
            mLoader = mLoaderManager.restartLoader(LOADER_ID, null, NewsListFragment.this);
        });
        mBtnBack.setOnClickListener(v -> {
            mGuardianQuery.setPage(--mCurrentPage);
            mLoader = mLoaderManager.restartLoader(LOADER_ID, null, NewsListFragment.this);
        });
        if (mCurrentPage == 1) {
            // make appropriate buttons inactive
            mBtnToFirst.setClickable(false);
            mBtnToFirst.setFocusable(false);
            mBtnBack.setClickable(false);
            mBtnBack.setFocusable(false);
        } else if (mCurrentPage == mPages) {
            // this is the last page state
            mBtnNext.setClickable(false);
            mBtnNext.setFocusable(false);
            mBtnToLast.setClickable(false);
            mBtnToLast.setFocusable(false);
        }
    }

    /**
     *  Helper method in order to navigate to the selected page via EditText.
     *  We check whether valid values has been set.
     */
    private void navigateToPage() {
        int value = mCurrentPage;
        // Are there any values given?
        if (!mToPageInput.getEditableText().toString().equalsIgnoreCase(""))
            value = Integer.parseInt(mToPageInput.getEditableText().toString());
        // if yes, check whether is it valid: startPage<= given number <=maximumPage are valid
        if (value != mCurrentPage) {
            if (value < 1) {
                mGuardianQuery.setPage(1);
            } else if (value > mPages) {
                mGuardianQuery.setPage(mPages);
            } else {
                mGuardianQuery.setPage(value);
            }
            mLoader = mLoaderManager.restartLoader(LOADER_ID, null, NewsListFragment.this);
        }
    }
}
