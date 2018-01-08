package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        mContext = getActivity();
        Bundle args = getArguments();
        if (args != null) {
            mMovie = args.getParcelable(ARGS_MOVIE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView titleTv = (TextView) view.findViewById(R.id.detail_movie);
        TextView titleLowTv = (TextView) view.findViewById(R.id.detail_movie_low);
        ImageView coverIv = (ImageView) view.findViewById(R.id.detail_cover);
        ImageView backdropIv = (ImageView) view.findViewById(R.id.detail_backdrop);

        if (mMovie != null) {
            titleTv.setText(mMovie.getTitle());
            titleLowTv.setText(new SimpleDateFormat("dd. MM. yyyy").format(new Date(mMovie.getReleaseDate())));
            Picasso picasso = new Picasso.Builder(mContext).build();
            picasso.load("https://image.tmdb.org/t/p/w300/" + mMovie.getCoverPath()).into(coverIv);
            picasso.load("https://image.tmdb.org/t/p/w500/" + mMovie.getBackdrop()).into(backdropIv);
        }
        return view;
    }
}
