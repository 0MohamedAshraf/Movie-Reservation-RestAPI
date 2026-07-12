package com.example.moviereservation.service;


import com.example.moviereservation.dto.ScheduleDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    List<ScheduleDto> getAll();

    ScheduleDto getScheduleById(String id);

    ScheduleDto addSchedule(ScheduleDto schedule);

    ScheduleDto updateMovie(String scheduleId, Movie newMovie);

    ScheduleDto updatePrice(String scheduleId, Double newPrice);

    ScheduleDto updateTheater(String scheduleId, Theater newTheater);

    ScheduleDto changeTime(String scheduleId, LocalDateTime startTime, LocalDateTime endTime);

    Movie showMovie(String scheduleId);
}
