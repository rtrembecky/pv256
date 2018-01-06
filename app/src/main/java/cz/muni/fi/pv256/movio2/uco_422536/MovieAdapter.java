package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Richard on 15.12.2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context mAppContext;
    private static ArrayList<Movie> mMovieList;

    public MovieAdapter(ArrayList<Movie> movieList, Context context) {
        mMovieList = movieList;
        mAppContext = context;
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
        Log.d("onBindViewHolder", "Binding " + movie.getTitle());
        holder.titleTv.setText(movie.getTitle());
        holder.popularityTv.setText(Float.toString(movie.getPopularity()));

        int coverId = mAppContext.getResources().getIdentifier(movie.getBackdrop(), "drawable", mAppContext.getPackageName());
        holder.coverIv.setImageDrawable(mAppContext.getResources().getDrawable(coverId));

        Bitmap myBitmap = BitmapFactory.decodeResource(mAppContext.getResources(), coverId);
        if (myBitmap != null && !myBitmap.isRecycled()) {
            Palette palette = Palette.from(myBitmap).generate();
            int color = palette.getVibrantColor(0x000000);
            int semiTransparentColor = Color.argb(128, Color.red(color), Color.green(color), Color.blue(color));
            GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.TRANSPARENT, color});
            holder.titleTv.setBackgroundColor(semiTransparentColor);
            holder.popularityTv.setBackgroundColor(color);
            holder.starIv.setBackground(gradient);
//            Palette.Swatch swatch = palette.getVibrantSwatch();
            int textColor = Color.WHITE;
            holder.titleTv.setTextColor(textColor);
            holder.popularityTv.setTextColor(textColor);
        }
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
        private ImageView coverIv;
        private TextView titleTv;
        private TextView popularityTv;
        private ImageView starIv;
        private RelativeLayout layout;

        public ViewHolder(View view, final Context context) {
            super(view);
            coverIv = (ImageView) view.findViewById(R.id.list_item_icon);
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
