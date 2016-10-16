package com.cspack.flickster.models;

import android.support.v7.graphics.Palette;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 10/11/16.
 */

public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;

    private String backdropPath, posterPath, originalTitle, overview, releaseDate;
    private double voteAverage;
    private int id;
    private long timestamp;

    // Generated function after image loads.
    private transient Palette.Swatch colorSwatch = null;
    public Palette.Swatch getColorSwatch() {
        return colorSwatch;
    }
    public synchronized void setColorSwatch(Palette.Swatch colorSwatch) {
        this.colorSwatch = colorSwatch;
    }

    public Movie(JSONObject jsonObject) throws JSONException {
        timestamp = System.currentTimeMillis();
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        originalTitle = jsonObject.getString("original_title");
        overview = jsonObject.getString("overview");
        releaseDate = jsonObject.getString("release_date");
        id = jsonObject.getInt("id");
        voteAverage = jsonObject.getDouble("vote_average");
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w300%s?ts=%d", backdropPath, timestamp);
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w300%s?ts=%d", posterPath, timestamp);
    }

    public int getId() { return id; }
    public String getOriginalTitle() {
        return originalTitle;
    }
    public String getOverview() {
        return overview;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public double getVoteAverage() { return voteAverage; }


    public static List<Movie> fromJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<Movie> movie = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            movie.add(new Movie(jsonArray.getJSONObject(i)));
        }
        return movie;
    }
}
