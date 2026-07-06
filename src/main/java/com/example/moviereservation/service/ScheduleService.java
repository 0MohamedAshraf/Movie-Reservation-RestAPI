package com.example.moviereservation.service;


import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {
    List<Schedule> getAll();

    Schedule getScheduleById(String id);

    Schedule addSchedule(Schedule schedule);

    Schedule updateMovie(String scheduleId, Movie newMovie);

    Schedule updatePrice(String scheduleId, Double newPrice);

    Schedule updateTheater(String scheduleId, Theater newTheater);

    Schedule changeTime(String scheduleId, LocalDateTime startTime, LocalDateTime endTime);

    Movie showMovie(String scheduleId);
}
