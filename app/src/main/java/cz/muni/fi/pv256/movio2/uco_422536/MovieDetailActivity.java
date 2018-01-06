package cz.muni.fi.pv256.movio2.uco_422536;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Richard on 13.12.2017.
 */

public class MovieDetailActivity extends AppCompatActivity {
    public static final String DETAILED_MOVIE = "detailed_movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState == null){
            Movie movie = getIntent().getParcelableExtra(DETAILED_MOVIE);
            FragmentManager fm = getSupportFragmentManager();
            DetailFragment fragment = (DetailFragment) fm.findFragmentById(R.id.movie_detail_container);

            if (fragment == null) {
                fragment = DetailFragment.newInstance(movie);
                fm.beginTransaction()
                        .add(R.id.movie_detail_container, fragment)
                        .commit();
            }
        }
    }
}
