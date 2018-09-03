 package com.example.aparn.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String MOVIES_DB = "MoviesDB";
    private static final int DATABASE_VERSION = 1;
    public DBHelper(Context context) {
        super(context, MOVIES_DB, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.Movie.TABLE_NAME + " (" +
                MovieContract.Movie.COLUMN_NAME_ID + " TEXT NOT NULL PRIMARY KEY, " +
                MovieContract.Movie.COLUMN_NAME_ORIGINALTITLE + " TEXT NOT NULL, " +
                MovieContract.Movie.COLUMN_NAME_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.Movie.COLUMN_NAME_POSTERPATH + " TEXT NOT NULL, " +
                MovieContract.Movie.COLUMN_NAME_RELEASEDATE + " TEXT NOT NULL, " +
                MovieContract.Movie.COLUMN_NAME_VOTEAVERAGE + " TEXT NOT NULL, " +
                MovieContract.Movie.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP " + ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + MovieContract.Movie.TABLE_NAME);
        onCreate(db);
    }
}
