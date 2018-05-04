package com.example.csongor.newsapp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.csongor.newsapp.guardian_api.NewsEntity;

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

        // todo create ListFragment
        // todo create restUrlQuery object with builder
    }
}
