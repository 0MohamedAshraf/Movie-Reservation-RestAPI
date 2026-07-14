package com.example.moviereservation.controller;

import com.example.moviereservation.dto.request.TheaterRequestDto;
import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.dto.response.TheaterScheduleDto;
import com.example.moviereservation.service.TheaterServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/theater")
public class TheaterController {

    private final TheaterServiceImpl theaterService;

    @Autowired
    public TheaterController(TheaterServiceImpl theaterService) {
        this.theaterService = theaterService;
    }

    @GetMapping
    public List<TheaterResponseDto> getAll(){
        return theaterService.getAll();
    }

    @GetMapping("/id/{id}")
    public TheaterResponseDto getTheaterById(@PathVariable Integer id){
        return theaterService.getTheaterById(id);
    }

    @GetMapping("/name/{name}")
    public TheaterResponseDto getTheaterByName(@PathVariable String name){
        return theaterService.getTheaterByName(name);
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<TheaterScheduleDto>> getTheaterSchedules(@PathVariable Integer id){
        return ResponseEntity
                .ok(theaterService.getTheaterSchedules(id));
    }

    @PostMapping
    public TheaterResponseDto addTheater(@RequestBody TheaterRequestDto theater){
        return theaterService.addTheater(theater);
    }
}
