package com.example.android.flicker;

import java.util.ArrayList;

/**
 * Created by rishw on 9/8/2017.
 */

public class Movies {

    private String posterImage;
    private String originalTitle;
    private String overview;
    private Double voteAverage;
    private String imageThumbNail;
    private String releaseDate;
    private Integer ID;
    private ArrayList<String> vidoeIDS = new ArrayList<>();


    public Movies(String posterImage, String originalTitle, String overview, Double voteAverage, String imageThumbNail, String releaseDate,Integer ID) {
        this.posterImage = posterImage;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.imageThumbNail = imageThumbNail;
        this.releaseDate = releaseDate;
        this.ID = ID;
    }


    public String getPosterImage() {
        return posterImage;
    }


    public String getOriginalTitle() {
        return originalTitle;
    }


    public String getOverview() {
        return overview;
    }


    public Double getVoteAverage() {
        return voteAverage;
    }


    public String getImageThumbNail() {
        return imageThumbNail;
    }


    public String getReleaseDate() {
        return releaseDate;
    }

    public Integer getID(){return ID;}

    public ArrayList<String> getVidoeIDS(){ return vidoeIDS;}

    public void setVidoeIDS(ArrayList<String> vidoeIDS) {
        this.vidoeIDS = vidoeIDS;
    }
}
