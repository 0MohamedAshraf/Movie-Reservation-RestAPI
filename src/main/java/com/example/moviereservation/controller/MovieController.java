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

    @GetMapping("/category/{category}")
    public List<MovieDto> filterByCategory(@PathVariable String category){
        return movieService.filterByCategory(category);
    }

    @GetMapping("/top")
    public List<MovieDto> topRatedMovies(){
        return movieService.topRatedMovies();
    }

    @PostMapping
    public MovieDto addMovie(@RequestBody MovieDto movie){

        return movieService.addMovie(movie);
    }

    @PutMapping("/{id}")
    public MovieDto updateMovie(@PathVariable String id,@RequestBody MovieDto newMovie){
        MovieDto theMovie = movieService.getMovieById(id);

        if(theMovie != null){
            newMovie.setId(theMovie.getId());
            return movieService.addMovie(newMovie);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable String id){
        movieService.deleteMovie(id);
    }

}
