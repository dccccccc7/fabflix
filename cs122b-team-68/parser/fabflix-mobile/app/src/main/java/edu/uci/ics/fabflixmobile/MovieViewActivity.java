package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MovieViewActivity extends Activity{

    private TextView movieTitle;
    private TextView movieYear;
    private TextView movieStars;
    private TextView movieGenres;
    private TextView movieDirector;

    private String movieId = "empty id";

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    //private final String host = "10.0.2.2"; //local
    //private final String port = "8080";  //local

    private final String host = "54.177.64.82"; //aws
    private final String port = "8443"; //aws
    //private final String domain = "cs122b-spring21-project2-login-cart-example-war";
    private final String domain = "cs122b-spring21-team-122";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.movieview);
        movieTitle = findViewById(R.id.movieTitle);
        movieYear = findViewById(R.id.movieYear);
        movieStars = findViewById(R.id.movieStars);
        movieGenres = findViewById(R.id.movieGenres);
        movieDirector = findViewById(R.id.movieDirector);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            movieId = extras.getString("movieId");
        }

        Log.d("MOVIE ID:", movieId);
        setMovieData();
    }

    public void setMovieData() {
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/single-movie?id=" + movieId,
                response -> {


                    try {
                        Log.d("debug: Search results", response);

                        // convert response into json object
                        JSONArray singleMovieArray = new JSONArray(response);

                        JSONObject movie = singleMovieArray.getJSONObject(0);

                        //get first 3 genres
                        String genres = "";
                        JSONArray genresArray = new JSONArray(movie.getString("movie_genres"));
                        for (int j = 0; j < genresArray.length(); j++) {
                            JSONObject genre = genresArray.getJSONObject(j);
                            genres += genre.getString("genre_name");
                            genres += ", ";
                        }
                        genres = genres.replaceAll(", $", "");

                        //get first 3 stars
                        String stars = "";
                        JSONArray starsArray = new JSONArray(movie.getString("movie_stars"));
                        for (int k = 0; k < starsArray.length(); k++) {
                            JSONObject star = starsArray.getJSONObject(k);
                            stars += star.getString("star_name");
                            stars += ", ";
                        }
                        stars = stars.replaceAll(", $", "");

                        movieStars.setText("Stars: " + stars);
                        movieGenres.setText("Genres: " + genres);
                        movieTitle.setText(movie.getString("movie_title"));
                        movieYear.setText("Year: " + movie.getString("movie_year"));
                        movieDirector.setText("Director: " + movie.getString("movie_director"));




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {
        };

        // important: queue.add is where the login request is actually sent
        queue.add(searchRequest);

    }



}
