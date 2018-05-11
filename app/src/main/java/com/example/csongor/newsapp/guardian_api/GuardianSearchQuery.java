package com.example.csongor.newsapp.guardian_api;

import android.os.Parcel;

public class GuardianSearchQuery implements GuardianQuery{

    private static final String GUARDIAN_URL="http://content.guardianapis.com/search?";
    @ApiKey private static final  String  API_KEY=ApiKey.API_KEY;
    // string used in getQueryString() to get &page=1 URL part
    private static final String PAGE_SYMBOL ="&page=";
    private int mPage;
    // string used in getQueryString() to get q=xxx URL part
    private static final String QUERY_SYMBOL ="q=";
    private String mQueryString;
    // set up page size
    private static final String PAGE_SIZE_SYMBOL="&page-size=10";

    /**
     * Empty constructor if there are no search query.
     */
    public GuardianSearchQuery() {
        mPage=1;
    }

    /**
     * Default constructor for this query object.
     * Later there can be add another class for example search query in a specified section
     * { GuardianSearchBySection(String sectionName) etc. }
     * @param queryString - the query string for the URL request
     */
    public GuardianSearchQuery(String queryString) {
        this();
        mQueryString=queryString;
    }

    // Parcelable builder implementation
    protected GuardianSearchQuery(Parcel in) {
        mPage = in.readInt();
        mQueryString = in.readString();
    }

    public static final Creator<GuardianSearchQuery> CREATOR = new Creator<GuardianSearchQuery>() {
        @Override
        public GuardianSearchQuery createFromParcel(Parcel in) {
            return new GuardianSearchQuery(in);
        }

        @Override
        public GuardianSearchQuery[] newArray(int size) {
            return new GuardianSearchQuery[size];
        }
    };

    /**
     * Since the Guardian API accepts page number in query this value can be used to
     * retrieve the following pages (if available). At the first query this value _must_ set to 1,
     * otherwise there can be no results. By default let the ResultController to set this value
     *
     * @param page the number of requested result page
     */
    @Override
    public void setPage(int page) {
        mPage=page;
    }

    /**
     * This is the complete query String which can be parsed into URL.
     * @return the query String for Guardian REST URL.
     */
    @Override
    public String getQueryString() {
        if(mQueryString==null||mQueryString.equalsIgnoreCase(""))
            return GUARDIAN_URL+API_KEY+ PAGE_SIZE_SYMBOL+ PAGE_SYMBOL +String.valueOf(mPage);
        return GUARDIAN_URL+ QUERY_SYMBOL +mQueryString+API_KEY+ PAGE_SIZE_SYMBOL+ PAGE_SYMBOL +String.valueOf(mPage);
    }


    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPage);
        dest.writeString(mQueryString);
    }
}
