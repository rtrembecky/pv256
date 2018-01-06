package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
    private OnMovieSelectListener mListener;
    private Context mContext;
    private ListView mListView;

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
        movieList.add(new Movie(getCurrentTime().getTime(), "Cover path 1", "Shrek", "Backdrop 1", 0.7f, R.drawable.shrek));
        movieList.add(new Movie(getCurrentTime().getTime(), "Cover path 2", "Čiara", "Backdrop 2", 0.6f, R.drawable.ciara));
        movieList.add(new Movie(getCurrentTime().getTime(), "Cover path 3", "Špina", "Backdrop 3", 0.5f, R.drawable.spina));
        movieList.add(new Movie(getCurrentTime().getTime(), "Cover path 4", "Once upon a time in Venice", "Backdrop 4", 0.3f, R.drawable.once_upon_a_time_in_venice));
        movieList.add(new Movie(getCurrentTime().getTime(), "Cover path 5", "Spiderman", "Backdrop 5", 0.9f, R.drawable.spiderman));
        movieList.add(new Movie(getCurrentTime().getTime(), "Cover path 6", "24 hours to live", "Backdrop 6", 0.8f, R.drawable.twentytwo_hours_to_live));

        mListView = (ListView) view.findViewById(R.id.listview_movies);

        if (movieList != null && !movieList.isEmpty()) {
            setAdapter(mListView, movieList);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

            if (mPosition != ListView.INVALID_POSITION) {
                mListView.smoothScrollToPosition(mPosition);
            }
        }

        return view;
    }

    private Date getCurrentTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        return cal.getTime();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private void setAdapter(ListView movieLV, final ArrayList<Movie> movieList) {
        MovieAdapter adapter = new MovieAdapter(movieList, mContext);
        movieLV.setAdapter(adapter);

        // set on click listener
        movieLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                mListener.onMovieSelect(movieList.get(position));
            }
        });

        // set on long click listener
        movieLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, movieList.get(position).getTitle(), Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
        });
    }

    public interface OnMovieSelectListener {
        void onMovieSelect(Movie movie);
    }
}
