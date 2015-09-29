package pl.michalstawarz.projectone_v2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import pl.michalstawarz.projectone_v2.Database.MovieContract;
import pl.michalstawarz.projectone_v2.Database.MovieDbHelper;
import pl.michalstawarz.projectone_v2.Helpers.MovieModel;
import pl.michalstawarz.projectone_v2.Helpers.MoviesApp;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MovieListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link MovieDetailFragment}.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private MovieModel movie;

    public void setCurrentMovie(MovieModel model) {
        this.movie = model;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable(MovieDetailFragment.ARG_ITEM_ID,
                    getIntent().getSerializableExtra(MovieDetailFragment.ARG_ITEM_ID));

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MovieListActivity.class));

            return true;
        } else if (id == R.id.action_share) {
            Log.e("MENU_BTN", "SHARE");
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void favMovie(View view) {
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
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);
        if (movieRowId != -1) {
            Log.e("DB", "SUCCESS");
        }
        else {
            Log.e("DB", "FAILURE");
        }
    }
}
