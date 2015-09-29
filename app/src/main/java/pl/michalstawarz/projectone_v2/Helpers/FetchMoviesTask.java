package pl.michalstawarz.projectone_v2.Helpers;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Michal Stawarz on 2015-08-11.
 */
public class FetchMoviesTask extends AsyncTask<Void, Void, MovieModel[]> {

    public static String MOVIE_DB_API_KEY = "";

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    FetchMoviesListener listener;
    String mAPI_KEY;
    String mSORT_ORDER;

    public FetchMoviesTask(FetchMoviesListener listener, String apiKey, String sortOrder) {
        this.listener = listener;
        mAPI_KEY = apiKey;
        mSORT_ORDER = sortOrder;
    }

    @Override
    protected MovieModel[] doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJSONStr = null;

        try {
            final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String QUERY_API_KEY_PARAM = "api_key";
            final String SORT_BY_PARAM = "sort_by";
            final String VOTE_COUNT_GTE = "vote_count.gte";

            Uri builtUri;

            if (mSORT_ORDER == "vote_average.desc") {
                builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, mSORT_ORDER)
                        .appendQueryParameter(VOTE_COUNT_GTE, "3000")
                        .appendQueryParameter(QUERY_API_KEY_PARAM, mAPI_KEY)
                        .build();
            } else {
                builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, mSORT_ORDER)
                        .appendQueryParameter(QUERY_API_KEY_PARAM, mAPI_KEY)
                        .build();
            }


            Log.d(LOG_TAG, "Built URI" + builtUri.toString());


            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                // Nothing to do.
                moviesJSONStr = null;
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                moviesJSONStr = null;
                return null;
            }
            moviesJSONStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            moviesJSONStr = null;
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            Log.d(LOG_TAG, "Movies JSON String: " + moviesJSONStr);
            return getMoviesDataFromJson(moviesJSONStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private MovieModel[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        // Names of JSON objects to extract
        // Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
        final String MD_MOVIE_POSTER_ID = "poster_path";
        final String MD_RELEASE_DATE = "release_date";
        final String MD_MOVIE_TITLE = "title";
        final String MD_VOTE_AVERAGE = "vote_average";
        final String MD_PLOT_SYNOPSIS = "overview";
        final String MD_MOVIE_ID    = "id";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray("results");

        MovieModel[] movies = new MovieModel[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieEntry = moviesArray.getJSONObject(i);

            MovieModel movieModel = new MovieModel();
            movieModel.poster_path = movieEntry.getString(MD_MOVIE_POSTER_ID);
            movieModel.release_date = movieEntry.getString(MD_RELEASE_DATE);
            movieModel.title = movieEntry.getString(MD_MOVIE_TITLE);
            movieModel.vote_average = movieEntry.getDouble(MD_VOTE_AVERAGE);
            movieModel.plot_overview = movieEntry.getString(MD_PLOT_SYNOPSIS);
            movieModel.movie_id = movieEntry.getString(MD_MOVIE_ID);

            movies[i] = movieModel;
        }

        return movies;
    }

    @Override
    protected void onPostExecute(MovieModel[] result) {
        if (result != null) {
            listener.onNewMoviesData(result);
        }
    }

    public interface FetchMoviesListener {
        void onNewMoviesData(MovieModel[] moviesArr);
    }
}
