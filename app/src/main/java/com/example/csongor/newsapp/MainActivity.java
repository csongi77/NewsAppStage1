package com.example.csongor.newsapp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.csongor.newsapp.guardian_api.GuardianDateWrapper;
import com.example.csongor.newsapp.guardian_api.GuardianQuery;
import com.example.csongor.newsapp.guardian_api.GuardianSearchQuery;
import com.example.csongor.newsapp.guardian_api.GuardianSectionWrapper;
import com.example.csongor.newsapp.guardian_api.NewsEntity;
import com.example.csongor.newsapp.guardian_api.YesterdayQueryDate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private List<NewsEntity> mNewsEntities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check network availability
        ConnectivityManager connectivityManager=(ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(!(networkInfo!=null && networkInfo.isConnectedOrConnecting())){
            Snackbar.make(findViewById(R.id.main_activity_placeholder),getString(R.string.no_network_error_message),Snackbar.LENGTH_INDEFINITE).show();
        }

        GuardianQuery query=new GuardianSearchQuery("");
        List<String> sectionsList=new ArrayList<>();
        sectionsList.add("news");
        sectionsList.add("environment");
        sectionsList.add("business");
        GuardianQuery section=new GuardianSectionWrapper(query,sectionsList);
        GuardianQuery date=new GuardianDateWrapper(section,new YesterdayQueryDate());

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
     * This method is called after {@link #onStart} when the activity is
     * being re-initialized from a previously saved state, given here in
     * <var>savedInstanceState</var>.  Most implementations will simply use {@link #onCreate}
     * to restore their state, but it is sometimes convenient to do it here
     * after all of the initialization has been done or to allow subclasses to
     * decide whether to use your default implementation.  The default
     * implementation of this method performs a restore of any view state that
     * had previously been frozen by {@link #onSaveInstanceState}.
     * <p>
     * <p>This method is called between {@link #onStart} and
     * {@link #onPostCreate}.
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
