package com.example.moviereservation.controller;

import com.example.moviereservation.config.OpenApiConfig;
import com.example.moviereservation.dto.request.TheaterRequestDto;
import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.dto.response.TheaterScheduleDto;
import com.example.moviereservation.service.TheaterServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/theater")
@Tag(name = "Theaters", description = "Browse theaters and their schedules; management operations require ADMIN")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class TheaterController {

    private final TheaterServiceImpl theaterService;

    @GetMapping
    @Operation(summary = "Get all theaters")
    @ApiResponse(responseCode = "200", description = "List of theaters")
    public ResponseEntity<List<TheaterResponseDto>> getAll(){
        return ResponseEntity
                .ok(theaterService.getAll());
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Get a theater by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Theater found"),
            @ApiResponse(responseCode = "404", description = "Theater not found")
    })
    public ResponseEntity<TheaterResponseDto> getTheaterById(@PathVariable Integer id){
        return ResponseEntity
                .ok(theaterService.getTheaterById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get a theater by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Theater found"),
            @ApiResponse(responseCode = "404", description = "Theater not found")
    })
    public ResponseEntity<TheaterResponseDto> getTheaterByName(@PathVariable String name){
        return ResponseEntity
                .ok(theaterService.getTheaterByName(name));
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Filter theaters by city", description = "Returns all theaters in the given city.")
    @ApiResponse(responseCode = "200", description = "Matching theaters")
    public ResponseEntity<List<TheaterResponseDto>> filterByCity(@PathVariable String city){
        return ResponseEntity
                .ok(theaterService.filterTheatersByCity(city));
    }

    @GetMapping("/{id}/schedules")
    @Operation(summary = "Get a theater's schedules", description = "Lists the upcoming schedules for the given theater.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Upcoming schedules"),
            @ApiResponse(responseCode = "404", description = "Theater not found")
    })
    public ResponseEntity<List<TheaterScheduleDto>> getTheaterSchedules(@PathVariable Integer id){
        return ResponseEntity
                .ok(theaterService.getTheaterSchedules(id));
    }


    @PostMapping
    @Operation(summary = "Add a theater", description = "ADMIN only. Seats are generated from totalSeats.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Theater created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "409", description = "Theater already exists"),
            @ApiResponse(responseCode = "403", description = "Caller does not have the ADMIN role")
    })
    public ResponseEntity<TheaterResponseDto> addTheater(@Valid @RequestBody TheaterRequestDto theater){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(theaterService.addTheater(theater));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a theater", description = "ADMIN only. Replaces the theater with the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Theater updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "Theater not found"),
            @ApiResponse(responseCode = "403", description = "Caller does not have the ADMIN role")
    })
    public ResponseEntity<TheaterResponseDto> updateTheater(@Valid @RequestBody TheaterRequestDto newTheater,@PathVariable Integer id){
        return ResponseEntity
                .ok(theaterService.updateTheater(newTheater,id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a theater", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Theater deleted"),
            @ApiResponse(responseCode = "404", description = "Theater not found"),
            @ApiResponse(responseCode = "403", description = "Caller does not have the ADMIN role")
    })
    public ResponseEntity<String> deleteTheater(@PathVariable Integer id){
        theaterService.deleteTheater(id);
        return ResponseEntity
                .ok("Deleted Successfully");
    }
}
