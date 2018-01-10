package cz.muni.fi.pv256.movio2.uco_422536;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Richard on 14.12.2017.
 */

public class Movie implements Parcelable {
    private Long mId;
    private long mReleaseDate;
    private String mCoverPath;
    private String mTitle;
    private String mBackdrop;
    private float mPopularity;
    private String mDescription;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public long getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getCoverPath() {
        return mCoverPath;
    }

    public void setCoverPath(String coverPath) {
        mCoverPath = coverPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getBackdrop() {
        return mBackdrop;
    }

    public void setBackdrop(String backdrop) {
        mBackdrop = backdrop;
    }

    public float getPopularity() {
        return mPopularity;
    }

    public void setPopularity(float popularity) {
        mPopularity = popularity;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Movie(long id, long releaseDate, String coverPath, String title, String backdrop, float popularity, String description) {
        mReleaseDate = releaseDate;
        mCoverPath = coverPath;
        mTitle = title;
        mBackdrop = backdrop;
        mPopularity = popularity;
        mDescription = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mReleaseDate);
        dest.writeString(mCoverPath);
        dest.writeString(mTitle);
        dest.writeString(mBackdrop);
        dest.writeFloat(mPopularity);
        dest.writeString(mDescription);
    }

    public Movie(Parcel in) {
        mReleaseDate = in.readLong();
        mCoverPath = in.readString();
        mTitle = in.readString();
        mBackdrop = in.readString();
        mPopularity = in.readFloat();
        mDescription = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
