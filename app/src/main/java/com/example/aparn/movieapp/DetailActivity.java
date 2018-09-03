package com.example.aparn.movieapp;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aparn.movieapp.data.MovieContract;
import com.example.aparn.movieapp.model.MovieDetails;
import com.example.aparn.movieapp.model.ReviewInfo;
import com.example.aparn.movieapp.model.TrailerInfo;
import com.example.aparn.movieapp.networkConn.NetworkUtils;
import com.example.aparn.movieapp.recyclerViewAdapter.MovieReviewAdapter;
import com.example.aparn.movieapp.recyclerViewAdapter.MovieTrailerAdapter;
import com.example.aparn.movieapp.utils.FetchJSONData;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, MovieTrailerAdapter.ListItemClickListener {

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String EXTRA_INDEX = "extra_index";
    private MovieDetails movieItem;
    FloatingActionButton movieFavoriteButton;

    private List<ReviewInfo> movieReviewItems;
    TextView reviewErrorView;
    RecyclerView mMovieReviewList;
    private MovieReviewAdapter reviewAdapter;

    private List<TrailerInfo> movieTrailerItems;
    TextView trailerErrorView;
    RecyclerView mMovieTrailerList;
    private MovieTrailerAdapter trailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Movie Details");
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError("No Intent");
        }

        Bundle data = getIntent().getExtras();
        if(data == null)
        {
            closeOnError("Not available.");
            return;
        }
        movieItem = data.getParcelable("movieItem");

        if (movieItem == null) {
            closeOnError("Nothing to display");
            return;
        }
        reviewErrorView=findViewById(R.id.review_errormsg);
        trailerErrorView=findViewById(R.id.trailer_errormsg);

        mMovieReviewList = findViewById(R.id.rv_movie_reviews);
        mMovieTrailerList = findViewById(R.id.rv_movie_trailers);

        movieFavoriteButton=findViewById(R.id.favorite_button);
        movieFavoriteButton.setOnClickListener(this);

        new DetailActivity.DataFrmNetworkTask().execute(new TrailerInfoFromNetwork(movieItem.getId(),getText(R.string.key_review).toString(),
                getText(R.string.api_key).toString()));
        new DetailActivity.DataFrmNetworkTask().execute(new TrailerInfoFromNetwork(movieItem.getId(),getText(R.string.key_trailer).toString(),
                getText(R.string.api_key).toString()));

        //populating all movie details
        populateUI();
    }

    private void closeOnError(String msg) {
        finish();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void populateUI() {
        TextView mv_OriginalTitle = findViewById(R.id.tv_original_title);
        TextView mv_Overview = findViewById(R.id.tv_synopsis);
        TextView mv_ReleaseDate = findViewById(R.id.tv_release_date);
        TextView mv_VoteAverage = findViewById(R.id.rbv_user_rating);
        ImageView mv_Poster = findViewById(R.id.iv_movie_poster);

        mv_OriginalTitle.setText(movieItem.getOriginalTitle());
        mv_Overview.setText(movieItem.getOverview());
        mv_ReleaseDate.setText(movieItem.getReleaseDate());
        mv_VoteAverage.setText(String.valueOf(movieItem.getVoteAverage()));
        String posterPathURL = NetworkUtils.makePosterUrl(movieItem.getPosterPath());
        try {
            Picasso.with(this)
                    .load(posterPathURL)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(mv_Poster);
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        setTitle(movieItem.getOriginalTitle());
        setFavoriteMovies();
    }

    private void setFavoriteMovies() {
        if (movieItem.isFavorite()) {
            movieFavoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            movieFavoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }
    @Override
    public void onClick(View v) {
        if (movieItem.isFavorite()) {
            RemoveFrom_FavoriteMoviesList();
        }else{
            InsertInto_FavoriteMoviesList();
        }
    }

    private void InsertInto_FavoriteMoviesList() {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.Movie.COLUMN_NAME_ID, movieItem.getId() );
        cv.put(MovieContract.Movie.COLUMN_NAME_ORIGINALTITLE, movieItem.getOriginalTitle());
        cv.put(MovieContract.Movie.COLUMN_NAME_OVERVIEW, movieItem.getOverview());
        cv.put(MovieContract.Movie.COLUMN_NAME_POSTERPATH, movieItem.getPosterPath());
        cv.put(MovieContract.Movie.COLUMN_NAME_RELEASEDATE, movieItem.getReleaseDate());
        cv.put(MovieContract.Movie.COLUMN_NAME_VOTEAVERAGE, movieItem.getVoteAverage());
        Uri uri = getContentResolver().insert(MovieContract.Movie.CONTENT_URI, cv);
        if(uri != null) {
            movieItem.setFavorite(true);
            setFavoriteMovies();
            showSnackBarMessage("Added into favorites");
        }
    }

    private void RemoveFrom_FavoriteMoviesList() {
        Uri uri = MovieContract.Movie.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieItem.getId()).build();
        int rowCount = getContentResolver().delete(uri, null, null);

        if (rowCount > 0) {
            movieItem.setFavorite(false);
            setFavoriteMovies();
            showSnackBarMessage("Removed from favorites");
        }
    }

    @Override
    public void OnListItemClick(TrailerInfo movieItem) {
        this.startActivity(new Intent(Intent.ACTION_VIEW, NetworkUtils.setTrailerUrl(movieItem.getKey())));
    }

    private static class TrailerInfoFromNetwork {
        final String id;
        final String dataKey;
        final URL getUrl;

        TrailerInfoFromNetwork(String id, String dataKey, String apiKey) {
            this.id = id;
            this.dataKey = dataKey;
            getUrl = NetworkUtils.buildMovieDataUrl(id, dataKey, apiKey);
        }
    }

    class DataFrmNetworkTask extends AsyncTask<TrailerInfoFromNetwork, Void, String> {

        String key;
        @Override
        protected String doInBackground(TrailerInfoFromNetwork... params) {
            URL Url = params[0].getUrl;
            key = params[0].dataKey;
            String Results = null;
            try {
                Results = NetworkUtils.getResponseFromHttpUrl(Url, getApplicationContext());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            return Results;
        }

        @Override
        protected void onPostExecute(String Results) {
            if (Results != null && !Results.equals("")) {
                if (key.equals(getText(R.string.key_review).toString())) {
                    movieReviewItems = FetchJSONData.parseMovieReviewJson(Results);
                    if(movieReviewItems.isEmpty())
                       displayReviewError();
                    else
                        displayReviews(movieReviewItems);
                } else {
                    movieTrailerItems = FetchJSONData.parseMovieTrailerJson(Results);
                    if(movieTrailerItems.isEmpty())
                        displayTrailerError();
                    else
                        displayTrailer(movieTrailerItems);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Nothing to display.......", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void displayReviews(List<ReviewInfo> movieReviewItems) {
        LinearLayoutManager layoutManager_Review = new LinearLayoutManager(this);
        mMovieReviewList.setLayoutManager(layoutManager_Review);
        mMovieReviewList.setHasFixedSize(false);
        reviewAdapter = new MovieReviewAdapter(movieReviewItems,this);
        mMovieReviewList.setAdapter(reviewAdapter);
        reviewAdapter.setReview(movieReviewItems);
    }

    private void displayReviewError() {
        mMovieReviewList.setVisibility(View.GONE);
        reviewErrorView.setVisibility(View.VISIBLE);
    }

    private void displayTrailerError() {
        mMovieTrailerList.setVisibility(View.GONE);
        trailerErrorView.setVisibility(View.VISIBLE);
    }

    private void displayTrailer(List<TrailerInfo> movieTrailerItems){
        LinearLayoutManager layoutManager_Trailer = new LinearLayoutManager(this);
        mMovieTrailerList.setLayoutManager(layoutManager_Trailer);
        mMovieTrailerList.setHasFixedSize(false);
        trailerAdapter = new MovieTrailerAdapter(movieTrailerItems, this, this);
        mMovieTrailerList.setAdapter(trailerAdapter);
        trailerAdapter.setTrailer(movieTrailerItems);
    }
    // hint -- https://stackoverflow.com/questions/30978457/how-to-show-snackbar-when-activity-starts
    private void showSnackBarMessage(String message) {
        Snackbar.make(findViewById(R.id.nested_scrollview) , message, Snackbar.LENGTH_SHORT).show();
    }
}

