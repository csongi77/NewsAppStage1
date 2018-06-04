package com.example.csongor.newsapp;

import android.app.Application;


/**
 * Base NewsApp class with built-in Internet connection listener
 * Idea and suggestion from previous reviewer, documented at
 * https://www.androidhive.info/2012/07/android-detect-internet-connection-status/
 */
public class NewsApp extends Application {

    private static NewsApp sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance =this;
    }

    public static synchronized NewsApp getInstance (){
        return sInstance;
    }

    public void setInternetCheckerListener(InternetChecker.InternetCheckListener listener){
        InternetChecker.sInternetCheckListener = listener;
    }
}
