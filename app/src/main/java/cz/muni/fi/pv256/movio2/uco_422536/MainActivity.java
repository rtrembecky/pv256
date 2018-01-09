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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements MainFragment.OnMovieSelectListener {

    public static final String MOVIE = "movie";
    public static final String CATEGORY = "CATEGORY";
    public static final String POSITION = "position";

    /*private SharedPreferences mShared;
    private SharedPreferences.Editor mSharedEditor;
    private static final String THEME = "primaryTheme";*/
    private boolean mTwoPane;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MainFragment mMainFragment;
    private static int mSelectedCategory = 0;

    public static int getSelectedCategory() {
        return mSelectedCategory;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // theme switch
        //changeTheme(isPrimaryThemeSet());

        setContentView(R.layout.activity_main);

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
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

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
                    mMainFragment.downloadData();
                }
                break;
            case R.id.nav_action:
                if(!menuItem.isChecked()) {
                    getSupportActionBar().setTitle(menuItem.getTitle());
                    mNavigationView.getMenu().getItem(0).setChecked(false);
                    mNavigationView.getMenu().getItem(2).setChecked(false);
                    mSelectedCategory = 1;
                    mMainFragment.downloadData();
                }
                break;
            case R.id.nav_adventure:
                if(!menuItem.isChecked()) {
                    getSupportActionBar().setTitle(menuItem.getTitle());
                    mNavigationView.getMenu().getItem(0).setChecked(false);
                    mNavigationView.getMenu().getItem(1).setChecked(false);
                    mSelectedCategory = 2;
                    mMainFragment.downloadData();
                }
                break;
        }
        mDrawerLayout.closeDrawers();
        menuItem.setChecked(true);
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int position = getIntent().getIntExtra(POSITION, 0);
        Movie movie = getIntent().getParcelableExtra(MOVIE);
        mSelectedCategory = getIntent().getIntExtra(CATEGORY, 0);
        MenuItem menuItem = mNavigationView.getMenu().getItem(mSelectedCategory);
        onMenuItemSelected(menuItem);
        if (movie != null) {
            onMovieSelect(movie, position);
        }

        ((MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main)).setPosition(position);
        ((MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main)).downloadData();
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
