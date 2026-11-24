package com.example.moviereservation.controller;

import com.example.moviereservation.config.OpenApiConfig;
import com.example.moviereservation.dto.request.MovieRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
@Tag(name = "Movies", description = "Browse the movie catalog; management operations require ADMIN")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    @Operation(summary = "Get all movies", description = "Returns a paginated, optionally sorted list of movies.")
    @ApiResponse(responseCode = "200", description = "Page of movies")
    public ResponseEntity<Page<MovieResponseDto>> getAllMovies(
            @Parameter(description = "Zero-based page number", example = "0") @RequestParam int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam int size,
            @Parameter(description = "Sort direction", example = "ASC")
            @RequestParam(required = false,defaultValue = "ASC") Sort.Direction order,
            @Parameter(description = "Field to sort by", example = "title")
            @RequestParam(required = false) String sort){

        return ResponseEntity.ok(movieService.getAll(page, size,order,sort));
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Get a movie by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie found"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<MovieResponseDto> getMovieById(@PathVariable Integer id){
        MovieResponseDto movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/title/{title}")
    @Operation(summary = "Get a movie by title")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie found"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<MovieResponseDto> getMovieByTitle(@PathVariable String title){
        MovieResponseDto movie = movieService.getMovieByTitle(title);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Filter movies by category", description = "Returns all movies in the given category.")
    @ApiResponse(responseCode = "200", description = "Matching movies")
    public ResponseEntity<List<MovieResponseDto>> filterByCategory(@PathVariable String category){
        List<MovieResponseDto> movies = movieService.filterByCategory(category);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/top")
    @Operation(summary = "Get top-rated movies", description = "Returns a paginated list of the highest-rated movies.")
    @ApiResponse(responseCode = "200", description = "Page of top-rated movies")
    public ResponseEntity<Page<MovieResponseDto>> topRatedMovies(
            @Parameter(description = "Zero-based page number", example = "0") @RequestParam int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam int size){

        return ResponseEntity.ok(movieService.topRatedMovies(page,size));
    }

    @GetMapping("/afterDate/{date}")
    @Operation(summary = "Get movies released after a date", description = "Date must be in ISO format (yyyy-MM-dd).")
    @ApiResponse(responseCode = "200", description = "Matching movies")
    public ResponseEntity<List<MovieResponseDto>> moviesAfterDate(@PathVariable LocalDate date){
        return ResponseEntity
                .ok(movieService.moviesReleasedAfter(date));
    }

    @PostMapping
    @Operation(summary = "Add a movie", description = "ADMIN only. Creates a new movie in the catalog.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movie created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "409", description = "Movie already exists"),
            @ApiResponse(responseCode = "403", description = "Caller does not have the ADMIN role")
    })
    public ResponseEntity<MovieResponseDto> addMovie(@Valid @RequestBody MovieRequestDto movie){
        MovieResponseDto theMovie = movieService.addMovie(movie);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(theMovie);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a movie", description = "ADMIN only. Replaces the movie with the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "403", description = "Caller does not have the ADMIN role")
    })
    public ResponseEntity<MovieResponseDto> updateMovie(@PathVariable Integer id,
                                                         @Valid @RequestBody MovieRequestDto newMovie){

        MovieResponseDto movie = movieService.updateMovie(newMovie, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(movie);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a movie", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie deleted"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "403", description = "Caller does not have the ADMIN role")
    })
    public ResponseEntity<?> deleteMovie(@PathVariable Integer id){
        movieService.deleteMovie(id);
        return ResponseEntity.ok("Deleted Successfully");
    }

}
