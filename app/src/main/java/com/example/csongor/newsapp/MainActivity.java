package com.example.csongor.newsapp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.csongor.newsapp.guardian_api.GuardianDateWrapper;
import com.example.csongor.newsapp.guardian_api.GuardianQuery;
import com.example.csongor.newsapp.guardian_api.GuardianSearchQuery;
import com.example.csongor.newsapp.guardian_api.GuardianSectionWrapper;
import com.example.csongor.newsapp.guardian_api.YesterdayQueryDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG=MainActivity.class.getSimpleName();
    private static final String BASE_URL = "http://content.guardianapis.com/search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check network availability
        ConnectivityManager connectivityManager=(ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(!(networkInfo!=null && networkInfo.isConnectedOrConnecting())){
            Snackbar.make(findViewById(R.id.main_activity_placeholder),getString(R.string.no_network_error_message),Snackbar.LENGTH_SHORT).show();

        }

        // todo create uri with uribuilder
        String mCurrentTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000)));

        Uri base=Uri.parse(BASE_URL);

        Uri.Builder uriBuilder= base.buildUpon();
              base= uriBuilder.appendQueryParameter("api-key",BuildConfig.GUARDIAN_QUERY_API_KEY)
                .appendQueryParameter("q","")
                .appendQueryParameter("section","news|environment|business")
                .appendQueryParameter("page-size","15")
                .appendQueryParameter("from-date",mCurrentTime).build();
        Log.d(LOG_TAG,"----->The query string is: "+uriBuilder.toString());

        // on Configuration change we don't have to create new fragment just use the originally created one.
        if(savedInstanceState==null) {
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     *Creating saved instance in order to avoid creating new fragment
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
