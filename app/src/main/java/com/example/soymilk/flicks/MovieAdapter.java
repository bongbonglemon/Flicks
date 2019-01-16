package com.example.soymilk.flicks;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.soymilk.flicks.models.Config;
import com.example.soymilk.flicks.models.Movie;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {


    // list of movies
    ArrayList<Movie> movies;

    // config needed for image urls
    Config config;
    // contexr for rendering
    Context context;



    public void setConfig(Config config) {
        this.config = config;
    }

    //initialise with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    // create the viewholder as a static inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        //track View objects
        ImageView ivPosterImage;
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivBackdropImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPosterImage = (ImageView)itemView.findViewById(R.id.ivPosterImage);
            tvOverview = (TextView)itemView.findViewById(R.id.tvOverview);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivBackdropImage = (ImageView)itemView.findViewById(R.id.ivBackdrop);
        }
    }

    // creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // get the context and create the inflater
        context =  parent.getContext();
        LayoutInflater inflater =  LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        //return a new ViewHolder
        return new ViewHolder(movieView);
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Movie movie = movies.get(position);
        // populate the view with the movie data
        // here i'm editting the elements of the view in my viewHolder
        viewHolder.tvTitle.setText(movie.getTitle());
        viewHolder.tvOverview.setText(movie.getOverview());

        // determine the current orientation
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;


        String imageUrl;
        // if in portrait mode, load the poster image
        if(isPortrait){
        imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        } else {
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }
        // build image url

        // get the placeholder and ImageView depending on the orientation
        int placeholderId = isPortrait ?  R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder; //ternary expression?
        ImageView imageView = isPortrait ? viewHolder.ivPosterImage : viewHolder.ivBackdropImage;


        // load image using Glide
        Glide.with(context)
                .load(imageUrl)

                .apply(new RequestOptions()
                        .placeholder(placeholderId)
                        .error(placeholderId)
                        .transform(new RoundedCorners(30))
                )

                .into(imageView);


    }

    // returns the total number of movies in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }
}
