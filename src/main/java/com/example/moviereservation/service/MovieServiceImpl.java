package com.example.moviereservation.service;

import com.example.moviereservation.dto.MovieDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.exceptions.MovieNotFoundException;
import com.example.moviereservation.mapper.MovieMapper;
import com.example.moviereservation.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<MovieDto> getAll(){
        return movieRepository.findAll()
                .stream()
                .map(mapper::entityToDto).toList();
    }

    @Override
    public MovieDto getMovieById(String id){
        Movie theMovie = movieRepository.findById(id)
                .orElseThrow(
                        () -> new MovieNotFoundException("Movie With ID: " + id + " Not Found")
                );

        return mapper.entityToDto(theMovie);


    }

    @Override
    public MovieDto getMovieByTitle(String title){

        Movie theMovie =  movieRepository.findByTitle(title);
        if(theMovie == null)
            throw new MovieNotFoundException("Movie With title \"" + title + "\" Not Found");
        return mapper.entityToDto(theMovie);
    }

    @Override
    public MovieDto addMovie(MovieDto movie){
        try {
            Movie theMovie =  movieRepository.save(mapper.dtoToEntity(movie));
            return mapper.entityToDto(theMovie);
        }catch (IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }catch (Exception e){
            System.out.println("Unknown Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteMovie(String id){
        if(getMovieById(id) != null)
            movieRepository.deleteById(id);
        else throw new RuntimeException("Entity with id: " + id + " Not found");
    }

    @Override
    public List<MovieDto> filterByCategory(String category) {
        List<Movie> movies = movieRepository.findByCategory(category);
        if(movies == null)
            return Collections.emptyList();
        return movies
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    public List<MovieDto> topRatedMovies() {
        List<Movie> movies = movieRepository.findTopRated();
        if (movies == null)
            return Collections.emptyList();

        return movies
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }
}
