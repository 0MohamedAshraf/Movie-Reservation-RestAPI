package com.example.moviereservation.controller;

import com.example.moviereservation.dto.request.TheaterRequestDto;
import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.dto.response.TheaterScheduleDto;
import com.example.moviereservation.service.TheaterServiceImpl;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/theater")
public class TheaterController {

    private final TheaterServiceImpl theaterService;

    @GetMapping
    public ResponseEntity<List<TheaterResponseDto>> getAll(){
        return ResponseEntity
                .ok(theaterService.getAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TheaterResponseDto> getTheaterById(@PathVariable Integer id){
        return ResponseEntity
                .ok(theaterService.getTheaterById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TheaterResponseDto> getTheaterByName(@PathVariable String name){
        return ResponseEntity
                .ok(theaterService.getTheaterByName(name));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<TheaterResponseDto>> filterByCity(@PathVariable String city){
        return ResponseEntity
                .ok(theaterService.filterTheatersByCity(city));
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<TheaterScheduleDto>> getTheaterSchedules(@PathVariable Integer id){
        return ResponseEntity
                .ok(theaterService.getTheaterSchedules(id));
    }


    @PostMapping
    public ResponseEntity<TheaterResponseDto> addTheater(@RequestBody TheaterRequestDto theater){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(theaterService.addTheater(theater));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheaterResponseDto> updateTheater(@RequestBody TheaterRequestDto newTheater,@PathVariable Integer id){
        return ResponseEntity
                .ok(theaterService.updateTheater(newTheater,id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTheater(@PathVariable Integer id){
        return ResponseEntity
                .ok("Deleted Successfully");
    }
}
