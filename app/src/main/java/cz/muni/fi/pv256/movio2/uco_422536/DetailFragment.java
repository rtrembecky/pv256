package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Richard on 14.12.2017.
 */

public class DetailFragment extends Fragment {
    public static final String TAG = DetailFragment.class.getSimpleName();
    private static final String ARGS_MOVIE = "args_movie";

    private Context mContext;
    private Movie mMovie;
    private SQLiteDatabase mDatabase;
    private MovieManager mMovieManager;
    private FloatingActionButton mFab;

    public static DetailFragment newInstance(Movie movie) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGS_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.logging) Log.e(TAG, "onCreate");
        mContext = getActivity();
        Bundle args = getArguments();
        if (args != null) {
            mMovie = args.getParcelable(ARGS_MOVIE);
        }
        MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
        mDatabase = dbHelper.getWritableDatabase();
        mMovieManager = new MovieManager(mDatabase);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        if (BuildConfig.logging) Log.e(TAG, "onCreateView");
        TextView titleTv = (TextView) view.findViewById(R.id.detail_title);
        TextView dateTv = (TextView) view.findViewById(R.id.detail_date);
        TextView descriptionTv = (TextView) view.findViewById(R.id.detail_desc);
        ImageView coverIv = (ImageView) view.findViewById(R.id.detail_cover);
        ImageView backdropIv = (ImageView) view.findViewById(R.id.detail_backdrop);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);

        if (mMovie != null) {
            view.setVisibility(View.VISIBLE);
            titleTv.setText(mMovie.getTitle());
            dateTv.setText(new SimpleDateFormat("dd. MM. yyyy").format(new Date(mMovie.getReleaseDate())));
            descriptionTv.setText(mMovie.getDescription());
            Picasso.get().load("https://image.tmdb.org/t/p/w300/" + mMovie.getCoverPath()).into(coverIv);
            Picasso.get().load("https://image.tmdb.org/t/p/w500/" + mMovie.getBackdrop()).into(backdropIv);
            setFabListener();
        }
        else {
            view.setVisibility(View.GONE);
        }
        return view;
    }

    private void setFabListener() {
        if (mMovieManager.containsId(mMovie.getId())) {
            mFab.setImageResource(R.drawable.ic_clear);
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMovieManager.containsId(mMovie.getId())) {
                    mMovieManager.deleteMovie(mMovie);
                    Toast.makeText(mContext, mMovie.getTitle() + " " + getResources().getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                    mFab.setImageResource(R.drawable.ic_add);
                }
                else {
                    mMovieManager.createMovie(mMovie);
                    Toast.makeText(mContext, mMovie.getTitle() + " " + getResources().getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                    mFab.setImageResource(R.drawable.ic_clear);
                }

                Fragment mainFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                if (mainFragment != null) {
                    ((MainFragment)mainFragment).updateData();
                }
            }
        });

        // workaround for bug in support library < 26.0.0:
        // button is sometimes not anchored correctly
        mFab.post(new Runnable() {
            @Override
            public void run() {
                mFab.requestLayout();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.logging) Log.e(TAG, "onDestroy");
        mDatabase.close();
        super.onDestroy();
    }
}
