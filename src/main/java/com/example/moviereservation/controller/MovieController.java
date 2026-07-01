package com.example.moviereservation.controller;


import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.service.MovieService;
import com.example.moviereservation.service.MovieServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {

    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> getAllMovies(){
        return movieService.getAll();
    }

    @GetMapping("/id/{id}")
    public Movie getMovieById(@PathVariable String id){
        return movieService.getMovieById(id);
    }

    @GetMapping("/title/{title}")
    public Movie getMovieByTitle(@PathVariable String title){
        return movieService.getMovieByTitle(title);
    }

    @PostMapping
    public Movie addMovie(@RequestBody Movie movie){

        Movie theMovie = movieService.addMovie(movie);
        if(theMovie != null){
            return theMovie;
        }else throw new RuntimeException("Can't add Movie");
    }
}
