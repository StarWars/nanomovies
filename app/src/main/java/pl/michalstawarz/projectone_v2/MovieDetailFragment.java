package pl.michalstawarz.projectone_v2;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.michalstawarz.projectone_v2.Database.MovieContract;
import pl.michalstawarz.projectone_v2.Database.MovieDbHelper;
import pl.michalstawarz.projectone_v2.Helpers.FetchMoviesTask;
import pl.michalstawarz.projectone_v2.Helpers.MovieModel;
import pl.michalstawarz.projectone_v2.Helpers.MovieDetailsAdapter;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */


public class MovieDetailFragment extends Fragment {
    private TrailersWrapper downloadedTrailerWrapper;
    private ReviewsWrapper downloadedReviewsWrapper;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private class TrailersWrapper {

        public final int id;
        public final List<Trailer> results;

        public TrailersWrapper(int id, List<Trailer> results){
            this.id = id;
            this.results = results;

        }
    }

    private class ReviewsWrapper {

        public final int id;
        public final int page;
        public final int total_pages;
        public final int total_results;
        public final List<Review> results;

        public ReviewsWrapper(int id, int page, List<Review> results, int total_pages, int total_results) {
            this.id = id;
            this.page = page;
            this.results = results;
            this.total_pages = total_pages;
            this.total_results = total_results;
        }
    }

    /**
     *  JSON mapping objects
     */
    public static class Trailer {
        public final String id;
        public final String iso_639_1;
        public final String key;
        public final String site;
        public final int size;
        public final String name;
        public final String type;

        public Trailer(String id, String iso_639_1, String key, String site, int size, String name, String type) {
            this.id = id;
            this.iso_639_1 = iso_639_1;
            this.key = key;
            this.site = site;
            this.size = size;
            this.name = name;
            this.type = type;
        }

    }

    public class Review {

        public final String id;
        public final String author;
        public final String content;
        public final String url;

        public Review(String id, String author, String content, String url) {
            this.id = id;
            this.author = author;
            this.content = content;
            this.url = url;
        }
    }

    public interface MovieDBTrailerService {
        @GET("movie/{movie_id}/videos")
        Call<TrailersWrapper> listTrailers(@Path("movie_id") String movie_id, @QueryMap Map<String, String> options);
    }

    public interface MovieDBReviewsService {
        @GET("movie/{movie_id}/reviews")
        Call<ReviewsWrapper> listReviews(@Path("movie_id") String movie_id, @QueryMap Map<String, String> options);
    }

