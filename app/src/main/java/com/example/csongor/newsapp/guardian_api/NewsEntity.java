package com.example.csongor.newsapp.guardian_api;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Basic News Entity
 */
public class NewsEntity implements Parcelable{
    private final String mTitle;
    private final String mSection;
    private final String mAuthor;
    private final String mDatePublished;
    private final String mURL;

    /**
     * Constructor of Entity
     *
     * @param title         - the title of News
     * @param section       - section of News
     * @param author        - author of News
     * @param datePublished - the publication date
     * @param url           - the URL of Article
     */
    public NewsEntity(String title, String section, String author, String datePublished, String url) {
        mTitle = title;
        mSection = section;
        mAuthor = author;
        mDatePublished = formatDate(datePublished);
        mURL = url;

    }

       // Parcelable implementation
    protected NewsEntity(Parcel in) {
        mTitle = in.readString();
        mSection = in.readString();
        mAuthor = in.readString();
        mDatePublished = in.readString();
        mURL = in.readString();
    }

    public static final Creator<NewsEntity> CREATOR = new Creator<NewsEntity>() {
        @Override
        public NewsEntity createFromParcel(Parcel in) {
            return new NewsEntity(in);
        }

        @Override
        public NewsEntity[] newArray(int size) {
            return new NewsEntity[size];
        }
    };

    /**
     * getter for title
     *
     * @return - title String of news object
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * getter for Section
     *
     * @return - the Section String of this instance
     */
    public String getSection() {
        return mSection;
    }

    /**
     * getter for Author. If the value is null or "" it returns "Anonymus"
     *
     * @return - name of Author
     */
    public String getAuthor() {
        if (mAuthor == null || mAuthor.equalsIgnoreCase("")) return "Anonymus";
        return mAuthor;
    }

    /**
     * getter for Date of publication
     *
     * @return publication Date in String format
     */
    public String getDatePublished() {
        return mDatePublished;
    }

    /**
     * getter for Article
     *
     * @return - the URL string of Article
     */
    public String getURL() {
        return mURL;
    }
    /**
     * Overriding equals in order to let these object comparable
     *
     * @param o - the other object
     * @return true if and only if the two objects are the same instances otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsEntity that = (NewsEntity) o;

        if (!mTitle.equals(that.mTitle)) return false;
        if (!mSection.equals(that.mSection)) return false;
        if (mAuthor != null ? !mAuthor.equals(that.mAuthor) : that.mAuthor != null) return false;
        return mDatePublished != null ? mDatePublished.equals(that.mDatePublished) : that.mDatePublished == null;
    }

    /**
     * Overriding hashCode
     *
     * @return heshCode
     */
    @Override
    public int hashCode() {
        int result = mTitle.hashCode();
        result = 31 * result + mSection.hashCode();
        result = 31 * result + (mAuthor != null ? mAuthor.hashCode() : 0);
        result = 31 * result + (mDatePublished != null ? mDatePublished.hashCode() : 0);
        return result;
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
        dest.writeString(mTitle);
        dest.writeString(mSection);
        dest.writeString(mAuthor);
        dest.writeString(mDatePublished);
        dest.writeString(mURL);
    }

    /**
     * Helper method to format parsed date into proper format
     * @param datePublished - from parsed JSON string format yyyy-MM-ddThh:mm:ssZ
     * @return - The return String in dd-MM-yyyy hh:mm
     */
    private String formatDate(String datePublished) {
        //cut original string
        String [] toCut = datePublished.split("T|Z");
        String [] dateToCut = toCut[0].split("-");
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(dateToCut[2]+"-").append(dateToCut[1]+"-").append(dateToCut[0]+", ");
        String [] timeToCut = toCut[1].split(":");
        stringBuilder.append(timeToCut[0]+":"+timeToCut[1]+"Z");
        return stringBuilder.toString();
    }
}


