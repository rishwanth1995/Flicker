package com.example.android.flicker;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.util.ArrayList;

/**
 * Created by rishw on 9/13/2017.
 */

public class MovieDetailsActivity extends YouTubeFailureRecoveryActivity {
    private TextView titleTextView;
    private YouTubePlayerFragment youTubePlayerFragment;
    private RatingBar ratingBar;
    private TextView releaseDateTextView;
    private TextView overViewTextView;
    private ArrayList<String> videoIDS;




    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moviedetails);
        setTitle(getIntent().getStringExtra("originaltitle"));

        youTubePlayerFragment =(YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubefragment);
        youTubePlayerFragment.initialize(BuildConfig.YOUTUBE_DEVELOPER_KEY,this);

        videoIDS = getIntent().getStringArrayListExtra("videoIDS");
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setMax(10);
        ratingBar.setRating((float) getIntent().getDoubleExtra("voteaverage",5)/2);

        titleTextView = (TextView) findViewById(R.id.title1);
        titleTextView.setText(getIntent().getStringExtra("originaltitle"));

        releaseDateTextView = (TextView) findViewById(R.id.releaseDate);
        releaseDateTextView.setText(getIntent().getStringExtra("releasedate"));

        overViewTextView = (TextView) findViewById(R.id.overview);
        overViewTextView.setText(getIntent().getStringExtra("overview"));
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubefragment);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b){
            youTubePlayer.cueVideos(videoIDS);
        }
    }
}
