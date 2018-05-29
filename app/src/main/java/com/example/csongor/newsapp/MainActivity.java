package com.example.csongor.newsapp;

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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import butterknife.BindInt;
import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String BASE_URL = "http://content.guardianapis.com/search";
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check network availability
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnectedOrConnecting())) {
            Snackbar.make(findViewById(R.id.main_activity_placeholder), getString(R.string.no_network_error_message), Snackbar.LENGTH_SHORT).show();

        }

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

        Uri base = Uri.parse(BASE_URL);

        Uri.Builder uriBuilder = base.buildUpon();
        base = uriBuilder.appendQueryParameter("api-key", BuildConfig.GUARDIAN_QUERY_API_KEY)
                .appendQueryParameter("q", "")
                .appendQueryParameter("section", sectionsQueryParameter)
                .appendQueryParameter("page-size", "25")
                .appendQueryParameter("from-date", mCurrentTime).build();
        Log.d(LOG_TAG, "----->The query string is: " + uriBuilder.toString());

        // on Configuration change we don't have to create new fragment just use the originally created one.
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Bundle bundleToSend = new Bundle();
            bundleToSend.putString(BundleKeys.BUNDLE_QUERY, base.toString());
            Fragment fragment = new NewsListFragment();
            fragment.setArguments(bundleToSend);
            transaction.add(R.id.main_activity_placeholder, fragment);
            transaction.commit();
        }
    }

    // inflate Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_options_menu, menu);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.menu_item_search_by_keyword).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    // on clicking menu item open Settings Activity with Intent
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optionsMenu = R.id.menu_item_search_options;
        if (item.getItemId() == optionsMenu) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
}
