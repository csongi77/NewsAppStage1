package com.example.csongor.newsapp.guardian_api;

import android.os.Parcelable;

/**
 * Interface for Query date strategy
 * At this stage it (News App stage 1) it has only "Yesterday" class.
 * Later it can be extensible with:
 * 1) from date
 * 2) to date
 * 3) from-to date
 */
public interface QueryDate extends Parcelable {
    /**
     * The string has to returned to GuardianDateWrapper depending on Date strategy
     * @return - String to wrapper, starting with "&date..."
     */
    String getDateString();
}
