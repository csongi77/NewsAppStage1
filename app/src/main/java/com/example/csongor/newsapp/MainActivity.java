package com.example.csongor.newsapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SearchView.OnCloseListener, InternetChecker.InternetCheckListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName() + "--->";
    private static final String BASE_URL = "http://content.guardianapis.com/search";
    private String mQueryParam, mQueryUriString;
    private SearchView mSearchView;
    private Bundle mSavedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "onCreate has been called");

        /*
         *  we get the reference of savedInstaceState and store it in global variable in order to
         *  check it at onStart method
         */
        mSavedState = savedInstanceState;
        mQueryParam = "";

        // Get the intent, verify the action and get the query
        handleIntent(getIntent());

        // Check network availability
        if (!InternetChecker.isConnectedToNet())
            Snackbar.make(findViewById(R.id.main_activity_placeholder), getString(R.string.no_network_error_message), Snackbar.LENGTH_SHORT).show();

        // If this is the firs time of starting the app we build the query and fire it
        if (savedInstanceState == null) {
            mQueryUriString = buildQuery();
            Log.d(LOG_TAG, "savedInstanceState is null");
            runQuery(mQueryUriString);
        }
    }

    /**
     * At this callback we have to check whether shared preferences
     * has been changed, because in _singleTop_ launch mode the _system_doesn't_call
     * onRestoreInstanceState_callback :(
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.e(LOG_TAG, "onStart called, buildQuery string = " + mQueryUriString);
        if (mSavedState != null) {
            /*
             * if the savedInstanceState was not null we get the saved values:
             * - the Bundle_Query holds the saved full query parameter
             * - the Bundle_Query_Param holds the search string which was entered into SearchView
             */
            String savedString = mSavedState.getString(BundleKeys.BUNDLE_QUERY);
            mQueryParam = mSavedState.getString(BundleKeys.BUNDLE_QUERY_PARAM);
            /*
             *  IMPORTANT:
             *  1) we build up a new query. This query is built up based on actual Shared Preferences
             *  2) if the new query is not the same as the saved one it means that Shared Preferences
             *      has been changed.
             *  3) in this case we have to fire the query again
             */
            mQueryUriString = buildQuery();
            Log.d(LOG_TAG, "savedInstanceState savedString = " + savedString);
            if (!mQueryUriString.equals(savedString)) {
                runQuery(mQueryUriString);
            }
        }
    }

    // set up Internet check listener when we restore UI
    @Override
    protected void onResume() {
        super.onResume();
        NewsApp.getInstance().setInternetCheckerListener(this);
    }

    /**
     * Because we use Single Top launch mode and the Search Intent is fired from here and this
     * Activity gets the same Intent we have to override this event as it's described in
     * Android developer guide (https://developer.android.com/training/search/setup)
     *
     * @param intent the intent we should handle
     */
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    // inflate Menu with SearchManager as it described at https://developer.android.com/training/search/setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_options_menu, menu);

        SearchManager mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_item_search_by_keyword).getActionView();

        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));
        // set up onCloseListener to be able to handle clearing search params
        mSearchView.setOnCloseListener(this);

        /**
         *  if the query param (Search text) is not null we set the query to the last one
         *  which was saved in Bundle. This is useful in case of device orientation change...
         */
        if (mQueryParam != null && mQueryParam.length() != 0)
            mSearchView.setQuery(mQueryParam, false);
        return super.onCreateOptionsMenu(menu);
    }

    // on clicking menu item open Settings Activity with Intent
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // handling menu click events
            case R.id.menu_item_search_options:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                Log.d(LOG_TAG, "------> onOptionsItemSelected");
                return super.onOptionsItemSelected(item);
        }
    }

    // Saving State for example on device orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState called");
        outState.putString(BundleKeys.BUNDLE_QUERY, mQueryUriString);
        outState.putString(BundleKeys.BUNDLE_QUERY_PARAM, mQueryParam);
        mSavedState = outState;
        super.onSaveInstanceState(mSavedState);
    }

    /**
     * We have to override the SearchView's default onClose callback method because
     * we want to reload the list if the user clears and closes the search field.
     *
     * @return true if the searchView was cleared and we want to reload the query(URI) without any query param.
     * false otherwise
     */
    @Override
    public boolean onClose() {
        Log.d(LOG_TAG, "onClose called");
        String textInQuery = mSearchView.getQuery().toString();

        // if the text in SearchView is not the same as before AND the length of
        // queryString is 0 then we must reload the query.
        if (!textInQuery.equalsIgnoreCase(mQueryParam) && textInQuery.length() == 0) {
            mQueryParam = textInQuery;
            mSearchView.setQuery(null, false);
            mQueryUriString = buildQuery();
            runQuery(mQueryUriString);
            mSearchView.clearFocus();
            return true;
        } else
            return false;
    }

    // Overriding Internet status change callback
    @Override
    public void onInternetStatusChanged(boolean isConnected) {
        if(!isConnected)
        Snackbar.make(findViewById(R.id.main_activity_placeholder), getString(R.string.no_network_error_message), Snackbar.LENGTH_LONG).show();
    }


    /**
     * Helper method for building Guardian query.
     */
    private String buildQuery() {

        // getting values of SharedPreferences
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // building DATE string
        String numberOfDaysString = mSharedPreferences.getString(getString(R.string.date_key), "1");
        int numberOfDaysInt = Integer.parseInt(numberOfDaysString);
        String mCurrentTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() - (numberOfDaysInt * 24 * 60 * 60 * 1000)));

        // building Sections string
        StringBuilder sectionsQueryParameterBuilder = new StringBuilder("");
        String sectionsQueryParameter;
        Set<String> sectionSet = mSharedPreferences.getStringSet(getString(R.string.sections_key), null);
        if (sectionSet != null && !sectionSet.isEmpty()) {

            // if any section has been selected, we iterate over the Set in order to build up sections query parameter
            Iterator<String> iterator = sectionSet.iterator();
            sectionsQueryParameterBuilder.append(iterator.next());
            while (iterator.hasNext()) sectionsQueryParameterBuilder.append("|" + iterator.next());
            sectionsQueryParameter = sectionsQueryParameterBuilder.toString();
        } else {
            sectionsQueryParameter = getString(R.string.all_other_sections_query_parameter);
        }

        // and now we build up the query
        Uri mBaseUri = Uri.parse(BASE_URL);

        Uri.Builder uriBuilder = mBaseUri.buildUpon();
        // Idea and suggestion for storing api-key in BuildConfig is from my previous reviewer
        uriBuilder = uriBuilder.appendQueryParameter("api-key", BuildConfig.GUARDIAN_QUERY_API_KEY)
                .appendQueryParameter("section", sectionsQueryParameter)
                .appendQueryParameter("page-size", "25")
                .appendQueryParameter("from-date", mCurrentTime);

        // we have to append query param (&q=...) if it has already defined
        if (mQueryParam != null && mQueryParam.length() != 0)
            uriBuilder = uriBuilder.appendQueryParameter("q", mQueryParam);
        mBaseUri = uriBuilder.build();
        Log.d(LOG_TAG, "----->(buildQuery) The query string is: " + uriBuilder.toString());

        return mBaseUri.toString();
    }

    /**
     * Helper method for executing query and display it in a fragment
     *
     * @param restQueryString - the URI query in String format.
     */
    private void runQuery(String restQueryString) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundleToSend = new Bundle();
        bundleToSend.putString(BundleKeys.BUNDLE_QUERY, restQueryString);
        Fragment fragment = new NewsListFragment();
        fragment.setArguments(bundleToSend);
        transaction.replace(R.id.main_activity_placeholder, fragment);
        transaction.commit();
    }

    /**
     * helper method for handling incoming search intents.
     *
     * @param intent the incoming intent. If it's an intent with a Search Action,
     *               we build up a new query with the search parameter and fire the query
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQueryParam = intent.getStringExtra(SearchManager.QUERY);
            Log.d(LOG_TAG, "query string: " + mQueryParam);
            mQueryUriString = buildQuery();
            runQuery(mQueryUriString);
        }
    }
}
