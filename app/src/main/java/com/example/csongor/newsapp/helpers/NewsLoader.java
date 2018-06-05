package com.example.csongor.newsapp.helpers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.csongor.newsapp.BundleKeys;
import com.example.csongor.newsapp.R;
import com.example.csongor.newsapp.guardian_api.NewsEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<Bundle> {

    // defining constacnt variables
    private static final String LOG_TAG = NewsLoader.class.getSimpleName();
    private static final String REQUEST_METHOD = "GET";
    private static final int CONNECT_TIMEOUT = 5 * 1000; // 5 seconds for setting connection timeout
    private static final int READ_TIMEOUT = 5 * 1000; // 5 seconds for setting read timeout
    private static final int NOT_SET_YET = 0;

    // defining variables
    private List<NewsEntity> mNewsList;
    private String mUrl;
    private int mPages;
    private int mCurrentPage;
    @BundleStates
    private int mResult;

    // default constructor
    public NewsLoader(@NonNull Context context, String queryUrl) {
        super(context);
        mCurrentPage = NOT_SET_YET;
        mUrl = queryUrl;
    }

    /**
     * overriding default loader behaviour
     *
     * @return - result Bundle.
     * It contains:
     * 1) Result code - compulsory, always exists
     * Optionally:
     * 2) result list of NewsEntities
     * 3) number of result pages
     * 4) number of total number of pages
     */
    @Nullable
    @Override
    public Bundle loadInBackground() {
        Log.d(LOG_TAG, "-----> loadInBackGround started");
        if (mCurrentPage == NOT_SET_YET) {
            // First make connection and get data based on URL REST API
            String jsonString = connectAndLoad(mUrl);

            // Parse result into List<NewsEntity>
            if (jsonString != null) {
                mNewsList = parseJsonToList(jsonString);
            }
        }
        // set up result Bundle. if there are more results than a single page, with these values can be reload following pages
        Bundle toReturn = new Bundle();
        toReturn.putInt(BundleKeys.BUNDLE_STATUS, mResult);
        if (mResult == 0) {
            toReturn.putParcelableArrayList(BundleKeys.BUNDLE_RESULT_LIST, (ArrayList) mNewsList);
            toReturn.putInt(BundleKeys.BUNDLE_PAGES, mPages);
            toReturn.putInt(BundleKeys.BUNDLE_CURRENT_PAGE, mCurrentPage);
        }
        return toReturn;
    }

    /**
     * Subclasses must implement this to take care of loading their data,
     * as per {@link #startLoading()}.  This is not called by clients directly,
     * but as a result of a call to {@link #startLoading()}.
     * This will always be called from the process's main thread.
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    /**
     * MEthod for making connection based on URL string.
     *
     * @param urlToParse - Url for the news
     * @return the retrieved String or null if there were no result;
     */
    private String connectAndLoad(String urlToParse) {
        Log.d(LOG_TAG, "-----> connectAndLoad started (using web traffic)");
        HttpURLConnection urlConnection = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // Open connection
            URL url = new URL(urlToParse);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);

            // Get Inputstrem -> BufferedReader
            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // read the first line
            String toRead = reader.readLine();

            // if not null repeat it until end of file
            while (toRead != null) {
                stringBuilder.append(toRead);
                toRead = reader.readLine();
            }
        } catch (MalformedURLException e) {
            // handling url connection errors
            Log.e(LOG_TAG, "----> MalformedURLException");
            e.printStackTrace();
            mResult = BundleStates.CONNECTION_ERROR;
            return null;
        } catch (IOException e) {

            // handling url connection errors
            Log.e(LOG_TAG, "----> IO Exception");
            e.printStackTrace();
            mResult = BundleStates.CONNECTION_ERROR;
            return null;
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return stringBuilder.toString();
    }

    /**
     * Helper method for parsing downloaded JSON string into News object
     *
     * @param jsonString
     * @return
     */
    private List<NewsEntity> parseJsonToList(String jsonString) {
        List<NewsEntity> newsListToReturn = new ArrayList<>();
        if (jsonString != null) {
            try {
                JSONObject baseObject = new JSONObject(jsonString);
                JSONObject response = baseObject.getJSONObject("response");

                // parse current page and pages in order to be able download next page,
                mCurrentPage = response.getInt("currentPage");
                mPages = response.getInt("pages");
                JSONArray newsListArray = response.getJSONArray("results");

                // parsing JsonArray's news list
                for (int i = 0; i < newsListArray.length(); i++) {
                    JSONObject result = newsListArray.getJSONObject(i);
                /*
                getting title. If title consist "|" then get Author, since the webTitle consists
                author's name, if exists
                 */
                    String title = result.getString("webTitle").trim();
                    String author;
                    if (title.contains("|")) {
                        String[] toTrim = title.split("[|]");
                        author = toTrim[1].trim();
                        title = toTrim[0].trim();
                    } else {
                        author = getContext().getString(R.string.author_not_available);
                    }

                    // getting section
                    String section = result.getString("sectionName").trim();

                    // getting and formatting publication date
                    String datePublished = result.getString("webPublicationDate").trim();
                    String webUrl = result.getString("webUrl").trim();

                    // creating NewsEntity from the values
                    NewsEntity newsEntity = new NewsEntity(getContext(), title, section, author, datePublished, webUrl);
                    newsListToReturn.add(newsEntity);
                }
            } catch (JSONException e) {
                // handling JSON parsing errors
                Log.e(LOG_TAG, "----> JSON Parse Exception");
                e.printStackTrace();
                mResult = BundleStates.JSON_PARSE_ERROR;
                return null;
            }
        }

        // checking are there any results. If there is at least 1 result set mResult value to 0;
        // for more info see @BundleStates
        if (newsListToReturn.isEmpty()) {
            mResult = BundleStates.NO_RESULTS;
            Log.e(LOG_TAG, "----> No results found");
        } else {
            mResult = BundleStates.OK_READY;
        }
        return newsListToReturn;
    }
}
