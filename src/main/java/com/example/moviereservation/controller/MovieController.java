package com.example.moviereservation.controller;


import com.example.moviereservation.dto.MovieDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.service.MovieService;
import com.example.moviereservation.service.MovieServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieDto> getAllMovies(){
        return movieService.getAll();
    }

    @GetMapping("/id/{id}")
    public MovieDto getMovieById(@PathVariable String id){
        return movieService.getMovieById(id);
    }

    @GetMapping("/title/{title}")
    public MovieDto getMovieByTitle(@PathVariable String title){
        return movieService.getMovieByTitle(title);
    }

    @PostMapping
    public MovieDto addMovie(@RequestBody MovieDto movie){

        return movieService.addMovie(movie);
    }
}
