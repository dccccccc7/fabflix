package edu.uci.ics.fabflixmobile;

import java.util.ArrayList;

public class Movie {
    private final String name;
    private final String year;
    private final String director;
    private final String genreNames;
    private final String stars;
    private final String id;

    // ArrayList<String> genres;
    //private ArrayList<String> stars;

    public Movie(String name, String year, String director, String genreNames, String stars, String id) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.genreNames = genreNames;
        this.stars = stars;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getGenreNames() {
        return genreNames;
    }

    public String getStars() {
        return stars;
    }

    public String getId() {
        return id;
    }
}