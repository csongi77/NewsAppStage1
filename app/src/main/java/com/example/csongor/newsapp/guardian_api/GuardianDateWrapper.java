package com.example.csongor.newsapp.guardian_api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Date wrapper for basic GuardianSearchQuery. It extends the base query string with Date.
 */
public class GuardianDateWrapper extends GuardianQueryAbstractWrapper implements Parcelable {

    // parcelable implementation
    public static final Creator<GuardianDateWrapper> CREATOR = new Creator<GuardianDateWrapper>() {
        @Override
        public GuardianDateWrapper createFromParcel(Parcel in) {
            return new GuardianDateWrapper(in);
        }

        @Override
        public GuardianDateWrapper[] newArray(int size) {
            return new GuardianDateWrapper[size];
        }
    };
    /**
     * QueryDate object. With this strategy this class can return different date query string
     * based on search (news from yesterday or from-to etc.)
     */
    private QueryDate mDate;

    /**
     * @param wrappedQuery - the Base Query class constructor. At the moment (News App stage 1)
     *                     this object accepts Query which will be wrapped with this object and
     *                     a QueryDate strategy.
     */
    public GuardianDateWrapper(GuardianQuery wrappedQuery, QueryDate mDate) {
        super(wrappedQuery);
        this.mDate = mDate;
    }

    protected GuardianDateWrapper(Parcel in) {
        mDate = in.readParcelable(QueryDate.class.getClassLoader());
    }

    /**
     * This is the complete query String which can be parsed into URL.
     *
     * @return the query String for Guardian REST URL.
     */
    @Override
    public String getQueryString() {
        return super.getQueryString() + mDate.getDateString();
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
        dest.writeParcelable(mDate, flags);
    }
}
