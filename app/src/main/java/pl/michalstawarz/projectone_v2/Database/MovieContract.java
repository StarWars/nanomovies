package pl.michalstawarz.projectone_v2.Database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by propr_000 on 19.09.2015.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "pl.michalstawarz.projectone_v2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY );
    public static final String PATH_FAV_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV_MOVIE).build();

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_PLOT_OVERVIEW = "plot_overview";

        public static final String COLUMN_FAV = "is_favourite";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildMovieUriWithMovieId(String movie_id) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_MOVIE_ID, movie_id).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
