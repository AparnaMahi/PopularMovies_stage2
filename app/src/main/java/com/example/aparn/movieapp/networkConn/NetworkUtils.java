package com.example.aparn.movieapp.networkConn;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {


    public static final String TAG = NetworkUtils.class.getSimpleName();

    public static final String API_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String API_POSTER_SIZE_ORIGINAL = "original";
    public static final String API_POSTER_SIZE_W92 = "w92";
    public static final String API_POSTER_SIZE_W154 = "w154";
    public static final String API_POSTER_SIZE_W185 = "w185";
    public static final String API_POSTER_SIZE_W342 = "w342";
    public static final String API_POSTER_SIZE_W500 = "w500";
    public static final String API_POSTER_SIZE_W780 = "w780";

    private final static String URL_MOVIE_DATA = "http://api.themoviedb.org/3/movie/";
    private final static String TRAILER_IMAGE = "http://img.youtube.com/vi/";
    private final static String TRAILER_VIDEO = "https://www.youtube.com/watch?v=";

    public static String makePosterUrl(String poster) {
        return makePosterUrl(poster, API_POSTER_SIZE_ORIGINAL);
    }

    private static String makePosterUrl(String poster, String size) {
        return API_POSTER_BASE_URL + size + poster;
    }

    public static String makePosterUrl(String poster, int minWidth) {
        String size;
        if (minWidth <= 92) {
            size = API_POSTER_SIZE_W92;
        } else if (minWidth <= 154) {
            size = API_POSTER_SIZE_W154;
        } else if (minWidth <= 185) {
            size = API_POSTER_SIZE_W185;
        } else if (minWidth <= 342) {
            size = API_POSTER_SIZE_W342;
        } else if (minWidth <= 500) {
            size = API_POSTER_SIZE_W500;
        } else if (minWidth <= 780) {
            size = API_POSTER_SIZE_W780;
        } else {
            size = API_POSTER_SIZE_ORIGINAL;
        }
        return makePosterUrl(poster, size);
    }

    @Nullable
    private static URL getUrl(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // how to use apikey  - got basic idea from udacity's implementation guide
    // derived idea from stackoverflow site
    // apikey setting in resource file - got idea from slack's students and how it is done got from stackoverflow site
    public static URL buildDataUrl(String apiKey, String sort) {
        String finalPath = URL_MOVIE_DATA + sort + "?api_key=" + apiKey;
        Uri builtUri = Uri.parse(finalPath);
        return getUrl(builtUri);
   }

    public static URL buildMovieDataUrl(String id, String dataKey, String apiKey) {
        String finalPath = URL_MOVIE_DATA + id + "/" + dataKey + "?api_key=" + apiKey;
        Uri builtUri = Uri.parse(finalPath);
        return getUrl(builtUri);
    }

    public static String setTrailerImageUrl(String key) {
        return TRAILER_IMAGE + key + "/0.jpg";
    }

    public static Uri setTrailerUrl(String Trailer_Key) {
        String video_Path = TRAILER_VIDEO + Trailer_Key;
        return Uri.parse(video_Path);
    }

    public static String getResponseFromHttpUrl(URL url, Context context) throws IOException {
        if (!isOnline(context)) {
            Log.e(TAG, "No Network Connection");
            return null;
        }
        // hint from https://eventuallyconsistent.net/2011/08/02/working-with-urlconnection-and-timeouts
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(10000);
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
             boolean hasInput = scanner.hasNext();
             if (hasInput) {
                  return scanner.next();
             } else {
                scanner.close();
                return null;
             }
            } finally {
                urlConnection.disconnect();
            }
    }
    // derived from Stackoverflow
    private static boolean isOnline(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

