package com.example.aparn.movieapp.data;


import android.net.Uri;
import android.provider.BaseColumns;

public final class MovieContract {

    public static final String AUTHORITY = "com.example.aparn.movieapp";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    public static final String MOVIE_PATH = "movies";

    public static final class Movie implements BaseColumns {

        public static final Uri CONTENT_URI = AUTHORITY_URI.buildUpon().appendPath(MOVIE_PATH).build();
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ORIGINALTITLE = "originalTitle";
        public static final String COLUMN_NAME_POSTERPATH = "posterPath";
        public static final String COLUMN_NAME_RELEASEDATE = "releaseDate";
        public static final String COLUMN_NAME_VOTEAVERAGE = "voteaVerage";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
