package com.example.soymilk.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {

    // the base URL for loading images
    String imageBaseUrl;
    // the poster size to use when fetching images
    String posterSize;
    // the backdrop size to use when fetching images
    String backdropSize;


    public Config(JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images");
        imageBaseUrl = images.getString("secure_base_url");
        // get the poster size stored in an array
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");

        // use the option at index 3 or w342 as a fallback
        posterSize = posterSizeOptions.optString(3, "w342");
        // ^if there's nothing at index 3, return "w342" instead

        // parse the backdrop sizes and use the option at index 1 or "w780"
        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
        backdropSize = backdropSizeOptions.optString(1, "w780");

    }

    // helper method to construct request url for image

    public String getImageUrl(String size, String path){
        return String.format("%s%s%s", imageBaseUrl, size, path);
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }


    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }
}
