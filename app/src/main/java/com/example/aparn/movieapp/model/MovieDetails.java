package com.example.aparn.movieapp.model;

import android.os.Parcel;
import android.os.Parcelable;


// derived code from site - http://www.vogella.com/tutorials/AndroidParcelable/article.html
// basic knowledge of using Parcelable got from udacity's example app
public class MovieDetails implements Parcelable
{
    public final String originalTitle;
    public final String overview;
    public final String posterPath;
    public final String releaseDate;
    public final double voteAverage;
    public final String id;

    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    private boolean isFavorite;

    public MovieDetails(String id, String originalTitle, String overview,
                        String posterPath, String releaseDate, double voteAverage, Boolean isFavorite) {
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.id=id;
        this.isFavorite=isFavorite;
    }

    private MovieDetails(Parcel in) {
        originalTitle = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        this.voteAverage = (Double) in.readValue(Double.class.getClassLoader());
        id = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<MovieDetails> CREATOR = new Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getOverview() {
        return overview;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getId() {
        return id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeValue(voteAverage);
        dest.writeString(id);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}
