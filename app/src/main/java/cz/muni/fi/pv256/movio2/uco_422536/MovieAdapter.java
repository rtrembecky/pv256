package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Richard on 15.12.2017.
 */

public class MovieAdapter extends BaseAdapter {

    private Context mAppContext;
    private ArrayList<Movie> mMovieList;

    public MovieAdapter(ArrayList<Movie> movieList, Context context) {
        mMovieList = movieList;
        mAppContext = context.getApplicationContext();
    }

    @Override
    public int getCount() {
        return mMovieList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMovieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mAppContext).inflate(R.layout.list_item_movie, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.bindView(mAppContext, mMovieList.get(position));

        return convertView;
    }

    private static class ViewHolder {
        private ImageView coverIv;
        private TextView dateTv;
        private TextView textTv;

        public ViewHolder(View view) {
            coverIv = (ImageView) view.findViewById(R.id.list_item_icon);
            dateTv = (TextView) view.findViewById(R.id.list_item_date);
            textTv = (TextView) view.findViewById(R.id.list_item_text);
        }

        public void bindView(Context context, Movie movie) {
            if (movie == null) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                coverIv.setImageDrawable(context.getDrawable(movie.getCoverId()));
            } else {
                coverIv.setImageDrawable(context.getResources().getDrawable(movie.getCoverId()));
            }
            coverIv.setScaleType(ImageView.ScaleType.FIT_XY);

            dateTv.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date(movie.getReleaseDate())));
            textTv.setText(movie.getTitle());
        }
    }
}
