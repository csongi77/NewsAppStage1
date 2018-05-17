package com.example.csongor.newsapp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.csongor.newsapp.guardian_api.GuardianDateWrapper;
import com.example.csongor.newsapp.guardian_api.GuardianQuery;
import com.example.csongor.newsapp.guardian_api.GuardianSearchQuery;
import com.example.csongor.newsapp.guardian_api.GuardianSectionWrapper;
import com.example.csongor.newsapp.guardian_api.YesterdayQueryDate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


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

        // creating Query object
        GuardianQuery query=new GuardianSearchQuery("");
        List<String> sectionsList=new ArrayList<>();
        sectionsList.add("news");
        sectionsList.add("environment");
        sectionsList.add("business");
        GuardianQuery section=new GuardianSectionWrapper(query,sectionsList);
        GuardianQuery date=new GuardianDateWrapper(section,new YesterdayQueryDate());

        // on Configuration change we don't have to create new fragment just use the originally created one.
        if(savedInstanceState==null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Bundle bundleToSend = new Bundle();
            bundleToSend.putParcelable(BundleKeys.BUNDLE_QUERY, date);
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
