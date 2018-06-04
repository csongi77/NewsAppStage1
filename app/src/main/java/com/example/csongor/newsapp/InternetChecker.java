package com.example.csongor.newsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class for checking internet connection status.
 * This class has a listener for connection status callback
 * Idea and suggestion from previous reviewer, documented at
 * https://www.androidhive.info/2012/07/android-detect-internet-connection-status/
 */
public class InternetChecker extends BroadcastReceiver {

    public static InternetCheckListener sInternetCheckListener;

    public InternetChecker() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (sInternetCheckListener != null) {
            sInternetCheckListener.onInternetStatusChanged(isConnected);
        }
    }

    public static boolean isConnectedToNet () {
        ConnectivityManager connectivityManager = (ConnectivityManager) NewsApp.getInstance()
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public interface InternetCheckListener {
        void onInternetStatusChanged(boolean isConnected);
    }
}
