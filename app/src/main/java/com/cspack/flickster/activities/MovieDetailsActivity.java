package com.cspack.flickster.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cspack.flickster.R;
import com.cspack.flickster.models.Movie;
import com.cspack.flickster.models.MovieVideo;
import com.cspack.flickster.network.MovieClient;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailsActivity extends YouTubeBaseActivity
        implements MovieClient.OnMovieVideosReady {
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private Movie movie;
    private List<MovieVideo> videos;

    private ImageView trailerThumbnail;
    // For youtube.
    final String apiKey="AIzaSyCTumicMc5tWiF0VcZb1x1ijVUqDQWbi44";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);


        if (!getIntent().hasExtra("movie")) {
            finish();
        }
        movie = (Movie) getIntent().getExtras().getSerializable("movie");
        MovieClient.getInstance().asyncGetMovieVideos(movie.getId(), this);
        trailerThumbnail = (ImageView) findViewById(R.id.ivTrailer);
        Picasso.with(MovieDetailsActivity.this).load(movie.getBackdropPath()).into(trailerThumbnail);
        ((TextView) findViewById(R.id.tvTitle)).setText(movie.getOriginalTitle());
        ((TextView) findViewById(R.id.tvReleaseDate)).setText(getString(R.string.released_on, movie.getReleaseDate()));
        ((TextView) findViewById(R.id.tvOverview)).setText(movie.getOverview());
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        // vote average is 1 to 10, normalize to 5 stars (with half increments).
        ratingBar.setRating((float) movie.getVoteAverage() / 2.0f);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5f);
        // Assume no trailer ontul prepareYoutbeVideo is called.
        trailerThumbnail.setOnClickListener(null);
        findViewById(R.id.ivPlayButton).setVisibility(View.INVISIBLE);
    }

    private void initAndPlayYoutubeVideo(final String video) {
        final YouTubePlayerView youTubePlayerView =
                (YouTubePlayerView) findViewById(R.id.youtubePlayer);
        youTubePlayerView.setVisibility(View.VISIBLE);
        youTubePlayerView.initialize(apiKey,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.loadVideo(video);
                        trailerThumbnail.setOnClickListener(null);
                    }

                    @Override
                    public void onInitializationFailure(
                            YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                        Log.e(TAG, "Failed: " + youTubeInitializationResult);
                        youTubePlayerView.setVisibility(View.INVISIBLE);
                        findViewById(R.id.ivPlayButton).setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void prepareYoutubeVideo(final String key) {
        if (movie.getVoteAverage() < 5) {
            // Use play button.
            findViewById(R.id.ivPlayButton).setVisibility(View.VISIBLE);
            trailerThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initAndPlayYoutubeVideo(key);
                }
            });
        } else {
            // Play now.
            initAndPlayYoutubeVideo(key);
        }
    }

    @Override
    public void onMovieVideosReady(int movieId, List<MovieVideo> videos) {
        // not implemented.
        String youtubeKey = null;
        for (MovieVideo video : videos) {
            if (video.getSite().equalsIgnoreCase("youtube")) {
                youtubeKey = video.getKey();
                break;
            }
        }
        if (youtubeKey != null) {
            prepareYoutubeVideo(youtubeKey);
        }
    }
}
