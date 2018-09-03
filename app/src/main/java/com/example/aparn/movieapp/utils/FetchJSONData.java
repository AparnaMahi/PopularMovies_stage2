package com.example.aparn.movieapp.utils;

import android.util.Log;
import com.example.aparn.movieapp.model.MovieDetails;
import com.example.aparn.movieapp.model.ReviewInfo;
import com.example.aparn.movieapp.model.TrailerInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class FetchJSONData {

    private static final String TAG = FetchJSONData.class.getSimpleName();

    public static ArrayList<MovieDetails> parseMovieJson(String json) {
        try {

            MovieDetails movie;
            JSONObject object = new JSONObject(json);
            JSONArray resultsArray = new JSONArray(object.optString("results",
                    "[\"\"]"));

            ArrayList<MovieDetails> items = new ArrayList<>();
            for (int i = 0; i < resultsArray.length(); i++) {
                String current = resultsArray.optString(i, "");
                JSONObject movieJson = new JSONObject(current);

                String id = movieJson.optString("id", "0");
                String overview = movieJson.optString("overview", "Not Available");
                String original_title = movieJson.optString("original_title",
                        "Not Available");
                String poster_path = movieJson
                        .optString("poster_path", "Not Available");
                String release_date = movieJson.optString("release_date",
                        "Not Available");
                String vote_average = movieJson.optString("vote_average", "Not Available");
                movie = new MovieDetails(id, original_title, overview, poster_path, release_date, Double.parseDouble(vote_average), false);
                items.add(movie);
            }
            return items;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<ReviewInfo> parseMovieReviewJson(String json) {
        try {
            ReviewInfo review;
            JSONObject object = new JSONObject(json);
            JSONArray res_Array = new JSONArray(object.optString("results",
                    "[\"\"]"));

            ArrayList<ReviewInfo> reviewResult = new ArrayList<>();
            for (int i = 0; i < res_Array.length(); i++) {
                String res = res_Array.optString(i, "");
                JSONObject reviewsData = new JSONObject(res);

                String reviewContent = reviewsData.optString("content", "Not Available");
                String author = reviewsData.optString("author", "Not Available");

                review = new ReviewInfo(author, reviewContent);
                reviewResult.add(review);
            }
            return reviewResult;
        } catch (Exception ex) {
            Log.e(TAG ,"Error JSON Parsing in Reviews ----"+ json);
            return null;
        }
    }

    public static List<TrailerInfo> parseMovieTrailerJson(String json) {
        try {
            TrailerInfo movieTrailer;
            JSONObject object = new JSONObject(json);
            JSONArray resultsArray = new JSONArray(object.optString("results",
                    "[\"\"]"));

            ArrayList<TrailerInfo> trailerResult = new ArrayList<>();
            for (int i = 0; i < resultsArray.length(); i++) {
                String res = resultsArray.optString(i, "");
                JSONObject TrailerJson = new JSONObject(res);

                String id = TrailerJson.optString("id", "Not Available");
                String key = TrailerJson.optString("key", "Not Available");

                movieTrailer = new TrailerInfo(id, key);
                trailerResult.add(movieTrailer);
            }
            return trailerResult;

        } catch (Exception ex) {
            Log.e(TAG ,"Error JSON parsing in Trailers...."+ json);
            return null;
        }
    }
}

