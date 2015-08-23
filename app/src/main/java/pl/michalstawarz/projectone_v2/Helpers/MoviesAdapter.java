package pl.michalstawarz.projectone_v2.Helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import pl.michalstawarz.projectone_v2.MovieListFragment;
import pl.michalstawarz.projectone_v2.R;

/**
 * Created by propr_000 on 18.08.2015.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private static MovieListFragment.RecyclerViewClickListener itemListener;
    private final String LOG_TAG = MoviesAdapter.class.getSimpleName();
    private MovieModel[] mDataset;
    private Context mParentContext;

    public MoviesAdapter(MovieModel[] moviesDataset, MovieListFragment.RecyclerViewClickListener itemListener) {
        mDataset = moviesDataset;
        MoviesAdapter.itemListener = itemListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(LOG_TAG, "##### \n ON CREATE VIEWHOLDER \n ######");

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_poster_layout, parent, false);
        mParentContext = parent.getContext();
        MovieViewHolder vh = new MovieViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder viewHolder, int position) {
        MovieModel model = mDataset[position];
        Log.d(LOG_TAG, "##### \n ON BIND VIEWHOLDER \n " + model.poster_path + "\n######");


        ImageView imgV = viewHolder.mImageView;
        //imgV.setLayoutParams(new RecyclerView.LayoutParams(85, 185));
        //imgV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //imgV.setPadding(8, 8, 8, 8);
        Picasso.with(mParentContext).load("http://image.tmdb.org/t/p/w300/" + model.poster_path).into(imgV);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public MovieModel getMovieAtIndex(int index) {
        return mDataset[index];
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;

        public MovieViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mImageView = (ImageView) view.findViewById(R.id.poster_layout_imageView);
        }

        @Override
        public void onClick(View view) {
            itemListener.recyclerViewListClicked(view, getLayoutPosition());
            Log.d(MovieViewHolder.class.getSimpleName(), "onClick " + getAdapterPosition() + " ");
        }
    }
}
