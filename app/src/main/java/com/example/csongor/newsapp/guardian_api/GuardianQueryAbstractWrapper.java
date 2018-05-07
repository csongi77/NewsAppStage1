package com.example.csongor.newsapp.guardian_api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Abstract Query Wrapper for components (date modifier, section modifier etc.)
 */
public abstract class GuardianQueryAbstractWrapper implements GuardianQuery {

    private GuardianQuery mWrappedQuery;


    /**
     * Basi
     * @param wrappedQuery - the Base Query class. At the moment (News App stage 1) the only
     *                     possible argument can be a GuardianSearchQuery object.
     */
    protected GuardianQueryAbstractWrapper(GuardianQuery wrappedQuery){
        this.mWrappedQuery=wrappedQuery;
    }

    // requred by Parcelable implementation
    protected GuardianQueryAbstractWrapper() {
    }


    /**
     * Since the Guardian API accepts page number in query this value can be used to
     * retrieve the following pages (if available). At the first query this value _must_ set to 1,
     * otherwise there can be no results. By default let the ResultController to set this value
     *
     * @param page the number of requested result page
     */
    @Override
    public void setPage(int page) {
        mWrappedQuery.setPage(page);
    }

    /**
     * This is the complete query String which can be parsed into URL.
     *
     * @return the query String for Guardian REST URL.
     */
    @Override
    public String getQueryString() {
        return mWrappedQuery.getQueryString();
    }
}
