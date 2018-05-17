package com.example.csongor.newsapp.guardian_api;

import android.os.Parcel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * QueryDate class implementation for querying news from yesterday and today
 */
public class YesterdayQueryDate implements QueryDate {

    private String mCurrentTime;
    // Default constructor for calculating yesterday

    public YesterdayQueryDate() {
        mCurrentTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000)));
    }
    // parcelable implementation

    public static final Creator<YesterdayQueryDate> CREATOR = new Creator<YesterdayQueryDate>() {
        @Override
        public YesterdayQueryDate createFromParcel(Parcel in) {
            return new YesterdayQueryDate(in);
        }

        @Override
        public YesterdayQueryDate[] newArray(int size) {
            return new YesterdayQueryDate[size];
        }
    };
    protected YesterdayQueryDate(Parcel in) {
        mCurrentTime = in.readString();
    }

    /**
     * This string will be returned to GuardianDateWrapper
     *
     * @return -
     */
    @Override
    public String getDateString() {
        return "&from-date=" + mCurrentTime;
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
        dest.writeString(mCurrentTime);
    }
}
