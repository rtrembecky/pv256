package cz.muni.fi.pv256.movio2.uco_422536;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Richard on 08.01.2018.
 */

public class DownloadService extends IntentService {

    public static final String TAG = DownloadService.class.getSimpleName();

    public static final String FAILED = "failed";
    public static final String STARTED = "started";
    public static final String DONE = "done";

    public static final String DOWNLOAD = "download";
    public static final String STATUS = "status";
    public static final String OK = "OK";
    public static final String ERROR = "error";
    public static final String UPCOMING = "upcoming";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadService(String name) {
        super(name);
    }

    public DownloadService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DOWNLOAD);

        if(isOffline()) {
//            Toast.makeText(this, getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show(); // TODO: sending message to a Handler on a dead thread
            broadcastIntent.putExtra(STATUS, ERROR);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            downloadNotification(FAILED);
        }
        else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieApi service = retrofit.create(MovieApi.class);

            downloadNotification(STARTED);
//            Toast.makeText(this, R.string.downloadStarted, Toast.LENGTH_SHORT).show(); //TODO: sending message to a Handler on a dead thread

            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String now = formatter.format(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 7);
            String weekLater = formatter.format(cal.getTime());

            String genres = "";
            int category = MainActivity.getSelectedCategory();
            switch (category) {
                case 0:
                    break;
                case 1:
                    genres = "28";
                    break;
                case 2:
                    genres = "12";
                    break;
            }
            Call<MovieList> request = service.getMovies(ApiKey.KEY, "primary_release_date.asc", now, weekLater, genres);
            try {
                MovieList movieList = request.execute().body();
                broadcastIntent.putExtra(UPCOMING, movieList.getResults());

                broadcastIntent.putExtra(STATUS, OK);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                downloadNotification(DONE);
            } catch (IOException e) {
                e.printStackTrace();
                broadcastIntent.putExtra(STATUS, ERROR);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                downloadNotification(FAILED);
            }
        }
    }

    private void downloadNotification(String type) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder n = new Notification.Builder(this);
        n.setContentTitle(getApplicationContext().getApplicationInfo().nonLocalizedLabel)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        switch (type) {
            case FAILED:
                n.setContentText(getResources().getString(R.string.downloadFailed)).setSmallIcon(R.drawable.ic_stat_error);
                break;
            case STARTED:
                n.setContentText(getResources().getString(R.string.downloadStarted)).setSmallIcon(R.drawable.ic_stat_file_download);
                break;
            case DONE:
                n.setContentText(getResources().getString(R.string.downloadDone)).setSmallIcon(R.drawable.ic_stat_done);
                break;
        }
        NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n.build());
    }

    public boolean isOffline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return (network == null || !network.isConnected());
    }
}
