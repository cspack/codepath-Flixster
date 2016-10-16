package com.cspack.flickster.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cspack.flickster.R;
import com.cspack.flickster.adapters.MovieArrayAdapter;
import com.cspack.flickster.models.Movie;
import com.cspack.flickster.network.MovieClient;

import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity implements MovieClient.OnMovieListReady {
    private static final String TAG = MovieActivity.class.getSimpleName();
    private List<Movie> movieList = new ArrayList<>();
    private ListView lvMovieList;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // Dirty hack to get icon to show in new android.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_popcorn_white_48dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvMovieList = (ListView) findViewById(R.id.lvMovies);
        lvMovieList.setAdapter(new MovieArrayAdapter(MovieActivity.this, movieList));
        lvMovieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(MovieActivity.this, MovieDetailsActivity.class);
                Movie movie = (Movie) lvMovieList.getAdapter().getItem(position);
                detailsIntent.putExtra("movie", movie);
                startActivity(detailsIntent);
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (lvMovieList != null) {
                    ((MovieArrayAdapter) lvMovieList.getAdapter()).clear();
                }
                MovieClient.getInstance().asyncGetNowPlaying(MovieActivity.this);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Delayed load of movie data to listview.
        MovieClient.getInstance().asyncGetNowPlaying(this);
    }

    @Override
    public void onMovieListReady(List<Movie> movies) {
        if (swipeContainer != null && swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }
        if (lvMovieList != null) {
            ((MovieArrayAdapter) lvMovieList.getAdapter()).clear();
            ((MovieArrayAdapter) lvMovieList.getAdapter()).addAll(movies);
            lvMovieList.deferNotifyDataSetChanged();
        }
    }
}
