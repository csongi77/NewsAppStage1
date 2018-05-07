package com.example.csongor.newsapp.guardian_api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Iterator;
import java.util.List;

/**
 *  Section wrapper of GuardianSearchQuery in order to find news only in selected sections
 */
public class GuardianSectionWrapper extends GuardianQueryAbstractWrapper {

    private static final String SECTION_SYMBOL="&section=";
    private List<String> mSections;

    /**
     * Constructor of this wrapper object
     * @param wrappedQuery - the wrapped Query instance
     * @param sectionList - the list for the search terms
     */
    public GuardianSectionWrapper(GuardianQuery wrappedQuery, List<String> sectionList) {
        super(wrappedQuery);
        this.mSections = mSections;
    }

    protected GuardianSectionWrapper(Parcel in) {
        mSections = in.createStringArrayList();
    }

    public static final Creator<GuardianSectionWrapper> CREATOR = new Creator<GuardianSectionWrapper>() {
        @Override
        public GuardianSectionWrapper createFromParcel(Parcel in) {
            return new GuardianSectionWrapper(in);
        }

        @Override
        public GuardianSectionWrapper[] newArray(int size) {
            return new GuardianSectionWrapper[size];
        }
    };


    /**
     * This is the complete query String which can be parsed into URL.
     *
     * @return the query String for Guardian REST URL.
     */
    @Override
    public String getQueryString() {
        return super.getQueryString()+this.sectionsToString();
    }

    /**
     * Helper method for making section REST string
     * @return - the String for query URL. Since this is a simple query parser
     * it uses only OR conditions between section queries
     */
    private String sectionsToString() {
        // put base query string into StringBuilder object -> "&section="
        StringBuilder builder=new StringBuilder(SECTION_SYMBOL);
        // getting Iterator of section list
        Iterator<String> iterator=mSections.iterator();
        while (iterator.hasNext()){
            // add section element to query string ->"news"
            builder.append(iterator.next());
            // if there are more elements in list, append "|" symbol as it is required by Guardian API
            if(iterator.hasNext())builder.append("|");
        }
        return builder.toString();
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
        dest.writeStringList(mSections);
    }
}
