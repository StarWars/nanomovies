package pl.michalstawarz.projectone_v2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import pl.michalstawarz.projectone_v2.Helpers.MovieModel;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The movie content this fragment is presenting.
     */
    private MovieModel movie;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            movie = (MovieModel) getArguments().get(ARG_ITEM_ID);
            Log.e("Fragment", "movie model: " + movie);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (movie != null) {
//            ((TextView) rootView.findViewById(R.id.movie_detail)).setText(movie.getTitle());

            ((TextView) rootView.findViewById(R.id.movie_title_txt_view)).setText(movie.getTitle());
            ((TextView) rootView.findViewById(R.id.movie_year_txt_view)).setText(movie.getRelease_date());
            ((TextView) rootView.findViewById(R.id.movie_rating_txt_view)).setText(movie.getVote_average() + "");
            ((TextView) rootView.findViewById(R.id.movie_description_txt_view)).setText(movie.getPlot_overview());
            Picasso.with(rootView.getContext()).load("http://image.tmdb.org/t/p/w300/" + movie.getPoster_path()).into(((ImageView) rootView.findViewById(R.id.movie_poster_img_view)));

        }

        return rootView;
    }

    public void setMovieObject(MovieModel movie) {
        this.movie = movie;
    }
}
