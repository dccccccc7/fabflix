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

public class ListViewActivity extends Activity {

    private EditText searchInput;
    private Button searchButton;
    private Button nextButton;
    private Button prevButton;

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

    private int pageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.search);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);

        searchInput.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                            pageNumber = 1;
                            search();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pageNumber=1;
                search();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pageNumber++;
                search();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pageNumber--;
                search();
            }
        });
    }

    //baseURL + "/api/movies?title=" + searchInput.getText().toString().trim()
    public void search() {
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/movies?type=search&title=" + searchInput.getText().toString().trim() + "&numResults=20&page=" + pageNumber,
                response -> {

                    // initialize movies array
                    final ArrayList<Movie> movies = new ArrayList<>();

                    try {
                        Log.d("debug: Search results", response);

                        // convert response into json object
                        JSONArray searchResults = new JSONArray(response);

                        // display next button if there are more than 20 movies
                        if (searchResults.length() > 20) {
                            nextButton.setVisibility(View.VISIBLE);
                        }
                        else {
                            nextButton.setVisibility(View.INVISIBLE);
                        }


                        // display prev button if page number > 1
                        if (pageNumber > 1) {
                            prevButton.setVisibility(View.VISIBLE);
                        }
                        else {
                            prevButton.setVisibility(View.INVISIBLE);
                        }



                        for (int i = 0; i < searchResults.length(); i++) {
                            JSONObject movie = searchResults.getJSONObject(i);

                            //get first 3 genres
                            String genres = "";
                            JSONArray genresArray = new JSONArray(movie.getString("movie_genres"));
                            for (int j = 0; j < genresArray.length(); j++) {
                                if (j>=3) break;
                                JSONObject genre = genresArray.getJSONObject(j);
                                genres += genre.getString("genre_name");
                                genres += ", ";
                            }
                            genres = genres.replaceAll(", $", "");

                            //get first 3 stars
                            String stars = "";
                            JSONArray starsArray = new JSONArray(movie.getString("movie_stars"));
                            for (int k = 0; k < starsArray.length(); k++) {
                                if (k>=3) break;
                                JSONObject star = starsArray.getJSONObject(k);
                                stars += star.getString("star_name");
                                stars += ", ";
                            }
                            stars = stars.replaceAll(", $", "");

//                            Log.d("debug: Movie Title", movie.getString("movie_title"));
//                            Log.d("debug: Year", movie.getString("movie_year"));
//                            Log.d("debug: Genres ", genres);
//                            Log.d("debug: Stars ", stars);

                            movies.add(new Movie(movie.getString("movie_title"), movie.getString("movie_year"), movie.getString("movie_director"), genres, stars, movie.getString("movie_id")));
                        }

                        // populate table
                        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);
                        ListView listView = findViewById(R.id.list);
                        listView.setAdapter(adapter);

                        // if item on list is clicked
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Movie m = movies.get(position);
                            Log.d("debug: clicked movie", m.getName());
                            Log.d("debug: id", m.getId());

                            // initialize the activity(page)/destination
                            Intent moviePage = new Intent(ListViewActivity.this, MovieViewActivity.class);
                            moviePage.putExtra("movieId", m.getId());
                            // activate the list page.
                            startActivity(moviePage);

                        });


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