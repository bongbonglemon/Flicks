package com.example.soymilk.flicks;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.soymilk.flicks.models.Config;
import com.example.soymilk.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {

    /* Stuff learnt:

    1.
    Using TODO
    in comments to note future changes to be made

    2.
    To build a request URL, you need:
    base_url, "api_key" and api_key (sensitive)

    3.
    Using ViewHolder to speed up population of the
    ListView considerably by caching view lookups for smoother, faster item loading

    Only findViewById for each item in the list once

    4.
    ViewHolder holds a View like a carriage

    5.
    I think key difference between ListView and RecyclerView is that the latter requires
    ViewHolder

    Questions:
    1.
    Does AsyncHttpClient work in background thread by default, or still need an AsyncTask
     */


    // constants
    // the base URL for the API
    public static final String API_BASE_URL = "https://api.themoviedb.org/3";
    // the parameter name for the API key
    public static final String API_KEY_PARAM = "api_key";

    // tag for logging from this activity
    public final static String TAG = "MovieListActivity";

    // instance fields (that only have values associated with
    // a specific instance of MovieListActivity)
    AsyncHttpClient client;


    // String variables to construct request url for images
    //To build an image URL, you will need 3 pieces of data. The base_url, size and file_path.

    // the base URL for loading images
    String imageBaseUrl;
    // the poster size to use when fetching images
    String posterSize;


    // the list of currently playing  movies
    ArrayList<Movie> movies;
    // the RecyclerView
    RecyclerView rvMovies;
    // the adapter wired to the RecyclerView
    MovieAdapter adapter;
    // image config
    Config config;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        //initialise the client
        client =  new AsyncHttpClient();
        //initialise the ArrayList
        movies = new ArrayList<>(); //empty for now
        // initialise adapter since movies ArrayList has to be passed to adapter
        // Note also movies ArrayList should not be reinitialised after this
        adapter = new MovieAdapter(movies);

        // resolve the reference to the RecyclerView
        rvMovies = (RecyclerView)findViewById(R.id.rvMovies);
        // connect layout manager (does design stuff) and adapter to the View
        rvMovies.setLayoutManager(new LinearLayoutManager(this)); // using an in-built LayoutManager
        rvMovies.setAdapter(adapter);



        //get the configuration on app creation
        getConfiguration();


    }

    //get the list of currently playing movies from the API
    private void getNowPlaying(){
        //create the url
        String url = API_BASE_URL + "/movie/now_playing";
        //set the request parameters that get appended to the request url
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // (key,value)
        //execute a GET result expecting a JSON object response
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //load the results into movies list
                try {
                    JSONArray results = response.getJSONArray("results");
                    for(int i = 0; i < results.length(); i++){
                        movies.add(new Movie(results.getJSONObject(i)));
                        // notify  adapter that a row has been added
                        adapter.notifyItemInserted(movies.size() - 1); // last index

                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));

                } catch (JSONException e) {
                    logError("Error parsing now_playing data", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                logError("Error to get data from now_playing endpoint", throwable, true);
            }
        });
    }

    // get the configuration from the APi
    // to retrieve secure_base_url and poster_size

    private void getConfiguration(){
        //create the url
        String url = API_BASE_URL + "/configuration";
        //set the request parameters that get appended to the request url
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // (key,value)
        // execute a GET request expecting a JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                // get the image base url
                try {
                    config = new Config(response);

                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSize %s",
                            config.getImageBaseUrl(), config.getPosterSize()));
                    // pass config to adapter
                    adapter.setConfig(config);
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Error parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                logError("Failed getting configuration", throwable, true);
            }
        });
    }

    // MISC: handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser){
        // always log the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if (alertUser){
            //show a long Toast message with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }



}
