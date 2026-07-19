package com.example.moviereservation.controller;

import com.example.moviereservation.dto.request.ScheduleRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.dto.response.ScheduleResponseDto;
import com.example.moviereservation.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;


    @GetMapping
    public List<ScheduleResponseDto> getAll(){
        return scheduleService.getAll();
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<ScheduleResponseDto>> getUpComingSchedules(){
        return ResponseEntity
                .ok(scheduleService.getUpcomingSchedules());
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ScheduleResponseDto> getScheduleByDate(@PathVariable
                                                                 LocalDate date){
        return ResponseEntity
                .ok(scheduleService.getScheduleByDate(date));
    }

    @GetMapping("/date/today")
    public ResponseEntity<List<ScheduleResponseDto>> getTodaySchedules(){
        return ResponseEntity
                .ok(scheduleService.getTodaySchedules());
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
    public ScheduleResponseDto addSchedule(@RequestBody ScheduleRequestDto schedule){
        return scheduleService.addSchedule(schedule);
    }

    @PutMapping("/{id}")
    public ScheduleResponseDto updateMovie(@RequestBody ScheduleRequestDto newSchedule,
                                           @PathVariable Integer id){
        return scheduleService.updateSchedule(newSchedule,id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSchedule(@PathVariable Integer id){
        scheduleService.deleteSchedule(id);
        return ResponseEntity
                .ok("Deleted Successfully");
    }
}
