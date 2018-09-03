package com.example.aparn.movieapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.aparn.movieapp.data.MovieContract;
import com.example.aparn.movieapp.networkConn.NetworkUtils;
import com.example.aparn.movieapp.model.MovieDetails;
import com.example.aparn.movieapp.utils.FetchJSONData;
import com.example.aparn.movieapp.recyclerViewAdapter.MovieListAdapter;

//how to use gridlayout,appbarlayout,toolbar derived from tutorials
public class MainActivity extends AppCompatActivity implements MovieListAdapter.ListItemClickListener{

    private MovieListAdapter movieDetAdapter;
    private RecyclerView movieListView;
    private static final String SORT_POPULAR = "popular";
    private static final String SORT_TOP_RATED = "top_rated";
    private static String currentSort = SORT_POPULAR;
    private static final String MOVIE_LIST_KEY = "MOVIE_LIST_KEY";
    private static final String CURRENT_SORT_KEY = "CURRENT_SORT_KEY";
    private ArrayList<MovieDetails> movieItems;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FAVORITE = "Favorite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        movieListView = findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        movieListView.setLayoutManager(layoutManager);
        movieListView.setHasFixedSize(true);
        movieDetAdapter = new MovieListAdapter(movieItems, this, this);
        movieListView.setAdapter(movieDetAdapter);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_LIST_KEY)) {
            movieItems = savedInstanceState.getParcelableArrayList(MOVIE_LIST_KEY);
            currentSort = savedInstanceState.getString(CURRENT_SORT_KEY, SORT_POPULAR);
        }
        displayMovieDetails();
    }
    private void displayMovieDetails() {
        if (movieItems == null || movieItems.isEmpty()) {
            if (currentSort.equals(FAVORITE)) {
                new FavoriteMoviesDBTask().execute();
            } else {
                new NetworkConnTask().execute(NetworkUtils.buildDataUrl(getText(R.string.api_key).toString(), currentSort));
            }
        } else
            movieDetAdapter.displayList(movieItems);
    }

    public class NetworkConnTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String responseFromHttpUrl = null;
            try {
                responseFromHttpUrl = NetworkUtils.getResponseFromHttpUrl(searchUrl, getApplicationContext());

            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            return responseFromHttpUrl;
        }

        @Override
        protected void onPostExecute(String responseFromHttpUrl) {
            if (responseFromHttpUrl != null && !responseFromHttpUrl.equals("")) {
                movieItems = FetchJSONData.parseMovieJson(responseFromHttpUrl);
                movieDetAdapter.displayList(movieItems);
                updateFav_Items();
            } else {
                movieItems =new ArrayList<>();
                movieDetAdapter.displayList(movieItems);
                Toast.makeText(getApplicationContext(), " Network connection problem.Can't load movies..", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuitems, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_most_popular && !currentSort.equals(SORT_POPULAR)) {
            resetMovieItems();
            currentSort = SORT_POPULAR;
            displayMovieDetails();
            return true;
        }
        if (id == R.id.action_sort_top_rated && !currentSort.equals(SORT_TOP_RATED)) {
            resetMovieItems();
            currentSort = SORT_TOP_RATED;
            displayMovieDetails();
            return true;
        }
        if (id == R.id.action_favorite && !currentSort.equals(FAVORITE)) {
            resetMovieItems();
            currentSort = FAVORITE;
            displayMovieDetails();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        switch (currentSort) {
            case SORT_TOP_RATED:
                menu.findItem(R.id.action_sort_top_rated).setChecked(true);
                break;
            case SORT_POPULAR:
                menu.findItem(R.id.action_sort_most_popular).setChecked(true);
                break;
            default:
                menu.findItem(R.id.action_favorite).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movieItems = savedInstanceState.getParcelableArrayList(MOVIE_LIST_KEY);
        currentSort = savedInstanceState.getString(CURRENT_SORT_KEY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_KEY, movieItems);
        outState.putString(CURRENT_SORT_KEY, currentSort);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentSort.equals(FAVORITE)) {
            resetMovieItems();
            displayMovieDetails();
        }
    }

    @Override
    public void OnListItemClick(MovieDetails movieItem) {
        Intent myIntent = new Intent(this, DetailActivity.class);
        myIntent.putExtra(DetailActivity.EXTRA_INDEX, 1);
        myIntent.putExtra("movieItem", movieItem);
        startActivity(myIntent);
    }
    private void resetMovieItems() {
        if (movieItems != null) {
            movieItems.clear();
            movieDetAdapter.notifyDataSetChanged();
        }
    }

    private void updateFav_Items() {
        ArrayList<MovieDetails> favoriteMovieItems = getAllFavoriteMovies();
        boolean found = false;
        for (int j = 0; j < movieItems.size(); j++) {
            MovieDetails item = movieItems.get(j);
            for (int i = 0; i < favoriteMovieItems.size(); i++) {
                MovieDetails temp = favoriteMovieItems.get(i);
                if (temp.getId().equals(item.getId())) {
                    item.setFavorite(true);
                    movieItems.set(j, item);
                    break;
                }
            }
        }
    }

    class FavoriteMoviesDBTask extends AsyncTask<Void, Void, List<MovieDetails>> {

        @Override
        protected List<MovieDetails> doInBackground(Void... voids) {

            List<MovieDetails> Results = null;
            try {
                Results = getAllFavoriteMovies();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return Results;
        }

        @Override
        protected void onPostExecute(List<MovieDetails> Results) {
            if (Results != null) {
                movieDetAdapter.displayList(Results);
            } else {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private ArrayList<MovieDetails> getAllFavoriteMovies() {
        Cursor cursor = getContentResolver().query(MovieContract.Movie.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.Movie.COLUMN_NAME_VOTEAVERAGE);

        ArrayList<MovieDetails> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_NAME_ID));
                Log.d(TAG, id);

                result.add(new MovieDetails(
                        cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_NAME_ORIGINALTITLE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_NAME_OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_NAME_POSTERPATH)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_NAME_RELEASEDATE)),
                        cursor.getDouble(cursor.getColumnIndex(MovieContract.Movie.COLUMN_NAME_VOTEAVERAGE)),
                        true
                ));
            }
        } finally {
            cursor.close();
        }
        return result;
    }
}


