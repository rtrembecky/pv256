package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Richard on 14.12.2017.
 */

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";

    private int mPosition = ListView.INVALID_POSITION;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private OnMovieSelectListener mListener;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            mListener = (OnMovieSelectListener) activity;
        } catch (ClassCastException e) {
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

        mContext = getActivity().getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<Movie> movieList = new ArrayList<>();
        movieList.add(new Movie(getCurrentTime().getTime(), "olaf_cover", "Olaf's Frozen Adventure", "olaf", 5.9f));
        movieList.add(new Movie(getCurrentTime().getTime(), "last_jedi_cover", "Star Wars: The Last Jedi", "last_jedi", 7.3f));
        movieList.add(new Movie(getCurrentTime().getTime(), "coco_cover", "Coco", "coco", 7.5f));
        movieList.add(new Movie(getCurrentTime().getTime(), "dunkirk_cover", "Dunkirk", "dunkirk", 7.4f));
        movieList.add(new Movie(getCurrentTime().getTime(), "jumanji_cover", "Jumanji", "jumanji", 6.3f));

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_movies);

        if (movieList != null && !movieList.isEmpty()) {
            setAdapter(mRecyclerView, movieList);
        }
        else {
            view = inflater.inflate(R.layout.list_empty, container, false);
            if (isOffline())
                ((TextView) view.findViewById(R.id.list_empty_text)).setText("Not connected");
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

            if (mPosition != ListView.INVALID_POSITION) {
                mRecyclerView.smoothScrollToPosition(mPosition);
            }
        }

        return view;
    }

    private Date getCurrentTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        return cal.getTime();
    }

    public boolean isOffline()
    {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return (network == null || !network.isConnected());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private void setAdapter(RecyclerView movieRV, final ArrayList<Movie> movieList) {
        MovieAdapter adapter = new MovieAdapter(movieList, getContext());
        movieRV.setAdapter(adapter);
        movieRV.setLayoutManager(new LinearLayoutManager(mContext));
    }

    public interface OnMovieSelectListener {
        void onMovieSelect(Movie movie);
    }
}
