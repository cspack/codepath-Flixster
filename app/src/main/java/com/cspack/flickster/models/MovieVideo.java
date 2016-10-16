package com.cspack.flickster.models;

import android.support.v7.graphics.Palette;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 10/13/16.
 */

public class MovieVideo {
    private String key, name, site, size, type;

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public MovieVideo(JSONObject jsonObject) throws JSONException {
        key = jsonObject.getString("key");
        name = jsonObject.getString("name");
        site = jsonObject.getString("site");
        size = jsonObject.getString("size");
        type = jsonObject.getString("type");
    }

    public static List<MovieVideo> fromJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<MovieVideo> movie = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            movie.add(new MovieVideo(jsonArray.getJSONObject(i)));
        }
        return movie;
    }
}
