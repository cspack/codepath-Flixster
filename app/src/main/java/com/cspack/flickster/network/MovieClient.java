package com.cspack.flickster.network;

import android.support.annotation.Nullable;
import android.util.Log;

import com.cspack.flickster.models.Movie;
import com.cspack.flickster.models.MovieVideo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by chris on 10/10/16.
 */

public class MovieClient {
    private static final String TAG = MovieClient.class.getSimpleName();

    public interface OnMovieListReady {
        // Notify that the movie loaded.
        public void onMovieListReady(List<Movie> movies);
    }

    public interface OnMovieVideosReady {
        // Notify that the movie loaded.
        public void onMovieVideosReady(int movieId, List<MovieVideo> videos);
    }

    private static MovieClient sInstance;
    public static synchronized MovieClient getInstance() {
        if (sInstance == null) {
            sInstance = new MovieClient();
        }
        return sInstance;
    }

    public void asyncGetNowPlaying(final OnMovieListReady ready)  {
        final String nowPlayingUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(nowPlayingUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray movieResults = response.getJSONArray("results");
                    ready.onMovieListReady(Movie.fromJSONArray(movieResults));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                throwable.printStackTrace();
            }
        });
    }

    public void asyncGetMovieVideos(final int movieId, final OnMovieVideosReady ready)  {
        final String detailsUrl = String.format("https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", movieId);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(detailsUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray movieResults = response.getJSONArray("results");
                    ready.onMovieVideosReady(movieId, MovieVideo.fromJSONArray(movieResults));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                throwable.printStackTrace();
            }
        });
    }
}
