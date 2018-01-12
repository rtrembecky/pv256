package cz.muni.fi.pv256.movio2.uco_422536;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Richard on 11.01.2018.
 */

public class UpdaterAuthenticatorService extends Service {
    private UpdaterAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new UpdaterAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
