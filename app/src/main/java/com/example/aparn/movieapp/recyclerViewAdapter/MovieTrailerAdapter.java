package com.example.aparn.movieapp.recyclerViewAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.aparn.movieapp.R;
import com.example.aparn.movieapp.model.TrailerInfo;
import com.example.aparn.movieapp.networkConn.NetworkUtils;
import com.squareup.picasso.Picasso;
import java.util.List;


public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.TrailerViewHolder> {

    private static final String TAG = MovieTrailerAdapter.class.getSimpleName();
    private List<TrailerInfo> trailer_ItemList;
    private final Context mContext;
    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void OnListItemClick(TrailerInfo movieItem);
    }

    public MovieTrailerAdapter(List<TrailerInfo> movieTrailerList,
                               Context context,ListItemClickListener  listener) {
        trailer_ItemList = movieTrailerList;
        mContext = context;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.movie_trailer;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return trailer_ItemList == null ? 0 : trailer_ItemList.size();
    }

    public void setTrailer(List<TrailerInfo> movieTrailerList) {
        trailer_ItemList = movieTrailerList;
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView trailerYoutube;
        TrailerViewHolder(View view) {
            super(view);
            trailerYoutube = view.findViewById(R.id.iv_trailer_youtube);
            view.setOnClickListener(this);
        }

        void bind(int listIndex) {
            TrailerInfo items = trailer_ItemList.get(listIndex);
            String trailerImageUrl = NetworkUtils.setTrailerImageUrl(items.getKey());
            try {
                Picasso.with(mContext)
                        .load(trailerImageUrl)
                        .fit().centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(trailerYoutube);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.OnListItemClick(trailer_ItemList.get(clickedPosition));
        }
    }

}
