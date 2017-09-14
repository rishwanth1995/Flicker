package com.example.android.flicker;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class MovieActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://api.themoviedb.org";
    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;
    public static MovieRecyclerViewAdapter movieRecyclerViewAdapter;
    public static int orientation;
    private RecyclerView recyclerView;
    ArrayList<Movies> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);


        orientation = getResources().getConfiguration().orientation;
        recyclerView = (RecyclerView) findViewById(R.id.activity_main);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder();
        urlBuilder.addPathSegments("3/movie/now_playing");
        urlBuilder.addQueryParameter("api_key",BuildConfig.THE_MOVIE_DB_API_TOKEN);

        getResultFromJSONResponse(urlBuilder.build());

        updateUI(arrayList);

    }

    public void updateUI(ArrayList<Movies> arrayList){



        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(MovieActivity.this, arrayList, new MovieRecyclerViewAdapter.MyAdapterListener() {

            @Override
            public void detailTextViewOnClick(View v, int position, Movies movie) {
                Intent intent = null;
                if(v.getId() == R.id.detailText){
                    intent = new Intent(MovieActivity.this,MovieDetailsActivity.class);
                    intent.putExtra("originaltitle",movie.getOriginalTitle());
                    intent.putExtra("voteaverage",movie.getVoteAverage());
                    intent.putExtra("releasedate",movie.getReleaseDate());
                    intent.putExtra("overview",movie.getOverview());
                    intent.putExtra("videoIDS", movie.getVidoeIDS());
                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }
                }

            }

            @Override
            public void imageTextViewOnClick(View v, int position, Movies movie) {
                Intent intent = null;
                if(v.getId() ==  R.id.image_backdrop){
                     intent = YouTubeStandalonePlayer.createVideosIntent(MovieActivity.this,BuildConfig.YOUTUBE_DEVELOPER_KEY,movie.getVidoeIDS());
                }
                if(intent != null){
                    if(canResolveIntent(intent)){
                        startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
                    }else{
                        YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog(MovieActivity.this,REQ_RESOLVE_SERVICE_MISSING).show();
                    }
                }
            }

            private boolean canResolveIntent(Intent intent) {
                List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
                return resolveInfo != null && !resolveInfo.isEmpty();
            }

        });

        Log.d("Movie", "onCreate:" + arrayList);

        recyclerView.setAdapter(movieRecyclerViewAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK){
            YouTubeInitializationResult errorReason = YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if(errorReason.isUserRecoverableError()){
                errorReason.getErrorDialog(this,0).show();
            }else{
                String errorMessage =
                        String.format("There was an error initializing the YouTubePlayer (%1$s)", errorReason.toString());
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getResultFromJSONResponse(HttpUrl httpUrl){
        ArrayList<Movies> result;

        String JSONResponse;

        makeHttpRequest(httpUrl);
        Log.d("JSON", "getResultFromJSONResponse:" + httpUrl);

        //result = extractResponseFromJSON(JSONResponse);

    }


    private void makeHttpRequest(HttpUrl httpUrl) {

        final OkHttpClient client = new OkHttpClient();


        final Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    final JSONArray jsonArray = jsonObject.getJSONArray("results");
                    if(jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonMovies = jsonArray.getJSONObject(i);
                            String moviesPosterPath = jsonMovies.getString("poster_path");
                            String moviesOverView = jsonMovies.getString("overview");
                            String moviesReleaseDate = jsonMovies.getString("release_date");
                            String moviesOriginalTilte = jsonMovies.getString("original_title");
                            Double moviesRating = jsonMovies.getDouble("vote_average");
                            String moviesthumbNail = jsonMovies.getString("backdrop_path");
                            Integer ID = jsonMovies.getInt("id");

                            Movies playingMovies = new Movies(moviesPosterPath, moviesOriginalTilte, moviesOverView, moviesRating, moviesthumbNail, moviesReleaseDate,ID);
                            arrayList.add(playingMovies);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            movieRecyclerViewAdapter.notifyDataSetChanged();
                            for(int i = 0; i < arrayList.size(); i++){
                                makeVideoHttpRequest(arrayList.get(i).getID(),i);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                };
            }

            private void makeVideoHttpRequest(Integer id, final int index) {
                HttpUrl.Builder builder = HttpUrl.parse(BASE_URL).newBuilder();
                builder.addPathSegments("3/movie/"+ id + "/videos");
                builder.addQueryParameter("api_key",BuildConfig.THE_MOVIE_DB_API_TOKEN);
                Request request1 = new Request.Builder()
                        .url(builder.build())
                        .get()
                        .build();
                client.newCall(request1).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ArrayList<String> videoIDs = new ArrayList<String>();
                        try{
                            String responseData = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseData);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            if(jsonArray != null){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    String key = jsonArray.getJSONObject(i).getString("key");
                                    videoIDs.add(key);
                                }
                                Log.d("video", "onResponse: " + videoIDs);
                                arrayList.get(index).setVidoeIDS(videoIDs);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });



    }



}
