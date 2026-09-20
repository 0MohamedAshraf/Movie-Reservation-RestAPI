package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.MovieRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public interface MovieService {
    Page<MovieResponseDto> getAll(int pageNum, int size, Sort.Direction direction,String sortCol);

    MovieResponseDto getMovieById(Integer id);

    MovieResponseDto getMovieByTitle(String title);

    MovieResponseDto addMovie(MovieRequestDto movie);

    MovieResponseDto updateMovie(MovieRequestDto movie, Integer id);

    void deleteMovie(Integer id);

    List<MovieResponseDto> filterByCategory(String category);

    Page<MovieResponseDto> topRatedMovies(int pageNum,int size);

    List<MovieResponseDto> moviesReleasedAfter(LocalDate date);
}
