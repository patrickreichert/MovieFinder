package com.example.moviefinder.model.movie;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Movie class
 */
@Entity(tableName = "movie")
public class Movie implements Parcelable
{
    @PrimaryKey(autoGenerate = true)
    private int roomId;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("poster_path")
    private String movieImagePath;
    @Expose
    private String overview;
    @SerializedName("vote_average")
    private Double voteAverage;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("id")
    private Integer id;

    @Ignore
    public Movie()
    {

    }

    @Ignore
    public Movie(String originalTitle, String movieImagePath, String overview, Double voteAverage, String releaseDate, String backdropPath, Integer id)
    {
        this.originalTitle = originalTitle;
        this.movieImagePath = movieImagePath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
        this.id = id;
    }

    public Movie(int roomId, String originalTitle, String movieImagePath, String overview, Double voteAverage, String releaseDate, String backdropPath, Integer id)
    {
        this.roomId = roomId;
        this.originalTitle = originalTitle;
        this.movieImagePath = movieImagePath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getMovieImagePath() {
        return movieImagePath;
    }

    public void setMovieImagePath(String movieImagePath) {
        this.movieImagePath = movieImagePath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    /**
     * Nello sviluppare applicazioni android, quando abbiamo la necessità di trasferire un oggetto creato da noi da un'activity ad un'altra (oppure anche in altri contesti),
     * dobbiamo ricorrere all'implementazione delle interfacce Parcelable e Parcelable.Creator.
     *
     * L'interfaccia Parcelable serve a registrare l'oggetto che stiamo trasferendo tramite il metodo writeToParcel() all'interno di un Parcel.
     * Mentre l'interfaccia Parcelable.Creator serve a ricostruire l'oggetto.
     */

    private Movie(Parcel parcel)
    {
        roomId = parcel.readInt();
        originalTitle = parcel.readString();
        movieImagePath = parcel.readString();
        overview = parcel.readString();
        if (parcel.readByte() == 0)
        {
            voteAverage = null;
        }
        else
        {
            voteAverage = parcel.readDouble();
        }
        releaseDate = parcel.readString();
        backdropPath = parcel.readString();
        id = parcel.readInt();
    }

    /**
     * Questo metodo in genere ritorna zero.
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>()
    {
        /**
         * Questo metodo viene invocato automaticamente nel momento in cui si tenta di ricostruire un ipotetico oggetto Parcelable.
         * Un caso molto comune lo troviamo nella classe Bundle quando invochiamo il metodo getParcelable().
         */
        @Override
        public Movie createFromParcel(Parcel source)
        {
            return new Movie(source);
        }

        /**
         * Viene ricostruito l'array di un oggetto parcelable.
         */
        @Override
        public Movie[] newArray(int size)
        {
            return new Movie[size];
        }
    };

    /**
     * Questo metodo riceve un oggetto Parcel dove andremo a trasferire gli attributi del nostro oggetto.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(roomId);
        dest.writeString(originalTitle);
        dest.writeString(movieImagePath);
        dest.writeString(overview);
        if (voteAverage == null)
        {
            dest.writeByte((byte) 0);
        }
        else
        {

            dest.writeByte((byte) 1);
            dest.writeDouble(voteAverage);
        }
        dest.writeString(releaseDate);
        dest.writeString(backdropPath);
        dest.writeInt(id);
    }
}