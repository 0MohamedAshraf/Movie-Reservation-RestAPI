package com.example.moviereservation.controller;


import com.example.moviereservation.dto.MovieDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.service.MovieService;
import com.example.moviereservation.service.MovieServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<MovieDto>> getAllMovies(){
        List<MovieDto> movieList = movieService.getAll();
        return ResponseEntity.ok(movieList);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable String id){
        MovieDto movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<MovieDto> getMovieByTitle(@PathVariable String title){
        MovieDto movie = movieService.getMovieByTitle(title);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<MovieDto>> filterByCategory(@PathVariable String category){
        List<MovieDto> movies = movieService.filterByCategory(category);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/top")
    public ResponseEntity<List<MovieDto>> topRatedMovies(){
        List<MovieDto> movies = movieService.topRatedMovies();
        return ResponseEntity.ok(movies);
    }

    @PostMapping
    public ResponseEntity<MovieDto> addMovie(@RequestBody MovieDto movie){
        MovieDto theMovie = movieService.addMovie(movie);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(theMovie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable String id,@RequestBody MovieDto newMovie){
        MovieDto theMovie = movieService.getMovieById(id);

        if(theMovie != null){
            newMovie.setId(theMovie.getId());
            MovieDto movie = movieService.addMovie(newMovie);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(movie);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable String id){
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

}
