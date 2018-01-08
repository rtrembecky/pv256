package cz.muni.fi.pv256.movio2.uco_422536;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Richard on 07.01.2018.
 */

public class MovieDTO {
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("poster_path")
    private String mCoverPath;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("backdrop_path")
    private String mBackdrop;
    @SerializedName("vote_average")
    private String mPopularity;

    public MovieDTO(String releaseDate, String coverPath, String title, String backdrop, String popularity) {
        mReleaseDate = releaseDate;
        mCoverPath = coverPath;
        mTitle = title;
        mBackdrop = backdrop;
        mPopularity = popularity;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public long getReleaseDateAsLong() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public String getCoverPath() {
        return mCoverPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBackdrop() {
        return mBackdrop;
    }

    public String getPopularity() {
        return mPopularity;
    }

    public Float getPopularityAsFloat() {
        return Float.parseFloat(getPopularity());
    }
}