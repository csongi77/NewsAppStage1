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
        mListController = mRootView.findViewById(R.id.list_controller_menu);
        mListController.setVisibility(View.GONE);
        mToPageInput = mRootView.findViewById(R.id.list_controller_to_page_input);

        // getting arguments from Bundle
        Bundle queryBundle = getArguments();
        mGuardianQuery = queryBundle.getParcelable(BundleKeys.BUNDLE_QUERY);

        if (savedInstanceState != null) mSavedInstanceState = savedInstanceState;

        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        // set up loaderManager
        mLoaderManager = getLoaderManager();
        mLoader = mLoaderManager.initLoader(LOADER_ID, null, this);
        return mRootView;
    }


    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mInputMethodManager.isAcceptingText()) mIsSoftkeyboardActive = true;
        outState.putBoolean(BUNDLE_IS_SOFTKEYBOARD_ACTIVE, mIsSoftkeyboardActive);
        if (!mToPageInput.getEditableText().toString().equalsIgnoreCase("")) {
            mEnteredPageNumber = Integer.parseInt(mToPageInput.getText().toString());
        } else {
            mEnteredPageNumber = 0;
        }
        outState.putInt(BUNDLE_ENTERED_VALUE, mEnteredPageNumber);
        Log.d(LOG_TAG, "--------> Saving to state: " + mEnteredPageNumber + ", mIsSoftKeyboard active: " + mIsSoftkeyboardActive);
        super.onSaveInstanceState(outState);
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


    /*
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

        // Set up "of XX" pages text in controller
        mControllerStatusText.setText(String.format(getString(R.string.list_controller_of_pages), mPages));

        // Implementing the navigate to selected page part
        mToPageInput.setHint(String.valueOf(mCurrentPage));

        // setup listener for Edit text. When user finishes typing pressing "done" it starts to download selected page
        mToPageInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    mIsSoftkeyboardActive = false;
                    navigateToPage();
                    mToPageInput.setText(null);
                    mToPageInput.clearFocus();
                    if (mInputMethodManager.isActive()) {
                        mInputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                }
                return false;
            }
        });

        /**
         * checking whether the softKeyboard should be opened. For instance user clicks on EditText and
         * then rotates the device, we save the state of EditText value and the status of Keyboard.
         * If it was opened, we open it again.
         */
        if (mSavedInstanceState != null) {
            int savedInputValue = 0;
            try {
                savedInputValue = mSavedInstanceState.getInt(BUNDLE_ENTERED_VALUE);
                mIsSoftkeyboardActive = mSavedInstanceState.getBoolean(BUNDLE_IS_SOFTKEYBOARD_ACTIVE);
                Log.d(LOG_TAG, "------> retrieving states after config change. Entered value: " +
                        savedInputValue + ", mIsSoftKeyboardActive: " + mIsSoftkeyboardActive);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (mIsSoftkeyboardActive) {
                if (savedInputValue != 0) {
                    mEnteredPageNumber = savedInputValue;
                    mToPageInput.setText(String.valueOf(mEnteredPageNumber));
                }
                if (!mInputMethodManager.isAcceptingText()) {
                    Log.d(LOG_TAG,"-------> requesting focus and showing input....");
                    InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mToPageInput.requestFocus();
                    imm.showSoftInput(mToPageInput, InputMethodManager.SHOW_FORCED);
                }
            }

        }


     /*   mToPageInput.setOnClickListener(v -> {
            if (!mIsSoftkeyboardActive) {
                mToPageInput.requestFocus();
                mInputMethodManager.showSoftInput(mToPageInput, InputMethodManager.SHOW_IMPLICIT);
                mIsSoftkeyboardActive = true;
            } else {
                mToPageInput.requestFocus();
                mInputMethodManager.hideSoftInputFromInputMethod(mToPageInput.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });*/

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

        /**
         * Checking state of current page. In first and last page state we disable the buttons
         */
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
     * Helper method in order to navigate to the selected page via EditText.
     * We check whether valid values has been set.
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
