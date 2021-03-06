package pl.michalstawarz.projectone_v2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import pl.michalstawarz.projectone_v2.Database.MovieContract;
import pl.michalstawarz.projectone_v2.Database.MovieDbHelper;
import pl.michalstawarz.projectone_v2.Helpers.FetchMoviesTask;
import pl.michalstawarz.projectone_v2.Helpers.MovieModel;
import pl.michalstawarz.projectone_v2.Helpers.MoviesApp;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link MovieListFragment} and the item details
 * (if present) is a {@link MovieDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link MovieListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class MovieListActivity extends ActionBarActivity implements MovieListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private MovieModel selectedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
//            ((MovieListFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.movies_recycler_view))
//                    .setActivateOnItemClick(true);
        }
    }


    /**
     * Callback method from {@link MovieListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(MovieModel selectedMovie) {
        Log.e("CAAT", "item selected " + selectedMovie.getTitle());
        this.selectedMovie = selectedMovie;
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
//            Bundle arguments = new Bundle();
//            arguments.putString(MovieDetailFragment.ARG_ITEM_ID, selectedMovie.getTitle());
            MovieDetailFragment fragment = new MovieDetailFragment();
//            fragment.setArguments(arguments);
            fragment.setMovieObject(selectedMovie);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra(MovieDetailFragment.ARG_ITEM_ID, selectedMovie);
            startActivity(detailIntent);
        }
    }
}
