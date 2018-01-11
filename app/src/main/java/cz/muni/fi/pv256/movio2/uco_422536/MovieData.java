package cz.muni.fi.pv256.movio2.uco_422536;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 07.01.2018.
 */

public class MovieData {
    private static List<List<Movie>> movieCategoriesData = new ArrayList<>(6);
    private static List<Movie> favoriteData = new ArrayList<>();

    public static void initialize() {
        movieCategoriesData.clear();
        for (int i = 0; i < 6; i++) {
            movieCategoriesData.add(new ArrayList<>());
        }
    }

    public static List<Movie> getMoviesByCategory(int timeInterval, int category) {
        return movieCategoriesData.get(timeInterval * 3 + category);
    }

    public static void setMoviesByCategory(int timeInterval, int category, List<Movie> data) {
        try {
            movieCategoriesData.remove(timeInterval * 3 + category);
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        List<Movie> movieList = new ArrayList<>();
        movieList.addAll(data);
        movieCategoriesData.add(timeInterval * 3 + category, movieList);
    }

    public static List<Movie> getFavoriteData() {
        return favoriteData;
    }

    public static void setFavoriteData(List<Movie> favoriteData) {
        MovieData.favoriteData.clear();
        MovieData.favoriteData.addAll(favoriteData);
    }
}
