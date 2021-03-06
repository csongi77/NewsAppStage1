package com.example.csongor.newsapp.guardian_api;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.csongor.newsapp.R;

/**
 * Basic News Entity
 */
public class NewsEntity implements Parcelable {
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
    private final String mTitle;
    private final String mSection;
    private final String mAuthor;
    private final String mDatePublished;
    private final String mURL;
    private Context mContext;

    /**
     * Constructor of Entity
     *
     * @param title         - the title of News
     * @param section       - section of News
     * @param author        - author of News
     * @param datePublished - the publication date
     * @param url           - the URL of Article
     */
    public NewsEntity(Context context, String title, String section, String author, String datePublished, String url) {
        mTitle = title;
        mSection = section;
        mAuthor = author;
        mContext = context;
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
     * @return heshCode                holder.getSection().setText(mContext.getString(R.string.section_news_label));

     */
    @Override
    public int hashCode() {
        int result = mTitle.hashCode();
        result = 31 * result + mSection.hashCode();
        result = 31 * result + (mAuthor != null ? mAuthor.hashCode() : 0);
        result = 31 * result + (mDatePublished != null ? mDatePublished.hashCode() : 0);
        return result;
    }


    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

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
     *
     * @param datePublished - from parsed JSON string format yyyy-MM-ddThh:mm:ssZ
     * @return - The return String in dd-MM-yyyy hh:mm
     */
    private String formatDate(String datePublished) {
        //cut original string if it's not null or empty
        if (datePublished != null && !datePublished.equalsIgnoreCase("") && datePublished.length() != 0) {
            String[] toCut = datePublished.split("T|Z");
            String[] dateToCut = toCut[0].split("-");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(dateToCut[2] + "-").append(dateToCut[1] + "-").append(dateToCut[0] + ", ");
            String[] timeToCut = toCut[1].split(":");
            stringBuilder.append(timeToCut[0] + ":" + timeToCut[1] + mContext.getString(R.string.zulu_time));
            return stringBuilder.toString();
        } else return mContext.getString(R.string.date_unknown);
    }
}


