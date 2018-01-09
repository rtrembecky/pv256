package cz.muni.fi.pv256.movio2.uco_422536;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 07.01.2018.
 */

public class MovieData {
    private static List<List<Movie>> movieCategoriesData = new ArrayList<>(3);

    public static List<List<Movie>> getMovieCategoriesData() {
        return movieCategoriesData;
    }

    public static void setMovieCategoriesData(List<List<Movie>> movieCategoriesData) {
        MovieData.movieCategoriesData = movieCategoriesData;
    }

    public static void initialize() {
        movieCategoriesData.add(new ArrayList<>());
        movieCategoriesData.add(new ArrayList<>());
        movieCategoriesData.add(new ArrayList<>());
    }

    public static List<Movie> getMoviesByCategory(int category) {
        return movieCategoriesData.get(category);
    }

    public static void setMoviesByCategory(int category, List<Movie> data) {
        try {
            movieCategoriesData.remove(category);
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        movieCategoriesData.add(category, data);
    }
}
