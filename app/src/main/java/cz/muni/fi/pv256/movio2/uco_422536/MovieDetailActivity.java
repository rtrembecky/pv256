package cz.muni.fi.pv256.movio2.uco_422536;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import static cz.muni.fi.pv256.movio2.uco_422536.MainActivity.CATEGORY;
import static cz.muni.fi.pv256.movio2.uco_422536.MainActivity.MOVIE;
import static cz.muni.fi.pv256.movio2.uco_422536.MainActivity.POSITION;

/**
 * Created by Richard on 13.12.2017.
 */

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Movie movie = getIntent().getParcelableExtra(MOVIE);
        if (findViewById(R.id.two_pane) != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(MOVIE, movie);
            intent.putExtra(CATEGORY, getIntent().getIntExtra(CATEGORY, 0));
            intent.putExtra(POSITION, getIntent().getIntExtra(POSITION, 0));
            startActivity(intent);
            return;
        }

        if(savedInstanceState == null){
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
