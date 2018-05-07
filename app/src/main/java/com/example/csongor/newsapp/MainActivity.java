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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private List<NewsEntity> mNewsEntities;
    private static final String BUNDLE_QUERY = "BUNDLE_QUERY";

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

        GuardianQuery query=new GuardianSearchQuery("London");
        GuardianQuery date=new GuardianDateWrapper(query,new YesterdayQueryDate());

        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        Bundle bundleToSend=new Bundle();
        bundleToSend.putParcelable(BUNDLE_QUERY,date);
        Fragment fragment = new NewsListFragment();
        fragment.setArguments(bundleToSend);
        transaction.add(R.id.main_activity_placeholder,fragment);
        transaction.commit();

        // todo create ListFragment
        // todo create restUrlQuery object with builder
    }
}
