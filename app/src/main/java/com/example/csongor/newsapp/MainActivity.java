package com.example.csongor.newsapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import butterknife.BindInt;
import butterknife.BindView;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String BASE_URL = "http://content.guardianapis.com/search";
    private SharedPreferences mSharedPreferences;
    private String mQueryString;
    private Uri mBaseUri;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueryString = "";

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(LOG_TAG, "------> query string: " + query);
        }

        // Check network availability
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnectedOrConnecting())) {
            Snackbar.make(findViewById(R.id.main_activity_placeholder), getString(R.string.no_network_error_message), Snackbar.LENGTH_SHORT).show();

        }

        buildQuery();

        // on Configuration change we don't have to create new fragment just use the originally created one.
        if (savedInstanceState == null) {
            runQuery();
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
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
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
            case R.id.menu_item_search_by_keyword:
                mSearchView.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mSearchView,InputMethodManager.SHOW_FORCED);
                return true;
            default:
                Log.d(LOG_TAG, "------> onOptionsItemSelected");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Creating saved instance in order to avoid creating new fragment
     *
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     * @see #onCreate
     * @see #onPostCreate
     * @see #onResume
     * @see #onSaveInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(LOG_TAG, "-----> onQueryTextSubmit has been called");
        onClose();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(LOG_TAG, "-----> onQueryTextSubmit has been called, newText="+newText);
        mQueryString=newText;
        return true;
    }



    /**
     * Helper method for building Guardian query. This method is called from
     * onCreate and onQueryTextSubmit methods.
     */
    private void buildQuery() {

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

        mBaseUri = Uri.parse(BASE_URL);

        Uri.Builder uriBuilder = mBaseUri.buildUpon();
        mBaseUri = uriBuilder.appendQueryParameter("api-key", BuildConfig.GUARDIAN_QUERY_API_KEY)
                .appendQueryParameter("q", mQueryString)
                .appendQueryParameter("section", sectionsQueryParameter)
                .appendQueryParameter("page-size", "25")
                .appendQueryParameter("from-date", mCurrentTime).build();
        Log.d(LOG_TAG, "----->The query string is: " + uriBuilder.toString());
    }

    /**
     * Helper method for executing query and display it in a fragment
     */
    private void runQuery() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundleToSend = new Bundle();
        bundleToSend.putString(BundleKeys.BUNDLE_QUERY, mBaseUri.toString());
        Fragment fragment = new NewsListFragment();
        fragment.setArguments(bundleToSend);
        transaction.replace(R.id.main_activity_placeholder, fragment);
        transaction.commit();
    }

    @Override
    public boolean onClose() {
        Log.d(LOG_TAG, "-----> onClose has been called");
        buildQuery();
        runQuery();
        mSearchView.setIconified(true);
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager.isActive()){
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        return false;
    }
}
