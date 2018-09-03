package com.example.aparn.movieapp.recyclerViewAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.aparn.movieapp.R;
import com.example.aparn.movieapp.model.ReviewInfo;
import java.util.List;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ReviewViewHolder> {

    private static final String TAG = MovieReviewAdapter.class.getSimpleName();
    private List<ReviewInfo> reviewItemList;
    private final Context mContext;

    public MovieReviewAdapter(List<ReviewInfo> movieReviewItemList,
                              Context context) {
        reviewItemList = movieReviewItemList;
        mContext = context;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.movie_reviews;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewAdapter.ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return reviewItemList == null ? 0 : reviewItemList.size();
    }

    public void setReview(List<ReviewInfo> movieReviewItems) {
        reviewItemList = movieReviewItems;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView authorView;
        final TextView contentView;

        ReviewViewHolder(View view) {
            super(view);
            authorView = view.findViewById(R.id.tv_review_author);
            contentView = view.findViewById(R.id.tv_review_content);
            view.setOnClickListener(this);
        }

        void bind(int listIndex) {
            ReviewInfo review = reviewItemList.get(listIndex);
            authorView.setText(review.getAuthor());
            contentView.setText(review.getContent());
        }

        @Override
        public void onClick(View view) {
        }
    }
}

