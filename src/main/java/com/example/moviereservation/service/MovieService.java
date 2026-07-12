package com.example.moviereservation.service;

import com.example.moviereservation.dto.MovieDto;
import com.example.moviereservation.entity.Movie;

import java.util.List;

public interface MovieService {
    List<MovieDto> getAll();

    MovieDto getMovieById(String id);

    MovieDto getMovieByTitle(String title);

    MovieDto addMovie(MovieDto movie);

    void deleteMovie(String id);
}
