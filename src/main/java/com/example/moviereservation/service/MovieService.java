package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.MovieRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface MovieService {
    List<MovieResponseDto> getAll();

    MovieResponseDto getMovieById(Integer id);

    MovieResponseDto getMovieByTitle(String title);

    MovieResponseDto addMovie(MovieRequestDto movie);

    MovieResponseDto updateMovie(MovieRequestDto movie, Integer id);

    void deleteMovie(Integer id);

    List<MovieResponseDto> filterByCategory(String category);

    List<MovieResponseDto> topRatedMovies();

    List<MovieResponseDto> moviesReleasedAfter(LocalDate date);
}
