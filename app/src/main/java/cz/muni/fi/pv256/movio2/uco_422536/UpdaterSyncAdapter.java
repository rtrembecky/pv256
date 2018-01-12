package cz.muni.fi.pv256.movio2.uco_422536;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Richard on 11.01.2018.
 */

public class UpdaterSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int SYNC_INTERVAL = 60 * 60 * 24; //day
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    public static final String SYNC_FINISHED = "sync_finished";

    private static final String TAG = UpdaterSyncAdapter.class.getSimpleName();

    public UpdaterSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(Bundle.EMPTY)
                    .build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        if (BuildConfig.logging) Log.d(TAG, "Sync immediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        UpdaterSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if (BuildConfig.logging)
            Log.d(TAG, "Starting synchronization...");
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MovieApi service = retrofit.create(MovieApi.class);

            MovieDbHelper dbHelper = new MovieDbHelper(getContext());
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            MovieManager movieManager = new MovieManager(database);

            List<Movie> favouriteMovies = movieManager.getFavorites();
            for (int i = 0; i < favouriteMovies.size(); i++) {
                Movie movie = favouriteMovies.get(i);
                Call<MovieDTO> request = service.getMovieById(movie.getId());
                MovieDTO m = request.execute().body();
                Movie updatedMovie = new Movie(Long.parseLong(m.getId(), 10), m.getReleaseDateAsLong(), m.getCoverPath(), m.getTitle(), m.getBackdrop(), m.getPopularityAsFloat(), m.getDescription());

                if (!compareMovies(movie, updatedMovie)) {
                    if (BuildConfig.logging)
                        Log.d(TAG, "Movie " + updatedMovie.getTitle() + " updated");
                    movieManager.deleteMovie(movie);
                    movieManager.createMovie(updatedMovie);
                    Notification.Builder n = new Notification.Builder(getContext());
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    n.setContentTitle(getContext().getApplicationInfo().nonLocalizedLabel).setAutoCancel(true)
                            .setContentText("Movie " + updatedMovie.getTitle() + " updated")
                            .setContentIntent(pIntent)
                            .setSmallIcon(R.drawable.ic_stat_done);

                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, n.build());
                }
            }
            if (BuildConfig.logging)
                Log.d(TAG, "Synchronization finished.");
            Intent i = new Intent(SYNC_FINISHED);
            getContext().sendBroadcast(i);
            database.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            if (BuildConfig.logging) Log.d(TAG, "Synchronization exception!");

        }
    }

    private boolean compareMovies(Movie oldMovie, Movie newMovie){
        return  (Math.abs(oldMovie.getReleaseDate() - newMovie.getReleaseDate()) <= 0.000001 &&
                oldMovie.getCoverPath().equals(newMovie.getCoverPath()) &&
                oldMovie.getTitle().equals(newMovie.getTitle()) &&
                oldMovie.getBackdrop().equals(newMovie.getBackdrop()) &&
                Math.abs(oldMovie.getPopularity() - newMovie.getPopularity()) <= 0.000001 &&
                oldMovie.getDescription().equals(newMovie.getDescription())
        );
    }
}

