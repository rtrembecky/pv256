package cz.muni.fi.pv256.movio2.uco_422536;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Richard on 07.01.2018.
 */

public class MovieDTO {
    @SerializedName("id")
    private String mId;
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
    @SerializedName("overview")
    private String mDescription;

    public MovieDTO(String id, String releaseDate, String coverPath, String title, String backdrop, String popularity, String description) {
        mId = id;
        mReleaseDate = releaseDate;
        mCoverPath = coverPath;
        mTitle = title;
        mBackdrop = backdrop;
        mPopularity = popularity;
        mDescription = description;
    }

    public String getId() {
        return mId;
    }

    public Long getIdAsLong() {
        return Long.parseLong(mId);
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

    public String getDescription() {
        return mDescription;
    }
}