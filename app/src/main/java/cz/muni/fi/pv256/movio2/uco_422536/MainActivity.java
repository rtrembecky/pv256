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

    /*private SharedPreferences mShared;
    private SharedPreferences.Editor mSharedEditor;
    private static final String THEME = "primaryTheme";*/
    private boolean mTwoPane;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MainFragment mMainFragment;
    private static int mMenuSelectedCategory;

    public static int getMenuSelectedCategory() {
        return mMenuSelectedCategory;
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
                switch (menuItem.getItemId()) {
                    case R.id.nav_all:
                        if(!menuItem.isChecked()) {
                            getSupportActionBar().setTitle(menuItem.getTitle());
                            mNavigationView.getMenu().getItem(1).setChecked(false);
                            mNavigationView.getMenu().getItem(2).setChecked(false);
                            mMenuSelectedCategory = 0;
                            mMainFragment.downloadData();
                        }
                        break;
                    case R.id.nav_action:
                        if(!menuItem.isChecked()) {
                            getSupportActionBar().setTitle(menuItem.getTitle());
                            mNavigationView.getMenu().getItem(0).setChecked(false);
                            mNavigationView.getMenu().getItem(2).setChecked(false);
                            mMenuSelectedCategory = 1;
                            mMainFragment.downloadData();
                        }
                        break;
                    case R.id.nav_adventure:
                        if(!menuItem.isChecked()) {
                            getSupportActionBar().setTitle(menuItem.getTitle());
                            mNavigationView.getMenu().getItem(0).setChecked(false);
                            mNavigationView.getMenu().getItem(1).setChecked(false);
                            mMenuSelectedCategory = 2;
                            mMainFragment.downloadData();
                        }
                        break;
                }
                mDrawerLayout.closeDrawers();
                menuItem.setChecked(true);
                return true;
            }
        });

        mMenuSelectedCategory = 0;
        MenuItem menuAll = mNavigationView.getMenu().getItem(0);
        menuAll.setChecked(true);
        getSupportActionBar().setTitle(menuAll.getTitle());

        mMainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
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
    public void onMovieSelect(Movie movie) {
        if (mTwoPane) {
            FragmentManager fm = getSupportFragmentManager();

            DetailFragment fragment = DetailFragment.newInstance(movie);
            fm.beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DetailFragment.TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.DETAILED_MOVIE, movie);
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
