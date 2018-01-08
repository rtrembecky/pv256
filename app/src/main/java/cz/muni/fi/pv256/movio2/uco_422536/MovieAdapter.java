package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 15.12.2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context mAppContext;
    private static List<Movie> mMovieList;

    public MovieAdapter(ArrayList<Movie> movieList, Context context) {
        mMovieList = movieList;
        mAppContext = context;
    }

    public void setmMovieList(List<Movie> mMovieList) {
        MovieAdapter.mMovieList = mMovieList;
        notifyDataSetChanged();
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mAppContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_movie, parent, false);
        return new ViewHolder(view, mAppContext);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);
        if(BuildConfig.logging)
            Log.d("onBindViewHolder", "Binding " + movie.getTitle());
        holder.titleTv.setText(movie.getTitle());
        holder.popularityTv.setText(Float.toString(movie.getPopularity()));

        Picasso picasso = new Picasso.Builder(mAppContext).build();
        picasso.load("https://image.tmdb.org/t/p/w500/" + movie.getBackdrop()).into(holder.backdropIv, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap myBitmap = ((BitmapDrawable)holder.backdropIv.getDrawable()).getBitmap();
                if (myBitmap != null && !myBitmap.isRecycled()) {
                    Palette palette = Palette.from(myBitmap).generate();
                    int color = palette.getVibrantColor(0x000000);
                    int semiTransparentColor = Color.argb(128, Color.red(color), Color.green(color), Color.blue(color));
                    GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.TRANSPARENT, color});
                    holder.titleTv.setBackgroundColor(semiTransparentColor);
                    holder.popularityTv.setBackgroundColor(color);
                    holder.starIv.setBackground(gradient);
                    int textColor = Color.WHITE;
                    holder.titleTv.setTextColor(textColor);
                    holder.popularityTv.setTextColor(textColor);
                }
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView backdropIv;
        private TextView titleTv;
        private TextView popularityTv;
        private ImageView starIv;
        private RelativeLayout layout;

        public ViewHolder(View view, final Context context) {
            super(view);
            backdropIv = (ImageView) view.findViewById(R.id.list_item_icon);
            titleTv = (TextView) view.findViewById(R.id.list_item_title);
            popularityTv = (TextView) view.findViewById(R.id.list_item_popularity);
            starIv = (ImageView) view.findViewById(R.id.list_item_star);
            layout = (RelativeLayout) view.findViewById(R.id.relative_layout);

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(context != null) {
                        ((MainActivity) context).onMovieSelect(mMovieList.get(getAdapterPosition()));
                    }
                }
            };

            layout.setOnClickListener(clickListener);
        }
    }
}
