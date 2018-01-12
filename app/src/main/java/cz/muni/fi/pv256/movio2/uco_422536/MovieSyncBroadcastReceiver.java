package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Richard on 11.01.2018.
 */

public class MovieSyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UpdaterSyncAdapter.getSyncAccount(context);
    }
}

