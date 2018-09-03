package com.example.aparn.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieContentProvider extends ContentProvider {

    private static final int MOVIES = 1;
    private static final int MOVIE_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private DBHelper DB_Helper;

    static {
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MOVIE_PATH, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MOVIE_PATH + "/*", MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        DB_Helper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = DB_Helper.getReadableDatabase();
        int match = uriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case MOVIES:
                retCursor = db.query(MovieContract.Movie.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        MovieContract.Movie.COLUMN_TIMESTAMP);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if (context != null) {
            retCursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = DB_Helper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        Uri resultUri;
        switch (match) {
            case MOVIES:
                long id = db.insert(MovieContract.Movie.TABLE_NAME, null, values);
                if (id > 0) {
                    resultUri = ContentUris.withAppendedId(MovieContract.Movie.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Not inserted... " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Error in uri: " + uri);
        }
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = DB_Helper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int del_movies;
        switch (match) {
            case MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                del_movies = db.delete(MovieContract.Movie.TABLE_NAME, "id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (del_movies != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return del_movies;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
