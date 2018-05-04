package com.example.csongor.newsapp.guardian_api;

/**
 * Basic News Entity
 */
public class NewsEntity implements ResultEntity {
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
}
