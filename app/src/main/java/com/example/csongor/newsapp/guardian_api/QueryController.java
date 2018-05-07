package com.example.csongor.newsapp.guardian_api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Class for retrieving result from Guardian's URL and managing loading the remaining pages.
 * This Class has to be instantiated in NewesLoader and will passed back to
 * ListFragment's OnLoadFinished callback method.
 * The responsibilities of this class:
 * 1) hold the original GuardianQuery
 * 2) after downloading the results, this instance will hold the
 *  List of ResultEntities (in this case, only News)
 * 3) this object can determine how many resultPages has left, and
 * can modify the Query's page number in order to retrieve other pages based on original Query
 */
public class QueryController implements Parcelable {

    // the number of result pages. It's retrieved from Guardians's JSON object,
    private int mPages;

    // the actual page. It's retrieved from Guardians's JSON object,
    private int mCurrentPage;

    // the list of results. Get it from Guardian's response JSONArray.
    private List<ResultEntity> mResultList;

    // the original Query.
    private GuardianQuery mGuardianQuery;

    /**
     * Default constructor of Controller
     * @param pages - maximum number from result pages.
     *               Get the value from Guardian's response JSON object's "pages" key
     * @param currentPage - current page of the result list.
     *                     Get the value from Guardian's response JSON object's "currentPage" key
     * @param originalQuery - the original Query object. It must be passed back to the original Fragment
     * @param resultList - the result list from Guardian's Response JSONArray of key "results".
     */
    public QueryController(int pages, int currentPage, GuardianQuery originalQuery, List<ResultEntity> resultList) {
        mPages = pages;
        mCurrentPage = currentPage;
        mGuardianQuery = originalQuery;
        mResultList = resultList;
    }

    // Implementing Parcelable
    protected QueryController(Parcel in) {
        mPages = in.readInt();
        mCurrentPage = in.readInt();
        // todo check it!!!! this is not the original implementation of Parcelable
        mResultList = in.readArrayList(ResultEntity.class.getClassLoader());
        mGuardianQuery = in.readParcelable(GuardianQuery.class.getClassLoader());
    }

    public static final Creator<QueryController> CREATOR = new Creator<QueryController>() {
        @Override
        public QueryController createFromParcel(Parcel in) {
            return new QueryController(in);
        }

        @Override
        public QueryController[] newArray(int size) {
            return new QueryController[size];
        }
    };

    // for checking whether has result list more pages
    public boolean hasNextPage(){
        return mCurrentPage<mPages;
    }

    // for navigation purposes. If user want to navigate back, this method can check are there any previous page left
    public boolean hasPreviousPage(){
        return mCurrentPage>1;
    }

    // returns the GuardianQuery object ready to pass to NewsLoader for downloading next page
    public GuardianQuery getNextPage(){
        if (hasNextPage()){
         mGuardianQuery.setPage(++mCurrentPage);
        }
        return mGuardianQuery;
    }

    // returns the GuardianQuery object ready to pass to NewsLoader for downloading previous page
    public GuardianQuery getPreviousPage(){
        if(hasPreviousPage()){
            mGuardianQuery.setPage(--mCurrentPage);
        }
        return mGuardianQuery;
    }

    // sets up the GuardianQuery object for specified page. If the number is invalid, the original object will be returned
    public GuardianQuery getSpecifiedPage(int pageNumber){
        if(pageNumber>0&&pageNumber<=mPages){
            mGuardianQuery.setPage(pageNumber);
        }
        return mGuardianQuery;
    }

    /**
     * @return - the result list of entities
     */
    public List<ResultEntity> getResultList() {
        return mResultList;
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
        dest.writeInt(mPages);
        dest.writeInt(mCurrentPage);
        //dest.writeTypedList(mResultList);
        dest.writeList(mResultList);
        dest.writeParcelable(mGuardianQuery, flags);
    }
}
