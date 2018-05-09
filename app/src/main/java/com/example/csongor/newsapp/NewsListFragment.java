package com.example.csongor.newsapp;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.csongor.newsapp.guardian_api.GuardianQuery;
import com.example.csongor.newsapp.guardian_api.NewsEntity;
import com.example.csongor.newsapp.helpers.NewsAdapter;
import com.example.csongor.newsapp.helpers.NewsLoader;

import java.util.List;

public class NewsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Bundle>{

    private static final String LOG_TAG=NewsListFragment.class.getSimpleName();
    private static final int LOADER_ID=42;

    private Loader<Bundle> mLoader;
    private GuardianQuery mGuardianQuery;
    private int mPages, mCurrentPage;
    private List<NewsEntity> mNewsList;
    private ArrayAdapter mArrayAdapter;
    private ListView mListView;
    private TextView mMessage;
    private ContentLoadingProgressBar mProgressBar;
    private LoaderManager mLoaderManager;
    private RecyclerView mRecyclerView;


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

        // assigning values to Views
        mMessage = mRootView.findViewById(R.id.news_list_txt_message);
        //mListView
        mRecyclerView = mRootView.findViewById(R.id.news_list_view);
        LinearLayoutManager mLinearLayoutManager=new LinearLayoutManager(getContext());

        mProgressBar = mRootView.findViewById(R.id.news_list_progressbar);

        // getting arguments from Bundle
        Bundle queryBundle = getArguments();
        mGuardianQuery=queryBundle.getParcelable(BundleKeys.BUNDLE_QUERY);
// todo load more pages
        // set up loaderManager
        mLoaderManager=getLoaderManager();
        mLoader=mLoaderManager.initLoader(LOADER_ID,null,this);
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
        if(mLoader==null)
        mLoader=new NewsLoader(getContext(),mGuardianQuery.getQueryString());
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
        mCurrentPage=data.getInt(BundleKeys.BUNDLE_CURRENT_PAGE);
        mPages=data.getInt(BundleKeys.BUNDLE_PAGES);
        mNewsList=data.getParcelableArrayList(BundleKeys.BUNDLE_RESULT_LIST);
        if(mNewsList!=null){
            mArrayAdapter=new NewsAdapter(getContext(),mNewsList);
            mListView.setAdapter(mArrayAdapter);
            mProgressBar.hide();
            mMessage.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.hide();
            mMessage.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
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
        Log.d(LOG_TAG,"-----> onLoaderReset called");
        mProgressBar.show();
        mListView.setVisibility(View.GONE);
        mMessage.setVisibility(View.GONE);
    }
}
