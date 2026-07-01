package com.example.moviereservation.service;

import com.example.moviereservation.entity.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> getAll();

    Movie getMovieById(String id);

    Movie getMovieByTitle(String title);

    Movie addMovie(Movie movie);

    void deleteMovie(String id);
}