    /**
     *  Class fields
     */
    public static final String ARG_ITEM_ID      = "item_id";
    final String QUERY_API_KEY_PARAM            = "api_key";
    final String QUERY_REVIEW_PAGE_PARAM        = "page";

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

        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_ITEM_ID)) {
                movie = (MovieModel) getArguments().get(ARG_ITEM_ID);
                ((MovieDetailActivity) getActivity()).setCurrentMovie(movie);
                Log.e("Fragment", "movie model: " + movie);
            }
        }
    }

    public void downloadTrailers(final View rootView) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();



        MovieDBTrailerService service = retrofit.create(MovieDBTrailerService.class);
        Map <String, String> hm = new HashMap<>();
        hm.put(QUERY_API_KEY_PARAM, FetchMoviesTask.MOVIE_DB_API_KEY);
        Call<TrailersWrapper> trailers = service.listTrailers(movie.getMovie_id(), hm);

        trailers.enqueue(new Callback<TrailersWrapper>() {
            @Override
            public void onResponse(Response<TrailersWrapper> response) {
                downloadedTrailerWrapper = response.body();

                Log.d("RetroFit", "Downloaded # of trailers: " + downloadedTrailerWrapper.results.toArray().length);

                ListView listView = (ListView) rootView.findViewById(R.id.movie_trailers_listView);
                MovieDetailsAdapter aAdapter = new MovieDetailsAdapter(getActivity(), (ArrayList<Trailer>) downloadedTrailerWrapper.results);

                listView.setAdapter(aAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View v, int position,
                                            long arg3) {
                        Trailer tr = (Trailer) adapter.getItemAtPosition(position);
                        Log.d("ListViewClick", "CLICK " + tr.site + tr.key);

                        if (appInstalledOrNot("com.google.android.youtube")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("vnd.youtube:"
                                            + tr.key));
                            startActivity(intent);
                        } else {

                            startActivity(new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/v/" + tr.key)));
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                Log.e("RetroFitError", "TRAILERS ERROR");
            }
        });
    }

    public void downloadReviews(final View rootView) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        MovieDBReviewsService service = retrofit.create(MovieDBReviewsService.class);
        Map <String, String> hm = new HashMap<>();
        hm.put(QUERY_API_KEY_PARAM, FetchMoviesTask.MOVIE_DB_API_KEY);
        hm.put(QUERY_REVIEW_PAGE_PARAM, "1");
        Call<ReviewsWrapper> reviews = service.listReviews(movie.getMovie_id(), hm);

        reviews.enqueue(new Callback<ReviewsWrapper>() {
            @Override
            public void onResponse(Response<ReviewsWrapper> response) {
                downloadedReviewsWrapper = response.body();
                Log.d("RetroFit", "Downloaded # of reviews: " + downloadedReviewsWrapper.results.toArray().length);

                ListView listView = (ListView) rootView.findViewById(R.id.movie_reviews_listView);
                MovieDetailsAdapter aAdapter = new MovieDetailsAdapter(getActivity(), (ArrayList<Review>) downloadedReviewsWrapper.results);

                listView.setAdapter(aAdapter);
            }

            @Override
            public void onFailure(Throwable t) {
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                Log.e("RetroFitError", "REVIEWS ERROR");
            }
        });

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

            downloadTrailers(rootView);
            downloadReviews(rootView);

            configureFavButton(rootView.findViewById(R.id.favourite_button));

            rootView.findViewById(R.id.favourite_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favMovie(view);
                }
            });
        }

        return rootView;
    }
    private void configureFavButton(View view) {

        Button buttonPressed = (Button) view;

        MovieDbHelper dbHelper = new MovieDbHelper(buttonPressed.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(MovieContract.MovieEntry.TABLE_NAME, null, MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + movie.getMovie_id(), null, null, null, null, null);

        Button favBtn = (Button) view;
        if(c.getCount() > 0) {
            favBtn.setText("LIKE");
            favBtn.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
            favBtn.invalidate();
        } else {
            favBtn.setText("LIKE");
            favBtn.getBackground().clearColorFilter();
            favBtn.invalidate();
        }

        c.close();
    }

    public void favMovie(View view){
        Button buttonPressed = (Button) view;

        MovieDbHelper dbHelper = new MovieDbHelper(view.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovie_id());
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_OVERVIEW, movie.getPlot_overview());
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster_path());
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getRelease_date());
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVote_average());

        long movieRowId;
        Cursor c = db.query(MovieContract.MovieEntry.TABLE_NAME, null, MovieContract.MovieEntry.COLUMN_MOVIE_ID +"="+movie.getMovie_id(), null, null, null, null,null);
        if(c.getCount() > 0){
            movieRowId = db.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?", new String[]{movie.getMovie_id()});
            if (movieRowId != -1) {
                Log.v("DB", "REMOVAL SUCCESS");
            }
            else {
                Log.v("DB", "FAILURE");
            }
        } else {
            movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);
            if (movieRowId != -1) {
                Log.v("DB", "SUCCESS");
            }
            else {
                Log.v("DB", "FAILURE");
            }
        }
        configureFavButton(buttonPressed);
        c.close();
        db.close();
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
    public void setMovieObject(MovieModel movie) {
        this.movie = movie;
    }

}
