package com.example.csongor.newsapp.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.csongor.newsapp.guardian_api.NewsEntity;

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

public class NewsLoader extends AsyncTaskLoader<List<NewsEntity>> {

    private static final String LOG_TAG=NewsLoader.class.getSimpleName();
    private static final String REQUEST_METHOD="GET";
    private List<NewsEntity> mNewsList;
    private String mUrl;

    public NewsLoader(@NonNull Context context, String queryUrl) {
        super(context);
        mUrl=queryUrl;
    }

    /**
     * Called on a worker thread to perform the actual load and to return
     * the result of the load operation.
     * <p>
     * Implementations should not deliver the result directly, but should return them
     * from this method, which will eventually end up calling {@link #deliverResult} on
     * the UI thread.  If implementations need to process the results on the UI thread
     * they may override {@link #deliverResult} and do so there.
     * <p>
     * To support cancellation, this method should periodically check the value of
     * {@link #isLoadInBackgroundCanceled} and terminate when it returns true.
     * Subclasses may also override {@link #cancelLoadInBackground} to interrupt the load
     * directly instead of polling {@link #isLoadInBackgroundCanceled}.
     * <p>
     * When the load is canceled, this method may either return normally or throw
     * {@link OperationCanceledException}.  In either case, the {@link Loader} will
     * call {@link #onCanceled} to perform post-cancellation cleanup and to dispose of the
     * result object, if any.
     *
     * @return The result of the load operation.
     * @throws OperationCanceledException if the load is canceled during execution.
     * @see #isLoadInBackgroundCanceled
     * @see #cancelLoadInBackground
     * @see #onCanceled
     */
    @Nullable
    @Override
    public List<NewsEntity> loadInBackground() {
        if(mNewsList==null){
            // First make connection and get data based on URL REST API
            String jsonString = connectAndLoad(mUrl);
            // Parse result into List<News>
            if(jsonString!=null)
            mNewsList=parseJsonToList(jsonString);
        }
        return mNewsList;
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
     * @param mUrl - Url for the news
     * @return the retrieved String or null if there were no result;
     */
    private String connectAndLoad(String mUrl) {
        HttpURLConnection urlConnection=null;
        StringBuilder stringBuilder=new StringBuilder();
        try {
            // Open connection
            URL url=new URL(mUrl);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.setConnectTimeout(10*1000);
            urlConnection.setReadTimeout(10*1000);
            // Get Inputstrem -> BufferedReader
            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader reader=new BufferedReader(inputStreamReader);
            // read the first line
            String toRead=reader.readLine();
            // if not null repeat it until end of file
            while(toRead!=null){
                stringBuilder.append(toRead);
                toRead=reader.readLine();
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,"----> MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG,"----> IO Exception");
            e.printStackTrace();
        } finally {
            if(urlConnection!=null) urlConnection.disconnect();
        }
        return stringBuilder.toString();
    }

    private List<NewsEntity> parseJsonToList(String jsonString) {
        List<NewsEntity> newsListToReturn = new ArrayList<>();
        try {
            JSONObject baseObject = new JSONObject(jsonString);
            JSONObject response = baseObject.getJSONObject("response");
            // todo finish parsing using restUrlQuery object

        } catch (JSONException e) {
            Log.e(LOG_TAG,"----> JSON Parse Exception");
            e.printStackTrace();
        }
        return newsListToReturn;
    }


}
