package com.example.moviereservation.controller;


import com.example.moviereservation.dto.request.MovieRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<MovieResponseDto>> getAllMovies(){
        List<MovieResponseDto> movieList = movieService.getAll();
        return ResponseEntity.ok(movieList);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<MovieResponseDto> getMovieById(@PathVariable Integer id){
        MovieResponseDto movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<MovieResponseDto> getMovieByTitle(@PathVariable String title){
        MovieResponseDto movie = movieService.getMovieByTitle(title);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<MovieResponseDto>> filterByCategory(@PathVariable String category){
        List<MovieResponseDto> movies = movieService.filterByCategory(category);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/top")
    public ResponseEntity<List<MovieResponseDto>> topRatedMovies(){
        List<MovieResponseDto> movies = movieService.topRatedMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/afterDate/{date}")
    public ResponseEntity<List<MovieResponseDto>> moviesAfterDate(@PathVariable LocalDate date){
        return ResponseEntity
                .ok(movieService.moviesReleasedAfter(date));
    }

    @PostMapping
    public ResponseEntity<MovieResponseDto> addMovie(@RequestBody MovieRequestDto movie){
        MovieResponseDto theMovie = movieService.addMovie(movie);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(theMovie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDto> updateMovie(@PathVariable Integer id, @RequestBody MovieRequestDto newMovie){

        MovieResponseDto movie = movieService.updateMovie(newMovie, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(movie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Integer id){
        movieService.deleteMovie(id);
        return ResponseEntity.ok("Deleted Successfully");
    }

}
