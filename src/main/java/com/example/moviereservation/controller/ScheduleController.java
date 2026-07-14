package com.example.moviereservation.controller;

import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.dto.response.ScheduleResponseDto;
import com.example.moviereservation.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public List<ScheduleResponseDto> getAll(){
        return scheduleService.getAll();
    }

    @GetMapping("/{id}")
    public ScheduleResponseDto getById(@PathVariable Integer id){
        return scheduleService.getScheduleById(id);
    }

    @GetMapping("/{scheduleId}/movie")
    public MovieResponseDto getMovie(@PathVariable Integer scheduleId){
        return scheduleService.showMovie(scheduleId);
    }

    @PostMapping
    public ScheduleResponseDto addSchedule(@RequestBody ScheduleResponseDto schedule){
        return scheduleService.addSchedule(schedule);
    }

    @PutMapping
    public ScheduleResponseDto updateMovie(@RequestBody ScheduleResponseDto newSchedule){
        return scheduleService.addSchedule(newSchedule);
    }
}
