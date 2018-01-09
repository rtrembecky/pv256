package cz.muni.fi.pv256.movio2.uco_422536;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by Richard on 08.01.2018.
 */

public interface MovieApi {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })

    @GET("discover/movie")
    Call<MovieList> getMovies(@Query("api_key") String apiKey,
                                   @Query("sort_by") String sortBy,
                                   @Query("primary_release_date.gte") String releaseDateFrom,
                                   @Query("primary_release_date.lte") String releaseDateTo,
                                   @Query("with_genres") String genres);
}
