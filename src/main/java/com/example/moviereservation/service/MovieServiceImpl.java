package com.example.moviereservation.service;

import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> getAll(){
        return movieRepository.findAll();
    }

    @Override
    public Movie getMovieById(String id){
        Movie theMovie = movieRepository.findById(id).orElse(null);
        if(theMovie != null)
            return theMovie;
        else throw new RuntimeException("Can't Find Movie with Id: " + id);
    }

    @Override
    public Movie getMovieByTitle(String title){
        return movieRepository.findByTitle(title);
    }

    @Override
    public Movie addMovie(Movie movie){
        try {
            return movieRepository.save(movie);
        }catch (IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }catch (Exception e){
            System.out.println("Unknown Exception: " + e.getMessage());
        }
        return null;
    }

    public void deleteMovie(String id){
        if(getMovieById(id) != null)
            movieRepository.deleteById(id);
        else throw new RuntimeException("Entity with id: " + id + " Not found");
    }
}
