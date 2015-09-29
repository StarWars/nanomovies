package pl.michalstawarz.projectone_v2;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import pl.michalstawarz.projectone_v2.Database.MovieContract;
import pl.michalstawarz.projectone_v2.Database.MovieDbHelper;
import pl.michalstawarz.projectone_v2.Helpers.FetchMoviesTask;
import pl.michalstawarz.projectone_v2.Helpers.MovieModel;
import pl.michalstawarz.projectone_v2.Helpers.MoviesAdapter;
import pl.michalstawarz.projectone_v2.Helpers.MoviesApp;

/**
 * A list fragment representing a list of Movies. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link MovieDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the
 * interface.
 */
public class MovieListFragment extends Fragment {
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final String SORT_ORDER_VOTE_DESC = "vote_average.desc";
    private static final String SORT_ORDER_POPULARITY_DESC = "popularity.desc";
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sMovieCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(MovieModel selectedMovie) {
            Log.e("MovieListFragment", "fragment not attached, yet callbacks are being called");
        }
    };
    private final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sMovieCallbacks;
    private RecyclerView.LayoutManager mLayoutManager;
    private MovieModel[] mMovies;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieListFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_screen_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sort_by_popularity) {
            downloadMoviesData(SORT_ORDER_POPULARITY_DESC);
        } else if (id == R.id.action_sort_by_highest_rated) {
            downloadMoviesData(SORT_ORDER_VOTE_DESC);
        } else if (id == R.id.action_sort_by_favs){
            retrieveFavMoviesData();
        }
        else if (id == R.id.action_refresh) {
            downloadMoviesData(SORT_ORDER_POPULARITY_DESC);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    private void retrieveFavMoviesData() {

        MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor myCursor = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null, null);

        MovieModel array[] = new MovieModel[myCursor.getCount()];
        int i = 0;
        myCursor.moveToFirst();
        while (!myCursor.isAfterLast()) {
            MovieModel movieModel = new MovieModel();
            movieModel.setMovie_id(myCursor.getString(myCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
            movieModel.setPlot_overview(myCursor.getString(myCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT_OVERVIEW)));
            movieModel.setPoster_path(myCursor.getString(myCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
            movieModel.setRelease_date(myCursor.getString(myCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movieModel.setTitle(myCursor.getString(myCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            movieModel.setVote_average(myCursor.getDouble(myCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            array[i] = movieModel;
            i++;
            myCursor.moveToNext();
        }
        myCursor.close();
        mMovies = array;
        mRecyclerView.setAdapter(new MoviesAdapter(mMovies, new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                mCallbacks.onItemSelected(mMovies[position]);
            }
        }));
        mAdapter.notifyDataSetChanged();
    }


    public void downloadMoviesData(String sortOrder) {
        new FetchMoviesTask(new FetchMoviesTask.FetchMoviesListener() {
            @Override
            public void onNewMoviesData(final MovieModel[] moviesArr) {

                mMovies = moviesArr;
                if (getActivity() != null && mMovies != null) {
                    mAdapter = new MoviesAdapter(mMovies, new RecyclerViewClickListener() {
                        @Override
                        public void recyclerViewListClicked(View v, int position) {
                            mCallbacks.onItemSelected(moviesArr[position]);
                        }
                    });
                } else {
                    Log.e(LOG_TAG, "null context");
                }

                mRecyclerView.setAdapter(mAdapter);

                mAdapter.notifyDataSetChanged();

                Log.d(LOG_TAG, "Will display " + moviesArr.length + " movies");
            }

        }, FetchMoviesTask.MOVIE_DB_API_KEY, sortOrder).execute(null, null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recycler_view_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(rootView.getContext(), 2, GridLayoutManager.VERTICAL, false); //VarColumnsGridLayoutManager(rootView.getContext(), 200, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(new MoviesAdapter(new MovieModel[0], null)); // Dummy adapter while waiting for MoviesDB API data

        if (savedInstanceState != null) {
            mMovies = (MovieModel[]) savedInstanceState.getSerializable("moviesArrObj");
            mRecyclerView.setAdapter(new MoviesAdapter(mMovies, new RecyclerViewClickListener() {
                @Override
                public void recyclerViewListClicked(View v, int position) {
                    mCallbacks.onItemSelected(mMovies[position]);
                }
            }));
        } else {
            downloadMoviesData(SORT_ORDER_POPULARITY_DESC);
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sMovieCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (mActivatedPosition != ListView.INVALID_POSITION) {
//            // Serialize and persist the activated item position.
//            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
//        }

        outState.putSerializable("moviesArrObj", mMovies);

    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(MovieModel selectedMovie);
    }

//    @Override
//    public void onListItemClick(ListView listView, View view, int position, long id) {
//        super.onListItemClick(listView, view, position, id);
//
//        // Notify the active callbacks interface (the activity, if the
//        // fragment is attached to one) that an item has been selected.
//        mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
//    }

    public interface RecyclerViewClickListener {
        void recyclerViewListClicked(View v, int position);
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
//    public void setActivateOnItemClick(boolean activateOnItemClick) {
//        // When setting CHOICE_MODE_SINGLE, ListView will automatically
//        // give items the 'activated' state when touched.
//        getListView().setChoiceMode(activateOnItemClick
//                ? ListView.CHOICE_MODE_SINGLE
//                : ListView.CHOICE_MODE_NONE);
//    }
//
//    private void setActivatedPosition(int position) {
//        if (position == ListView.INVALID_POSITION) {
//            getListView().setItemChecked(mActivatedPosition, false);
//        } else {
//            getListView().setItemChecked(position, true);
//        }
//
//        mActivatedPosition = position;
//    }
}
