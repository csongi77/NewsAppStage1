package com.example.csongor.newsapp.helpers;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.example.csongor.newsapp.helpers.BundleStates.CONNECTION_ERROR;
import static com.example.csongor.newsapp.helpers.BundleStates.JSON_PARSE_ERROR;
import static com.example.csongor.newsapp.helpers.BundleStates.NO_RESULTS;
import static com.example.csongor.newsapp.helpers.BundleStates.OK_READY;

/**
 * IntDef for network and parse states:
 * 0 - everything is fine, got results
 * 1 - conncetion OK, 0 results
 * 2 - connection OK, JSON parse error
 * 3 - connection error
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({OK_READY, NO_RESULTS, JSON_PARSE_ERROR, CONNECTION_ERROR})
public @interface BundleStates {
    int OK_READY=0;
    int NO_RESULTS=1;
    int JSON_PARSE_ERROR=2;
    int CONNECTION_ERROR=3;
}
