package com.example.csongor.newsapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName() + "--->";
    private static final String BASE_URL = "http://content.guardianapis.com/search";
    private SharedPreferences mSharedPreferences;
    private String mQueryParam, mQueryUriString;
    private SearchView mSearchView;
    private Bundle mSavedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "onCreate has been called");
        mSavedState = savedInstanceState;
        mQueryParam = "";

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQueryParam = intent.getStringExtra(SearchManager.QUERY);
            Log.d(LOG_TAG, "query string: " + mQueryParam);
        }

        // Check network availability
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnectedOrConnecting())) {
            Snackbar.make(findViewById(R.id.main_activity_placeholder), getString(R.string.no_network_error_message), Snackbar.LENGTH_SHORT).show();

        }

        mQueryUriString = buildQuery();
        // on Configuration change we don't have to create new fragment just use the originally created one.
        if (savedInstanceState == null) {
            Log.d(LOG_TAG, "savedInstanceState is null");
            runQuery(mQueryUriString);
        }
    }

    /**
     * We have to check at this callback that whether shared preferences
     * has been changed, because in singleTop launch mode the system don't call
     * onRestoreInstanceState callback :(
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart called");
        mQueryUriString = buildQuery();
        Log.e(LOG_TAG, "buildQuery string = " + mQueryUriString);
        if (mSavedState != null) {
            String savedString = mSavedState.getString(BundleKeys.BUNDLE_QUERY);
            Log.d(LOG_TAG, "savedInstanceState savedString = " + savedString);
            if (!mQueryUriString.equals(savedString)) {
                mQueryUriString = buildQuery();
                runQuery(mQueryUriString);
            }
        }
    }

    // inflate Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_item_search_by_keyword).getActionView();

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);

        return super.onCreateOptionsMenu(menu);
    }


    // on clicking menu item open Settings Activity with Intent
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search_options:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                Log.d(LOG_TAG, "------> onOptionsItemSelected");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState called");
        outState.putString(BundleKeys.BUNDLE_QUERY, mQueryUriString);
        mSavedState = outState;
        super.onSaveInstanceState(outState);
    }


    /**
     * Helper method for building Guardian query. This method is called from
     * onCreate and onQueryTextSubmit methods.
     */
    private String buildQuery() {

        // getting values of SharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // DATE
        String numberOfDaysString = mSharedPreferences.getString(getString(R.string.date_key), "1");
        int numberOfDaysInt = Integer.parseInt(numberOfDaysString);
        String mCurrentTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() - (numberOfDaysInt * 24 * 60 * 60 * 1000)));

        // Sections
        StringBuilder sectionsQueryParameterBuilder = new StringBuilder("");
        String sectionsQueryParameter;
        Set<String> sectionSet = mSharedPreferences.getStringSet(getString(R.string.sections_key), null);
        if (sectionSet != null && !sectionSet.isEmpty()) {
            Iterator<String> iterator = sectionSet.iterator();
            sectionsQueryParameterBuilder.append(iterator.next());
            while (iterator.hasNext()) sectionsQueryParameterBuilder.append("|" + iterator.next());
            sectionsQueryParameter = sectionsQueryParameterBuilder.toString();
        } else {
            sectionsQueryParameter = getString(R.string.all_other_sections_query_parameter);
        }

        Uri mBaseUri = Uri.parse(BASE_URL);

        Uri.Builder uriBuilder = mBaseUri.buildUpon();
        mBaseUri = uriBuilder.appendQueryParameter("api-key", BuildConfig.GUARDIAN_QUERY_API_KEY)
                .appendQueryParameter("q", mQueryParam)
                .appendQueryParameter("section", sectionsQueryParameter)
                .appendQueryParameter("page-size", "25")
                .appendQueryParameter("from-date", mCurrentTime).build();
        Log.d(LOG_TAG, "----->The query string is: " + uriBuilder.toString());

        return mBaseUri.toString();
    }

    /**
     * Helper method for executing query and display it in a fragment
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

}
