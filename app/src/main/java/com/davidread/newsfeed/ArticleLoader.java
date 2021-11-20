package com.davidread.newsfeed;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ArticleLoader} is a utility class that provides an {@link AsyncTaskLoader} for requesting
 * and retrieving data from The Guardian API. More specifically, it allows you to get article
 * listings, where you can specify how the listings are sorted, the page index for pagination, and
 * an optional query term.
 */
public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    /**
     * {@link String} log tag name for {@link ArticleLoader}.
     */
    public static final String LOG_TAG_NAME = ArticleLoader.class.getSimpleName();

    /**
     * {@link String} for specifying what order article listings will be returned in. Possible
     * values include "newest", "oldest", and "relevance".
     */
    private final String orderBy;

    /**
     * int index for specifying which page result set will be returned. Page indices start at 1 and
     * 50 article listings are returned in each page result set.
     */
    private final int pageIndex;

    /**
     * {@link String} for requesting article listings containing certain free text. To exclude a
     * query from the request, simply pass this parameter as null or the empty string.
     */
    private final String query;

    /**
     * Constructs a new {@link ArticleLoader} object.
     *
     * @param context   {@link Context} for the superclass.
     * @param orderBy   {@link String} for specifying what order article listings will be returned
     *                  in.
     * @param pageIndex int index for specifying which page result set will be returned.
     * @param query     {@link String} for requesting article listings containing certain free text.
     */
    public ArticleLoader(@NonNull Context context, String orderBy, int pageIndex, String query) {
        super(context);
        this.pageIndex = pageIndex;
        this.orderBy = orderBy;
        this.query = query;
    }

    /**
     * Callback method invoked directly before executing the actual load. It calls forceLoad() to
     * start the loader.
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Callback method invoked to perform the actual load on a worker thread and return the result.
     * It simply returns {@link ArticleLoader#getArticlesFromTheGuardianAPI(String, int, String)}.
     */
    @Nullable
    @Override
    public List<Article> loadInBackground() {
        return getArticlesFromTheGuardianAPI(orderBy, pageIndex, query);
    }

    /**
     * Returns a {@link List} of {@link Article} objects fetched via a network request to The
     * Guardian API.
     *
     * @param orderBy   {@link String} for specifying what order results will be returned in.
     * @param pageIndex int index representing which page result set will be returned.
     * @param query     {@link String} for requesting listings containing this free text.
     * @return {@link List} of {@link Article} objects fetched via a network request to The Guardian
     * API.
     */
    private List<Article> getArticlesFromTheGuardianAPI(String orderBy, int pageIndex, String query) {

        // Construct URL object.
        URL url = constructUrl(orderBy, pageIndex, query);

        // Perform network request.
        String json = null;
        try {
            json = getJsonFromUrl(url);
        } catch (IOException e) {
            cancelLoad();
            Log.e(LOG_TAG_NAME, "Error closing input stream", e);
        }

        // Return List of Article objects extracted from JSON string.
        return extractArticlesFromJson(json);
    }

    /**
     * Returns a {@link URL} object for requesting article listings from The Guardian API.
     *
     * @param orderBy   {@link String} for specifying what order results will be returned in.
     * @param pageIndex int index representing which page result set will be returned.
     * @param query     {@link String} for requesting listings containing this free text.
     * @return {@link URL} object for requesting article listings from The Guardian API.
     */
    private URL constructUrl(String orderBy, int pageIndex, String query) {

        // Construct string URL.
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https")
                .authority("content.guardianapis.com")
                .appendPath("search")
                .appendQueryParameter("api-key", "e2b2fb18-0d25-484d-b3e3-72e4562b3077")
                .appendQueryParameter("format", "json")
                .appendQueryParameter("order-by", orderBy)
                .appendQueryParameter("page", Integer.toString(pageIndex))
                .appendQueryParameter("page-size", "50");
        if (query != null && !query.isEmpty()) {
            uriBuilder.appendQueryParameter("q", query);
        }
        String stringUrl = uriBuilder.build().toString();

        // Construct URL object.
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            cancelLoad();
            Log.e(LOG_TAG_NAME, "Error constructing URL object", e);
        }
        return url;
    }

    /**
     * Performs the network request specified by the given {@link URL} object and returns a
     * {@link String} JSON response returned from the request.
     *
     * @param url {@link URL} object specifying how to make the network request.
     * @return {@link String} JSON response returned from the request.
     */
    private String getJsonFromUrl(URL url) throws IOException {

        // Initialize network request objects.
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        String json = null;

        try {
            // Setup the network request and execute it.
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                /* If the request is successful, get the input stream from the request and convert
                 * it into a JSON string. */
                inputStream = httpURLConnection.getInputStream();
                json = parseJsonFromInputStream(inputStream);
            } else {
                cancelLoad();
                Log.e(LOG_TAG_NAME, "Network request failed with response code " + responseCode);
            }

        } catch (IOException e) {
            cancelLoad();
            Log.e(LOG_TAG_NAME, "Error performing network request", e);
        } finally {
            // Cleanup network request objects.
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return json;
    }

    /**
     * Parses each character returned from an {@link InputStream} into a {@link String} JSON
     * response.
     *
     * @param inputStream {@link InputStream} to be parsed.
     * @return {@link String} JSON response parsed from an {@link InputStream}.
     */
    private String parseJsonFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder jsonStringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String bufferedReaderLine = bufferedReader.readLine();
        while (bufferedReaderLine != null) {
            jsonStringBuilder.append(bufferedReaderLine);
            bufferedReaderLine = bufferedReader.readLine();
        }
        return jsonStringBuilder.toString();
    }

    /**
     * Parses a {@link String} JSON response into a {@link List} of {@link Article} objects.
     *
     * @param json {@link String} JSON response from a The Guardian API content search.
     * @return {@link List} of {@link Article} objects parsed from a JSON response.
     */
    private List<Article> extractArticlesFromJson(String json) {

        if (json == null) {
            cancelLoad();
        }

        List<Article> articles = new ArrayList<>();

        // Get results JSON array from the JSON string.
        JSONArray resultsJsonArray = null;
        try {
            JSONObject rootJsonObject = new JSONObject(json);
            JSONObject responseJsonObject = rootJsonObject.getJSONObject("response");
            resultsJsonArray = responseJsonObject.getJSONArray("results");
        } catch (JSONException e) {
            cancelLoad();
            Log.e(LOG_TAG_NAME, "Error parsing the results JSON array", e);
        }

        // Iterate through the results JSON array.
        for (int resultsIndex = 0; resultsIndex < resultsJsonArray.length(); resultsIndex++) {

            // Get the current result object.
            JSONObject resultJSONObject;
            try {
                resultJSONObject = resultsJsonArray.getJSONObject(resultsIndex);
            } catch (JSONException e) {
                Log.e(LOG_TAG_NAME, "Error parsing the result JSON object with index " + resultsIndex, e);
                // Skip to the next result if a null current result is parsed.
                continue;
            }

            // Get the properties for this result.
            String webTitle = "";
            try {
                webTitle = resultJSONObject.getString("webTitle");
            } catch (JSONException e) {
                Log.e(LOG_TAG_NAME, "Error parsing the webTitle JSON property for the result with index " + resultsIndex, e);
            }

            String sectionName = "";
            try {
                sectionName = resultJSONObject.getString("sectionName");
            } catch (JSONException e) {
                Log.e(LOG_TAG_NAME, "Error parsing the sectionName JSON property for the result with index " + resultsIndex, e);
            }

            String webPublicationDate = "";
            try {
                webPublicationDate = resultJSONObject.getString("webPublicationDate");
            } catch (JSONException e) {
                Log.e(LOG_TAG_NAME, "Error parsing the webPublicationDate JSON property for the result with index " + resultsIndex, e);
            }

            String webUrl = "";
            try {
                webUrl = resultJSONObject.getString("webUrl");
            } catch (JSONException e) {
                Log.e(LOG_TAG_NAME, "Error parsing the webUrl JSON property for the result with index " + resultsIndex, e);
            }

            // Add a new Article object for this result.
            articles.add(new Article(webTitle, sectionName, webPublicationDate, webUrl));
        }

        return articles;
    }
}
