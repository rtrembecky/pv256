package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static cz.muni.fi.pv256.movio2.uco_422536.DownloadService.DOWNLOAD;
import static cz.muni.fi.pv256.movio2.uco_422536.DownloadService.OK;
import static cz.muni.fi.pv256.movio2.uco_422536.DownloadService.STATUS;
import static cz.muni.fi.pv256.movio2.uco_422536.DownloadService.UPCOMING;

/**
 * Created by Richard on 14.12.2017.
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>>{

    public static final String TAG = MainFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ViewStub mViewStub;

    private Context mContext;
    private MovieAdapter mMovieAdapter;
    private OnMovieSelectListener mListener;
    private MovieDownloadBroadcastReceiver mReceiver;
    private SQLiteDatabase mDatabase;
    private MovieManager mMovieManager;
    private MovieDbHelper mDbHelper;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            mListener = (OnMovieSelectListener) activity;
        } catch (ClassCastException e) {
            if(BuildConfig.logging)
                Log.e(TAG, "Activity must implement OnMovieSelectListener", e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (BuildConfig.logging) Log.e("MainFragment", "onCreate");
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (BuildConfig.logging) Log.w(MainFragment.class.getSimpleName(), "onResume");
        mReceiver = new MovieDownloadBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(DOWNLOAD);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        if (BuildConfig.logging) Log.w(MainFragment.class.getSimpleName(), "onPause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (BuildConfig.logging) Log.e("MainFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_movies);
        mViewStub = (ViewStub) view.findViewById(R.id.viewstub_empty);

        MovieData.initialize();
        setAdapter(mRecyclerView, (ArrayList<Movie>) MovieData.getMoviesByCategory(0));

        return view;
    }

    private void setAdapter(RecyclerView movieRV, final ArrayList<Movie> movieList) {
        mMovieAdapter = new MovieAdapter(movieList, getContext());
        movieRV.setAdapter(mMovieAdapter);
        movieRV.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        if (BuildConfig.logging) Log.e("MainFragment", "onCreateLoader");
        return new SQLiteMovieLoader(this.getActivity(), mMovieManager, null, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if (BuildConfig.logging) Log.w("MaiFragment", "onLoadFinished");
        MovieData.setFavoriteData(data);
        updateView(MovieData.getFavoriteData());
        mDatabase.close();
        getLoaderManager().destroyLoader(1);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }

    public void updateData() {
        if (BuildConfig.logging) Log.w(TAG, "updateData. Favorites: " + MainActivity.isFavorites());
        if (MainActivity.isFavorites()) {
            updateFavoritesData();
        } else {
            updateCategoryData();
        }
    }

    private void updateFavoritesData() {
        if (BuildConfig.logging) Log.w("MaiFragment", "updateFavoritesData");
        mDbHelper = new MovieDbHelper(getActivity());
        mDatabase = mDbHelper.getWritableDatabase();
        mMovieManager = new MovieManager(mDatabase);
        getLoaderManager().restartLoader(1, null, this);
    }

    private void updateCategoryData() {
        List<Movie> movieList = MovieData.getMoviesByCategory(MainActivity.getSelectedCategory());
        if (movieList.isEmpty()) {
            downloadData();
        } else {
            updateView(movieList);
        }
    }

    private void downloadData() {
        Intent intent = new Intent(getActivity(), DownloadService.class);
        getActivity().startService(intent);
    }

    private void updateView(List<Movie> movieList) {
        updateView(movieList, true);
    }
    private void updateView(List<Movie> movieList, boolean successful) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMovieAdapter.setmMovieList(movieList);
            }
        });

        if (movieList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mViewStub.setVisibility(View.VISIBLE);
            TextView noDataTv = (TextView) getView().findViewById(R.id.list_empty_text);
            if (successful) {
                noDataTv.setText(getString(R.string.no_data));
            } else {
                noDataTv.setText(getString(R.string.no_connection));
            }
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mViewStub.setVisibility(View.GONE);
            mRecyclerView.smoothScrollToPosition(MainActivity.getPosition());
        }
    }

    public interface OnMovieSelectListener {
        void onMovieSelect(Movie movie, int position);
    }

    private class MovieDownloadBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BuildConfig.logging) Log.e(TAG, intent.getAction());
            String status = intent.getStringExtra(STATUS);
            List<Movie> movieList = new ArrayList<>();
            boolean successful = false;
            if (status.equals(OK)) {
                movieList.addAll(getFilteredMovies((List<MovieDTO>) intent.getSerializableExtra(UPCOMING)));
                successful = true;
            }
            MovieData.setMoviesByCategory(MainActivity.getSelectedCategory(), movieList);
            updateView(movieList, successful);
        }

        private List<Movie> getFilteredMovies(List<MovieDTO> movieList) {
            List<Movie> movies = new ArrayList<>();
            for (MovieDTO m : movieList) {
                Movie movie = new Movie(m.getIdAsLong(), m.getReleaseDateAsLong(), m.getCoverPath(), m.getTitle(), m.getBackdrop(), m.getPopularityAsFloat(), m.getDescription());
                if (movie.getBackdrop() != null && movie.getCoverPath() != null) {
                    movies.add(movie);
                }
            }
            return movies;
        }
    }
}
