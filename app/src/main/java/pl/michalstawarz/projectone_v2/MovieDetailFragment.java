package pl.michalstawarz.projectone_v2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.michalstawarz.projectone_v2.Helpers.FetchMoviesTask;
import pl.michalstawarz.projectone_v2.Helpers.MovieModel;
import pl.michalstawarz.projectone_v2.Helpers.TrailersAdapter;
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

    public interface MovieDBTrailerService {
        @GET("movie/{movie_id}/videos")
        Call<TrailersWrapper> listTrailers(@Path("movie_id") String movie_id, @QueryMap Map<String, String> options);
    }

    /**
     *  Class fields
     */
    public static final String ARG_ITEM_ID  = "item_id";
    final String QUERY_API_KEY_PARAM        = "api_key";

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
                TrailersAdapter aAdapter = new TrailersAdapter(getActivity(), (ArrayList<Trailer>)downloadedTrailerWrapper.results);

                listView.setAdapter(aAdapter);

                int totalHeight = 0;
                for (int i = 0; i < aAdapter.getCount(); i++) {
                    View listItem = aAdapter.getView(i, null, listView);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }

                ViewGroup.LayoutParams params = listView.getLayoutParams();
                float dpValue = totalHeight + (listView.getDividerHeight() * (aAdapter.getCount() - 1));
                float density = getActivity().getResources().getDisplayMetrics().density;
                params.height = (int) Math.ceil(dpValue * density); //pixels value
                listView.setLayoutParams(params);
                listView.requestLayout();

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
                Log.e("RetroFitError", "ERROR");
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
        }

        return rootView;
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
