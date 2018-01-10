package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import static cz.muni.fi.pv256.movio2.uco_422536.MainActivity.POSITION;

/**
 * Created by Richard on 14.12.2017.
 */

public class MainFragment extends Fragment {

    public static final String TAG = MainFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ViewStub mViewStub;
    private TextView mNoDataTv;

    private Context mContext;
    private MovieAdapter mMovieAdapter;
    private OnMovieSelectListener mListener;
    private MovieDownloadBroadcastReceiver mReceiver;
    private int mPosition = 0;
    private boolean mFavorites;

    public void setPosition(int position) {
        mPosition = position;
    }

    public boolean isFavorites() {
        return mFavorites;
    }

    public void setFavorites(boolean favorites) {
        mFavorites = favorites;
    }

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
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_movies);
        mViewStub = (ViewStub) view.findViewById(R.id.viewstub_empty);
        mNoDataTv = (TextView) view.findViewById(R.id.list_empty_text);

        MovieData.initialize();
        setAdapter(mRecyclerView, (ArrayList<Movie>) MovieData.getMoviesByCategory(0));

        if (isOffline()) {
            view = inflater.inflate(R.layout.list_empty, container, false);
        }
        else {
            updateData();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION)) {
            mPosition = savedInstanceState.getInt(POSITION);
            mRecyclerView.smoothScrollToPosition(mPosition);
        }

        return view;
    }

    public void updateData() {
        List<Movie> movieList = MovieData.getMoviesByCategory(MainActivity.getSelectedCategory());
        if (movieList.isEmpty()) {
            downloadData();
        } else {
            updateView(movieList, true);
        }
    }

    private void downloadData() {
        Intent intent = new Intent(getActivity(), DownloadService.class);
        getActivity().startService(intent);
        IntentFilter intentFilter = new IntentFilter(DOWNLOAD);
        mReceiver = new MovieDownloadBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
    }

    public boolean isOffline()
    {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return (network == null || !network.isConnected());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(POSITION, mPosition);
        super.onSaveInstanceState(outState);
    }

    private void setAdapter(RecyclerView movieRV, final ArrayList<Movie> movieList) {
        mMovieAdapter = new MovieAdapter(movieList, getContext());
        movieRV.setAdapter(mMovieAdapter);
        movieRV.setLayoutManager(new LinearLayoutManager(mContext));
    }

    public interface OnMovieSelectListener {
        void onMovieSelect(Movie movie, int position);
    }

    private class MovieDownloadBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
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

    public void updateView(List<Movie> movieList) {
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
            if (successful) {
                mNoDataTv.setText(getString(R.string.no_data));
            }
            else {
                mNoDataTv.setText(getString(R.string.no_connection));
            }
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mViewStub.setVisibility(View.GONE);
            mRecyclerView.smoothScrollToPosition(mPosition);
        }
    }
}
