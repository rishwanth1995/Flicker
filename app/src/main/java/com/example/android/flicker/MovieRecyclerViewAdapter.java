package com.example.android.flicker;


import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by rishw on 9/8/2017.
 */

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    private Context mContext;
    private List<Movies> mMovies;
    private MyAdapterListener listener;

    public MovieRecyclerViewAdapter(Context mContext, List<Movies> mMovies, MyAdapterListener listener) {
        this.mContext = mContext;
        this.mMovies = mMovies;
        this.listener = listener;
    }

    public interface MyAdapterListener{
        void detailTextViewOnClick(View v, int position,Movies movie);
        void imageTextViewOnClick(View v, int position,Movies movie);
    }
    public class PopularViewHolder extends RecyclerView.ViewHolder{

        private ImageView backdropImage;
        private TextView overViewTextView;
        private TextView movieTitleView;
        private ImageView playImageView;
        private TextView mDetailTextView;
        public PopularViewHolder(View itemView) {
            super(itemView);
            backdropImage = (ImageView) itemView.findViewById(R.id.image_backdrop);
            playImageView = (ImageView) itemView.findViewById(R.id.imgPlayVideo);
            mDetailTextView = (TextView) itemView.findViewById(R.id.detailText);
            if(MovieActivity.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                overViewTextView = (TextView) itemView.findViewById(R.id.overview);
                movieTitleView = (TextView) itemView.findViewById(R.id.movietitle);
            }

        }

        public void bind(final Movies movie) {
            String backdropUri = movie.getImageThumbNail();

            Uri baseUri = Uri.parse(BASE_IMAGE_URL);
            Uri.Builder imageUri = baseUri.buildUpon();
            imageUri.path("/t/p/w500/"+ backdropUri);

            DisplayMetrics displayMetrics = itemView.getContext().getResources().getDisplayMetrics();

            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;


            Picasso.with(itemView.getContext()).load(imageUri.toString()).transform(new RoundedCornersTransformation(20,20)).resize(width,height/2).placeholder(R.drawable.movies).into(backdropImage);
            if(MovieActivity.orientation == Configuration.ORIENTATION_LANDSCAPE){
                Picasso.with(itemView.getContext()).load(imageUri.toString()).transform(new RoundedCornersTransformation(20,20)).resize(width*2/3,height*2/3).placeholder(R.drawable.movies).into(backdropImage);
                overViewTextView.setText(movie.getOverview());
                movieTitleView.setText(movie.getOriginalTitle());
            }
            backdropImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.imageTextViewOnClick(view,getAdapterPosition(),movie);
                }
            });

            mDetailTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.detailTextViewOnClick(view,getAdapterPosition(),movie);
                }
            });
        }
    }

    public class ViewHolderNormal extends RecyclerView.ViewHolder{

        private ImageView posterImage;
        private TextView overViewTextView;
        private TextView movieTitleView;
        private TextView detailTextView;

        public ViewHolderNormal(View itemView) {
            super(itemView);
            posterImage = (ImageView) itemView.findViewById(R.id.posterimage);
            overViewTextView = (TextView) itemView.findViewById(R.id.overview);
            movieTitleView = (TextView) itemView.findViewById(R.id.movietitle);
            detailTextView = (TextView) itemView.findViewById(R.id.detailText);
        }


        public void bind(final Movies movie) {
            String posterLink = movie.getPosterImage();
            Uri baseUri = Uri.parse(BASE_IMAGE_URL);
            Uri.Builder imageUri = baseUri.buildUpon();
            imageUri.path("/t/p/w185/"+ posterLink);

            DisplayMetrics displayMetrics = itemView.getContext().getResources().getDisplayMetrics();

            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            Picasso.with(itemView.getContext()).load(imageUri.toString()).transform(new RoundedCornersTransformation(20,20)).placeholder(R.drawable.movies).resize(width*2/5,height/2).into(posterImage);
            if(MovieActivity.orientation == Configuration.ORIENTATION_LANDSCAPE){
                Picasso.with(itemView.getContext()).load(imageUri.toString()).transform(new RoundedCornersTransformation(20,20)).resize(width/3,height*2/3).placeholder(R.drawable.movies).into(posterImage);
            }
            overViewTextView.setText(movie.getOverview());
            movieTitleView.setText(movie.getOriginalTitle());

            detailTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.detailTextViewOnClick(view,getAdapterPosition(),movie);
                }
            });
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;
        if(viewType == 0){
            view = layoutInflater.inflate(R.layout.popular_viewholder,parent,false);
            return new PopularViewHolder(view);
        }else if(viewType == 1){
            view = layoutInflater.inflate(R.layout.viewholder,parent,false);
            return new ViewHolderNormal(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Movies movie = mMovies.get(position);

        if(movie.getVoteAverage() > 7){
            ((PopularViewHolder) holder).bind(movie);
        }else if(movie.getVoteAverage() <= 7){
            ((ViewHolderNormal) holder).bind(movie);
        }
    }

    @Override
    public int getItemCount() {
        if(mMovies != null) {
            return mMovies.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(mMovies.get(position).getVoteAverage() > 7){
            return 0;
        }else if(mMovies.get(position).getVoteAverage() <= 7){
            return 1;
        }else{
            return -1;
        }
    }
}
