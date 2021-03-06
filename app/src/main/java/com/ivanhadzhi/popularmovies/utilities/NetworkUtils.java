/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ivanhadzhi.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.ivanhadzhi.popularmovies.BuildConfig;
import com.ivanhadzhi.popularmovies.model.ImageSize;
import com.ivanhadzhi.popularmovies.model.SortBy;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the movies database servers.
 */
public final class NetworkUtils {

    private static final int CONNECTION_TIMEOUT = 1000 /* milliseconds */ * 20 /* seconds */;

    private NetworkUtils() {}

    /**
     * To prospective reviewers: please replace the value of the API_KEY with your own Movie DB api key
     */
    private static final String API_KEY = BuildConfig.MOVIE_DB_KEY;

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

    private static final String API_KEY_PARAM = "api_key";

    public static URL getImageURL(ImageSize size, String posterPath) {
        Uri imageUri = Uri.parse(BASE_IMAGE_URL + size + posterPath).buildUpon().build();
        try {
            return new URL(imageUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public static URL getURL(SortBy sortBy) {

        Uri.Builder moviesDbUriBuilder = Uri.parse(BASE_URL + sortBy).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY);
        Uri moviesUri = moviesDbUriBuilder.build();

        try {
            URL moviesUrl = new URL(moviesUri.toString());
            Log.v(TAG, "URL: " + moviesUrl);
            return moviesUrl;
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        try
            (InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in)) {
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}