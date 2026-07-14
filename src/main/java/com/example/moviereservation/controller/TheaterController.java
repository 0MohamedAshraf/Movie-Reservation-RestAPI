package com.example.moviereservation.controller;

import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.service.TheaterServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    public TheaterResponseDto addTheater(@RequestBody TheaterResponseDto theater){
        return theaterService.addTheater(theater);
    }
}
