package com.example.csongor.newsapp;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.example.csongor.newsapp.BundleKeys.BUNDLE_CURRENT_PAGE;
import static com.example.csongor.newsapp.BundleKeys.BUNDLE_PAGES;
import static com.example.csongor.newsapp.BundleKeys.BUNDLE_QUERY;
import static com.example.csongor.newsapp.BundleKeys.BUNDLE_RESULT_LIST;

@Retention(RetentionPolicy.SOURCE)
@StringDef({BUNDLE_QUERY,BUNDLE_RESULT_LIST,BUNDLE_PAGES,BUNDLE_CURRENT_PAGE})
public @interface BundleKeys {
    String BUNDLE_QUERY="BUNDLE_QUERY";
    String BUNDLE_RESULT_LIST="BUNDLE_RESULT_LIST";
    String BUNDLE_PAGES="BUNDLE_PAGES";
    String BUNDLE_CURRENT_PAGE="BUNDLE_CURRENT_PAGE";
}
