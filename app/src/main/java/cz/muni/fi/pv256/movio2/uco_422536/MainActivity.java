package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import static cz.muni.fi.pv256.movio2.uco_422536.UpdaterSyncAdapter.SYNC_FINISHED;

public class MainActivity extends AppCompatActivity implements MainFragment.OnMovieSelectListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String MOVIE = "movie";
    public static final String CATEGORY = "category";
    public static final String TIME_INTERVAL = "time_interval";
    public static final String POSITION = "position";
    public static final String FAVORITES = "favorites";

    /*private SharedPreferences mShared;
    private SharedPreferences.Editor mSharedEditor;
    private static final String THEME = "primaryTheme";*/
    private boolean mTwoPane;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MainFragment mMainFragment;
    private SwitchCompat mSwitch;
    private static int mPosition = 0;
    private static int mSelectedCategory = 0;
    private static int mSelectedTimeInterval = 0;
    private static boolean mFavorites = false;

    private static IntentFilter syncIntentFilter = new IntentFilter(SYNC_FINISHED);

    public static int getPosition() {
        return mPosition;
    }

    public static int getSelectedCategory() {
        return mSelectedCategory;
    }

    public static int getmSelectedTimeInterval() {
        return mSelectedTimeInterval;
    }

    public static boolean isFavorites() {
        return mFavorites;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // theme switch
        //changeTheme(isPrimaryThemeSet());
        if (BuildConfig.logging) Log.w(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION)) {
            mPosition = savedInstanceState.getInt(POSITION);
        }

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DetailFragment.TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mMainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                onMenuItemSelected(menuItem);
                return true;
            }
        });

        MenuItem menuItem = mNavigationView.getMenu().getItem(mSelectedTimeInterval).getSubMenu().getItem(mSelectedCategory);
        onMenuItemSelected(menuItem);

        UpdaterSyncAdapter.initializeSyncAdapter(this);
    }

    private void onMenuItemSelected(MenuItem menuItem) {
        unCheckAllMenuItems(mNavigationView.getMenu());
        getSupportActionBar().setTitle(menuItem.getTitle());
        switch (menuItem.getItemId()) {
            case R.id.upcoming_all:
                mSelectedCategory = 0;
                mSelectedTimeInterval = 0;
                if (BuildConfig.logging) Log.e(TAG, "calling updateData from onMenuSelected (nav_all)");
                break;
            case R.id.upcoming_action:
                mSelectedCategory = 1;
                mSelectedTimeInterval = 0;
                if (BuildConfig.logging) Log.e(TAG, "calling updateData from onMenuSelected (nav_action)");
                break;
            case R.id.upcoming_adventure:
                mSelectedCategory = 2;
                mSelectedTimeInterval = 0;
                if (BuildConfig.logging) Log.e(TAG, "calling updateData from onMenuSelected (nav_adventure)");
                break;
            case R.id.last_all:
                mSelectedCategory = 0;
                mSelectedTimeInterval = 1;
                if (BuildConfig.logging) Log.e(TAG, "calling updateData from onMenuSelected (nav_all)");
                break;
            case R.id.last_action:
                mSelectedCategory = 1;
                mSelectedTimeInterval = 1;
                if (BuildConfig.logging) Log.e(TAG, "calling updateData from onMenuSelected (nav_action)");
                break;
            case R.id.last_adventure:
                mSelectedCategory = 2;
                mSelectedTimeInterval = 1;
                if (BuildConfig.logging) Log.e(TAG, "calling updateData from onMenuSelected (nav_adventure)");
                break;
        }
        mMainFragment.updateData();
        mDrawerLayout.closeDrawers();
        menuItem.setChecked(true);
    }

    private void unCheckAllMenuItems(@NonNull final Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if(item.hasSubMenu()) {
                unCheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(syncFinishedReceiver, syncIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(syncFinishedReceiver);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(POSITION, mPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mNavigationView))
                    mDrawerLayout.closeDrawers();
                else
                    mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_item_sync:
                UpdaterSyncAdapter.syncImmediately(getApplicationContext());
                Toast.makeText(getApplicationContext(), R.string.downloadStarted, Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (BuildConfig.logging) Log.w(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_switch);
        item.setActionView(R.layout.action_bar_favorites);
        mSwitch = (SwitchCompat) item.getActionView().findViewById(R.id.favorites_switch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mFavorites = b;
                mPosition = 0;
                mDrawerLayout.closeDrawers();
                if (BuildConfig.logging) Log.e(TAG, "calling updateData from onCheckedChanged");
                mMainFragment.updateData();
            }
        });
        mSwitch.setChecked(mFavorites);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (BuildConfig.logging) Log.w(TAG, "onNewIntent");
        Movie movie = null;
        if (mTwoPane) {
            mPosition = getIntent().getIntExtra(POSITION, 0);
            movie = getIntent().getParcelableExtra(MOVIE);
            mSelectedCategory = getIntent().getIntExtra(CATEGORY, 0);
            mSelectedTimeInterval = getIntent().getIntExtra(TIME_INTERVAL, 0);
            mFavorites = getIntent().getBooleanExtra(FAVORITES, false);
        }

        MenuItem menuItem = mNavigationView.getMenu().getItem(mSelectedTimeInterval).getSubMenu().getItem(mSelectedCategory);
        if (BuildConfig.logging) Log.e(TAG, "calling onMenuSelected AND updateData from onNewIntent");
        onMenuItemSelected(menuItem);
        mMainFragment.updateData();

        if (movie != null) {
            onMovieSelect(movie, mPosition);
        }
    }

    @Override
    public void onMovieSelect(Movie movie, int position) {
        if (mTwoPane) {
            FragmentManager fm = getSupportFragmentManager();

            DetailFragment fragment = DetailFragment.newInstance(movie);
            fm.beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DetailFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MOVIE, movie);
            intent.putExtra(CATEGORY, mSelectedCategory);
            intent.putExtra(POSITION, position);
            intent.putExtra(FAVORITES, mFavorites);
            startActivity(intent);
        }
    }

    private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mFavorites) {
                mMainFragment.updateData();
            }
            Toast.makeText(context, getResources().getString(R.string.downloadDone), Toast.LENGTH_SHORT).show();
        }
    };
// theme switch
/*    public void buttonClick(View v) {
        mSharedEditor = mShared.edit();
        boolean primaryTheme = isPrimaryThemeSet();
        mSharedEditor.putBoolean(THEME, !primaryTheme);
        mSharedEditor.commit();
        changeTheme(!primaryTheme);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void changeTheme(boolean primaryTheme) {
        if(primaryTheme)
            setTheme(R.style.AppTheme);
        else
            setTheme(R.style.AppTheme2);
    }

    public boolean isPrimaryThemeSet() {
        mShared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return mShared.getBoolean(THEME, true);
    }*/
}
