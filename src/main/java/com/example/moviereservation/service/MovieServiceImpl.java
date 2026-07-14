package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.MovieRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.exceptions.InvalidMovieEntityException;
import com.example.moviereservation.exceptions.MovieAlreadyExistsException;
import com.example.moviereservation.exceptions.MovieNotFoundException;
import com.example.moviereservation.mapper.MovieMapper;
import com.example.moviereservation.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper mapper;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, MovieMapper mapper) {
        this.movieRepository = movieRepository;
        this.mapper = mapper;
    }


    private void validateMovie(MovieRequestDto movie){
        if(movie.getRating() > 5 || movie.getRating() < 0)
            throw new InvalidMovieEntityException("Movie Rating Must be between 0-5");
        if(movie.getDuration() < 0)
            throw new InvalidMovieEntityException("Movie Duration Must be positive");
    }

    @Override
    public List<MovieResponseDto> getAll(){
        return movieRepository.findAll()
                .stream()
                .map(mapper::entityToDto).toList();
    }

    @Override
    public MovieResponseDto getMovieById(Integer id){
        Movie theMovie = movieRepository.findById(id)
                .orElseThrow(
                        () -> new MovieNotFoundException("Movie With ID: " + id + " Not Found")
                );

        return mapper.entityToDto(theMovie);


    }

    @Override
    public MovieResponseDto getMovieByTitle(String title){

        Movie theMovie =  movieRepository.findByTitle(title);
        if(theMovie == null)
            throw new MovieNotFoundException("Movie With title \"" + title + "\" Not Found");
        return mapper.entityToDto(theMovie);
    }

    @Override
    public MovieResponseDto addMovie(MovieRequestDto movie){
        validateMovie(movie);
        if(movieRepository.existsByTitle(movie.getTitle()))
            throw new MovieAlreadyExistsException("Movie \"" + movie.getTitle() + "\" Already Exists");

        Movie theMovie =  movieRepository.save(mapper.dtoToEntity(movie));
        return mapper.entityToDto(theMovie);

    }

    @Override
    public MovieResponseDto updateMovie(MovieRequestDto movie,Integer id) {
        validateMovie(movie);
        Movie theMovie = movieRepository.findById(id).orElseThrow(
                () -> new MovieNotFoundException("No Movie Found With Id: " + id)
        );
        Movie newMovie = mapper.dtoToEntity(movie);
        newMovie.setId(id);
        Movie updatedMovie = movieRepository.save(newMovie);
        return mapper.entityToDto(updatedMovie);

    }

    @Override
    public void deleteMovie(Integer id){
        if(getMovieById(id) != null)
            movieRepository.deleteById(id);
        else throw new MovieNotFoundException("Movie with id: " + id + " Not found");
    }

    @Override
    public List<MovieResponseDto> filterByCategory(String category) {
        List<Movie> movies = movieRepository.findByCategory(category);
        if(movies == null)
            return Collections.emptyList();
        return movies
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    public List<MovieResponseDto> topRatedMovies() {
        List<Movie> movies = movieRepository.findTopRated();
        if (movies == null)
            return Collections.emptyList();

        return movies
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    public List<MovieResponseDto> moviesReleasedAfter(LocalDate date) {
        if (date.isAfter(LocalDate.now()))
            throw new MovieNotFoundException("Please Enter Valid Date");
        List<Movie> movieList = movieRepository.findByReleaseDateAfter(date);
        if(movieList == null)
            return Collections.emptyList();

        return movieList
                .stream()
                .map(mapper::entityToDto)
                .toList();

    }
}
