package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements MainFragment.OnMovieSelectListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String MOVIE = "movie";
    public static final String CATEGORY = "category";
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
    private static boolean mFavorites = false;

    public static int getPosition() {
        return mPosition;
    }

    public static int getSelectedCategory() {
        return mSelectedCategory;
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

        MenuItem menuItem = mNavigationView.getMenu().getItem(mSelectedCategory);
        onMenuItemSelected(menuItem);
    }

    private void onMenuItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_all:
                if(!menuItem.isChecked()) {
                    getSupportActionBar().setTitle(menuItem.getTitle());
                    mNavigationView.getMenu().getItem(1).setChecked(false);
                    mNavigationView.getMenu().getItem(2).setChecked(false);
                    mSelectedCategory = 0;
                    if (BuildConfig.logging) Log.e(TAG, "calling updateData from onMenuSelected (nav_all)");
                    mMainFragment.updateData();
                }
                break;
            case R.id.nav_action:
                if(!menuItem.isChecked()) {
                    getSupportActionBar().setTitle(menuItem.getTitle());
                    mNavigationView.getMenu().getItem(0).setChecked(false);
                    mNavigationView.getMenu().getItem(2).setChecked(false);
                    mSelectedCategory = 1;
                    if (BuildConfig.logging) Log.e(TAG, "calling updateData from onMenuSelected (nav_action)");
                    mMainFragment.updateData();
                }
                break;
            case R.id.nav_adventure:
                if(!menuItem.isChecked()) {
                    getSupportActionBar().setTitle(menuItem.getTitle());
                    mNavigationView.getMenu().getItem(0).setChecked(false);
                    mNavigationView.getMenu().getItem(1).setChecked(false);
                    mSelectedCategory = 2;
                    if (BuildConfig.logging) Log.e(TAG, "calling updateData from onMenuSelected (nav_adventure)");
                    mMainFragment.updateData();
                }
                break;
        }
        mDrawerLayout.closeDrawers();
        menuItem.setChecked(true);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (BuildConfig.logging) Log.w(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.menu);
        item.setActionView(R.layout.action_bar);
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
            mFavorites = getIntent().getBooleanExtra(FAVORITES, false);
        }

        MenuItem menuItem = mNavigationView.getMenu().getItem(mSelectedCategory);
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
