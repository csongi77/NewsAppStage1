package com.example.csongor.newsapp.guardian_api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Basic News Entity
 */
public class NewsEntity implements ResultEntity{
    private final String mTitle;
    private final String mSection;
    private final String mAuthor;
    private final String mDatePublished;
    // private final long mDateLong;

    /**
     * Constructor of Entity
     *
     * @param title         - the title of News
     * @param section       - section of News
     * @param author        - author of News
     * @param datePublished - the publication date
     */
    public NewsEntity(String title, String section, String author, String datePublished) {
        this.mTitle = title;
        this.mSection = section;
        this.mAuthor = author;
        this.mDatePublished = datePublished;

    }

    protected NewsEntity(Parcel in) {
        mTitle = in.readString();
        mSection = in.readString();
        mAuthor = in.readString();
        mDatePublished = in.readString();
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
     * @return - title String of news object
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * getter for Section
     * @return - the Section String of this instance
     */
    public String getSection() {
        return mSection;
    }

    /**
     * getter for Author. If the value is null or "" it returns "Anonymus"
     * @return - name of Author
     */
    public String getAuthor() {
        if(mAuthor==null || mAuthor.equalsIgnoreCase(""))return  "Anonymus";
        return mAuthor;
    }

    /**
     * getter for Date of publication
     * @return publication Date in String format
     */
    public String getDatePublished() {
        return mDatePublished;
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
    }
}
