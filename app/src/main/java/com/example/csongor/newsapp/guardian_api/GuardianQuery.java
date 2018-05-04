package com.example.csongor.newsapp.guardian_api;

import android.os.Parcelable;

/**
 * Basic interface for Queries. The basic query has:
 * 1) search string (may be null in general search and in query of search by section.
 * 2) api key
 * 3) page number. By default it is set to 1 but the ResultController class may modify it
 * This basic object will be wrapped by proper query objects (for example search, sections, tag
 * depending on search type)
 * At this moment (News Reader App stage 1) only search object wrapper will be delivered but
 * later it can be easily extendable.
 */
public interface GuardianQuery extends Parcelable {

    /**
     * Since the Guardian API accepts page number in query this value can be used to
     * retrieve the following pages (if available). At the first query this value _must_ set to 1,
     * otherwise there can be no results. By default let the ResultController to set this value
     * @param page the number of requested result page
     */
    void setPage(int page);

    /**
     * This is the complete query String which can be parsed into URL.
     * @return the query String for Guardian REST URL.
     */
    String getQueryString();

}
