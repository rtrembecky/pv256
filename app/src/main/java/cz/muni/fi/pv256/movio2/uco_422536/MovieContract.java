package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Richard on 10.01.2018.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "cz.muni.fi.pv256.movio2.uco_422536.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WORK_TIME = "movies";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String insertDateToDb(Long longDate){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.format(longDate);
    }

    @NonNull
    public static Long getDateFromDb(String dBdate){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date date = null;
        try {
            date = (Date)formatter.parse(dBdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORK_TIME).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WORK_TIME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WORK_TIME;

        public static final String TABLE_NAME = "Movies";

        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_COVER_PATH = "cover_path";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_DESCRIPTION = "description";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
