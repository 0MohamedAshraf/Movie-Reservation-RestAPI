package com.example.moviereservation.controller;

import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public List<Schedule> getAll(){
        return scheduleService.getAll();
    }

    @GetMapping("/{id}")
    public Schedule getById(@PathVariable String id){
        return scheduleService.getScheduleById(id);
    }

    @GetMapping("/{scheduleId}/movie")
    public Movie getMovie(@PathVariable String scheduleId){
        return scheduleService.showMovie(scheduleId);
    }

    @PostMapping
    public Schedule addSchedule(@RequestBody Schedule schedule){
        return scheduleService.addSchedule(schedule);
    }

    @PutMapping
    public Schedule updateMovie(@RequestBody Schedule newSchedule){
        return scheduleService.addSchedule(newSchedule);
    }
}
