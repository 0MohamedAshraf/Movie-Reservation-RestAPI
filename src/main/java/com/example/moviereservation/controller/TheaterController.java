package com.example.moviereservation.controller;

import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.service.TheaterServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/theater")
public class TheaterController {

    private TheaterServiceImpl theaterService;

    @Autowired
    public TheaterController(TheaterServiceImpl theaterService) {
        this.theaterService = theaterService;
    }

    @GetMapping
    public List<Theater> getAll(){
        return theaterService.getAll();
    }

    @GetMapping("/id/{id}")
    public Theater getTheaterById(@PathVariable String id){
        return theaterService.getTheaterById(id);
    }

    @GetMapping("/name/{name}")
    public Theater getTheaterByName(@PathVariable String name){
        return theaterService.getTheaterByName(name);
    }

    @PostMapping
    public Theater addTheater(@RequestBody Theater theater){
        return theaterService.addTheater(theater);
    }
}
