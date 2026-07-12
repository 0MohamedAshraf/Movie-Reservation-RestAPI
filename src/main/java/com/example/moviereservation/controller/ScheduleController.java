package com.example.moviereservation.controller;

import com.example.moviereservation.dto.MovieDto;
import com.example.moviereservation.dto.ScheduleDto;
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
    public List<ScheduleDto> getAll(){
        return scheduleService.getAll();
    }

    @GetMapping("/{id}")
    public ScheduleDto getById(@PathVariable String id){
        return scheduleService.getScheduleById(id);
    }

    @GetMapping("/{scheduleId}/movie")
    public MovieDto getMovie(@PathVariable String scheduleId){
        return scheduleService.showMovie(scheduleId);
    }

    @PostMapping
    public ScheduleDto addSchedule(@RequestBody ScheduleDto schedule){
        return scheduleService.addSchedule(schedule);
    }

    @PutMapping
    public ScheduleDto updateMovie(@RequestBody ScheduleDto newSchedule){
        return scheduleService.addSchedule(newSchedule);
    }
}
