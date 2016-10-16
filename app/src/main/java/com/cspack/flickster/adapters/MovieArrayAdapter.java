package com.cspack.flickster.adapters;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cspack.flickster.R;
import com.cspack.flickster.models.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by chris on 10/11/16.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private static final String TAG = MovieArrayAdapter.class.getSimpleName();
    public enum MovieListDisplayType {
        BACKDROP_POPULAR_VIDEO,
        STANDARD_POSTER_OR_BACKDROP,
    }

    public static class ViewHolder {
        ImageView ivMovie;
        ProgressBar loadSpinner;
        TextView tvTitle;
        TextView tvOverview;
    }

    @Override
    public int getViewTypeCount() {
        return MovieListDisplayType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getVoteAverage() >= 5) {
            return MovieListDisplayType.BACKDROP_POPULAR_VIDEO.ordinal();
        }
        return MovieListDisplayType.STANDARD_POSTER_OR_BACKDROP.ordinal();

        // getItemViewType cannot change dynamically:
        // http://stackoverflow.com/questions/13000708/dynamically-update-view-types-of-listview
        /*
        return (getContext().getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT)
                ? MovieListDisplayType.POSTER_OVERVIEW.ordinal()
                : MovieListDisplayType.BACKDROP_AND_OVERVIEW.ordinal();
                */
    }

    private Activity activityContext;

    public MovieArrayAdapter(Activity activityContext, List<Movie> movies) {
        super(activityContext, R.layout.item_movie_standard, movies);
        this.activityContext = activityContext;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Movie movie = getItem(position);
        final MovieListDisplayType type = MovieListDisplayType.values()[getItemViewType(position)];
        Log.d(TAG, "Movie " + movie.getOriginalTitle() + " type is " + type + "; position " + position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflator = LayoutInflater.from(getContext());
            switch (type) {
                case STANDARD_POSTER_OR_BACKDROP:
                    convertView = inflator.inflate(R.layout.item_movie_standard, parent, false);
                    break;
                case BACKDROP_POPULAR_VIDEO:
                    convertView = inflator.inflate(R.layout.item_movie_popular, parent, false);
                    break;
            }
            Log.d(TAG, "Setting view holder for type " + type);
            viewHolder.ivMovie = (ImageView) convertView.findViewById(R.id.ivMovie);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            if (convertView.findViewById(R.id.tvOverview) != null) {
                viewHolder.tvOverview = (TextView) convertView.findViewById(R.id.tvOverview);
            }
            viewHolder.loadSpinner = (ProgressBar) convertView.findViewById(R.id.loadSpinner);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Rseet UI elements to loading states.
        viewHolder.ivMovie.setImageResource(0);
        viewHolder.ivMovie.setColorFilter(0);
        viewHolder.loadSpinner.setVisibility(View.VISIBLE);
        viewHolder.tvTitle.setText(movie.getOriginalTitle());
        if (viewHolder.tvOverview != null) {
            viewHolder.tvOverview.setText(movie.getOverview());
        }
        if (movie.getColorSwatch() == null) {
            Log.d(TAG, "Default color for movie " + " type " + type + "; position " + position);
            convertView.setBackgroundColor(Color.BLACK);
            if (viewHolder.tvOverview != null) {
                viewHolder.tvOverview.setTextColor(Color.GRAY);
            }
            viewHolder.tvTitle.setTextColor(Color.WHITE);
            if (type == MovieListDisplayType.BACKDROP_POPULAR_VIDEO) {
                viewHolder.tvTitle.setBackgroundColor(Color.argb(180, 0, 0, 0));
            } else {
                viewHolder.tvTitle.setBackgroundColor(0);
            }
        } else {
            final Palette.Swatch vibrant = movie.getColorSwatch();
            convertView.setBackgroundColor(vibrant.getRgb());
            if (viewHolder.tvOverview != null) {
                viewHolder.tvOverview.setTextColor(vibrant.getBodyTextColor());
            }
            viewHolder.tvTitle.setTextColor(vibrant.getTitleTextColor());
            int alphacolor = Color.HSVToColor(180, vibrant.getHsl());
            if (type == MovieListDisplayType.BACKDROP_POPULAR_VIDEO) {
                viewHolder.tvTitle.setBackgroundColor(alphacolor);
            }
            Log.d(TAG, "Fancy color for movie " + " type " + type + "; position " + position + " alphacolor " + String.format("%08X", alphacolor));
        }

        final String imageUrl = (type != MovieListDisplayType.BACKDROP_POPULAR_VIDEO &&
                getContext().getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_PORTRAIT)
                ? movie.getPosterPath() : movie.getBackdropPath();
        Picasso.with(activityContext)
                .load(imageUrl)
                .fit()
                .transform(
                new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        if (source != null) {
                            final Palette.Swatch vibrant = Palette.from(source)
                                    .maximumColorCount(16)
                                    .generate().getVibrantSwatch();
                            if (vibrant != null) {
                                movie.setColorSwatch(vibrant);
                                if (activityContext != null) {
                                    activityContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyDataSetChanged();
                                            return;
                                        }
                                    });
                                }
                            }
                        }
                        Log.d(TAG, "Setting color failed for " + " type " + type + "; position " + position);
                        return source;
                    }

                    @Override
                    public String key() {
                        return "";
                    }
                })
                .transform(new RoundedCornersTransformation(16, 16))
                .into(viewHolder.ivMovie, new Callback() {
            @Override
            public void onSuccess() {
                // note: In the doc this uses a null check here, but 'final' might make it
                // unnecessary. to check later.
                if (viewHolder != null && viewHolder.loadSpinner != null) {
                    viewHolder.loadSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError() {
                if (viewHolder != null && viewHolder.loadSpinner != null &&
                        viewHolder.ivMovie != null) {
                    viewHolder.loadSpinner.setVisibility(View.GONE);
                    viewHolder.ivMovie.setImageResource(R.drawable.image_broken);
                    viewHolder.ivMovie.setColorFilter(Color.WHITE);
                }
            }
        });
        return convertView;
    }
}
