package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.muni.fi.pv256.movio2.uco_422536.MovieContract.MovieEntry;

/**
 * Created by Richard on 10.01.2018.
 */

public class MovieManager {
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_RELEASE_DATE = 1;
    public static final int COL_MOVIE_COVER_PATH = 2;
    public static final int COL_MOVIE_TITLE = 3;
    public static final int COL_MOVIE_BACKDROP_PATH = 4;
    public static final int COL_MOVIE_POPULARITY = 5;
    public static final int COL_MOVIE_DESCRIPTION = 6;

    SQLiteDatabase mDatabase;

    private static final String[] MOVIE_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_COVER_PATH,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_BACKDROP_PATH,
            MovieEntry.COLUMN_POPULARITY,
            MovieEntry.COLUMN_DESCRIPTION,
    };

    public MovieManager(SQLiteDatabase database) {
        mDatabase = database;
    }

    public boolean createMovie(Movie movie) {
        if (movie == null) {
            throw new NullPointerException("Cannot create movie: movie is null");
        }
        if (movie.getCoverPath() == null) {
            throw new IllegalStateException("Cannot create movie: cover path is null");
        }
        if (movie.getTitle() == null) {
            throw new IllegalStateException("Cannot create movie: title is null");
        }
        if (movie.getBackdrop() == null) {
            throw new IllegalStateException("Cannot create movie: backdrop path is null");
        }
        if (movie.getDescription() == null) {
            throw new IllegalStateException("Cannot create movie: description is null");
        }
        long result = mDatabase.insert(MovieEntry.TABLE_NAME, null, prepareMovieValues(movie));
        return result != -1;
    }

    public List<Movie> getFavorites() {
        if (BuildConfig.logging) Log.e("MovieManager", "getFavorites");
        Cursor cursor = mDatabase.query(MovieEntry.TABLE_NAME, MOVIE_COLUMNS, null,
                null, null, null, null);
        List<Movie> movieList = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    movieList.add(getMovie(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return movieList;
    }

    private Movie getMovie(Cursor cursor) {
        return new Movie(
                cursor.getLong(COL_MOVIE_ID),
                MovieContract.getDateFromDb(cursor.getString(COL_MOVIE_RELEASE_DATE)),
                cursor.getString(COL_MOVIE_COVER_PATH),
                cursor.getString(COL_MOVIE_TITLE),
                cursor.getString(COL_MOVIE_BACKDROP_PATH),
                cursor.getFloat(COL_MOVIE_POPULARITY),
                cursor.getString(COL_MOVIE_DESCRIPTION)
        );
    }

    public boolean deleteMovie(Movie movie) {
        if (movie == null) {
            return false;
        }
        if (movie.getId() == null) {
            throw new IllegalStateException("Cannot delete movie: ID is null");
        }

        int result = mDatabase.delete(MovieEntry.TABLE_NAME, MovieEntry._ID + " = " + movie.getId(), null);
        return result != 0;
    }

    public boolean containsId(Long id) {
        String Query = "Select * from " + MovieEntry.TABLE_NAME + " where " + MovieEntry._ID + " = " + id;
        Cursor cursor = mDatabase.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private ContentValues prepareMovieValues(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, movie.getId());
        values.put(MovieEntry.COLUMN_RELEASE_DATE, MovieContract.insertDateToDb(movie.getReleaseDate()));
        values.put(MovieEntry.COLUMN_COVER_PATH, movie.getCoverPath());
        values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdrop());
        values.put(MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
        values.put(MovieEntry.COLUMN_DESCRIPTION, movie.getDescription());
        return values;
    }
}
