package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Richard on 14.12.2017.
 */

public class MainFragment extends Fragment {

    public static final String TAG = MainFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";

    private int mPosition = ListView.INVALID_POSITION;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private ViewStub mViewStub;
    private MovieAdapter mMovieAdapter;
    private OnMovieSelectListener mListener;
    private AsyncDownloadTask mDownloader;

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
        MovieData.initialize();
        setAdapter(mRecyclerView, (ArrayList<Movie>) MovieData.getMoviesByCategory(0));

        if (isOffline()) {
            view = inflater.inflate(R.layout.list_empty, container, false);
        }
        else {
            downloadData();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            if (mPosition != ListView.INVALID_POSITION) {
                mRecyclerView.smoothScrollToPosition(mPosition);
            }
        }

        return view;
    }

    public void downloadData() {
        mDownloader = new AsyncDownloadTask();
        mDownloader.execute();
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
        mMovieAdapter = new MovieAdapter(movieList, getContext());
        movieRV.setAdapter(mMovieAdapter);
        movieRV.setLayoutManager(new LinearLayoutManager(mContext));
    }

    public interface OnMovieSelectListener {
        void onMovieSelect(Movie movie);
    }

    private class AsyncDownloadTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground - thread: " + Thread.currentThread().getName());
            try {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String now = formatter.format(cal.getTime());
                cal.add(Calendar.DAY_OF_YEAR, 7);
                String weekLater = formatter.format(cal.getTime());
                String filter = "&primary_release_date.gte=" + now + "&primary_release_date.lte=" + weekLater + "&sort_by=primary_release_date.asc";
                int category = ((MainActivity) mContext).getMenuSelectedCategory();
                switch (category) {
                    case 0:
                        filter += "";
                        break;
                    case 1:
                        filter += "&with_genres=28";
                        break;
                    case 2:
                        filter += "&with_genres=12";
                        break;
                }

                String data = requestData("http://api.themoviedb.org/3/discover/movie?api_key=" + ApiKey.KEY + filter);

                List<Movie> movieList = parseData(data);

                MovieData.setMoviesByCategory(category, movieList);
                MainFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMovieAdapter.setmMovieList(movieList);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                Toast.makeText(getActivity().getApplicationContext(), "Data sucesfully downloaded", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity().getApplicationContext(), "Data not recieved", Toast.LENGTH_SHORT).show();

            int category = ((MainActivity) mContext).getMenuSelectedCategory();
            if (MovieData.getMoviesByCategory(category).isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mViewStub.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mViewStub.setVisibility(View.GONE);
            }
            mDownloader = null;
        }

        String requestData(String url) throws IOException {
            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();
        }

        List<Movie> parseData(String data) {
            List<Movie> movieList = new ArrayList<>();
            try {
                JSONObject json = new JSONObject(data);
                Gson gson = new Gson();
                ArrayList<MovieDTO> movies = gson.fromJson(json.getJSONArray("results").toString(), new TypeToken<List<MovieDTO>>(){ }.getType());

                for (MovieDTO m : movies) {
                    Movie movie = new Movie(m.getReleaseDateAsLong(), m.getCoverPath(), m.getTitle(), m.getBackdrop(), m.getPopularityAsFloat());
                    movieList.add(movie);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movieList;
        }
    }
}
