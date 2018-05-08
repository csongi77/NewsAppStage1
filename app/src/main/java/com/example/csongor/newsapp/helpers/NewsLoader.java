package com.example.csongor.newsapp.helpers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.csongor.newsapp.BundleKeys;
import com.example.csongor.newsapp.guardian_api.GuardianQuery;
import com.example.csongor.newsapp.guardian_api.NewsEntity;
import com.example.csongor.newsapp.guardian_api.QueryController;
import com.example.csongor.newsapp.guardian_api.ResultEntity;

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

    private static final String LOG_TAG = NewsLoader.class.getSimpleName();
    private static final String REQUEST_METHOD = "GET";

    private List<NewsEntity> mNewsList;
    private String mUrl;
    private GuardianQuery mGuardianQuery;
    private int mPages;
    private int mCurrentPage;

    public NewsLoader(@NonNull Context context, String queryUrl) {
        super(context);
        mUrl = queryUrl;
    }


    @Nullable
    @Override
    public Bundle loadInBackground() {
        Log.d(LOG_TAG, "-----> loadInBackGround started");
        if (mNewsList == null) {
            // First make connection and get data based on URL REST API
            String jsonString = connectAndLoad(mUrl);
            // Parse result into List<News>
            if (jsonString != null)
                mNewsList = parseJsonToList(jsonString);
        }
        Bundle toReturn = new Bundle();
        toReturn.putParcelableArrayList(BundleKeys.BUNDLE_RESULT_LIST, (ArrayList) mNewsList);
        toReturn.putInt(BundleKeys.BUNDLE_PAGES, mPages);
        toReturn.putInt(BundleKeys.BUNDLE_CURRENT_PAGE, mCurrentPage);
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
            Log.d(LOG_TAG, "-----> trying to open: " + url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);

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
            Log.e(LOG_TAG, "----> MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "----> IO Exception");
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return stringBuilder.toString();
    }

    private List<NewsEntity> parseJsonToList(String jsonString) {
        List<NewsEntity> newsListToReturn = new ArrayList<>();
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
                String title = result.getString("webTitle");
                String section = result.getString("sectionName");
                String author;
                if (result.has("author")) {
                    author = result.getString("author");
                } else {
                    author = "Not available";
                }
                String datePublished = result.getString("webPublicationDate");
                if (datePublished == null) datePublished = "Unknown";
                String webUrl = result.getString("webUrl");
                NewsEntity newsEntity = new NewsEntity(title, section, author, datePublished, webUrl);
                newsListToReturn.add(newsEntity);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "----> JSON Parse Exception");
            e.printStackTrace();
        }
        return newsListToReturn;
    }

}
