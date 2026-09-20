package com.example.moviereservation.service;


import com.example.moviereservation.dto.request.ScheduleRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.dto.response.ScheduleResponseDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Theater;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ScheduleService {
    List<ScheduleResponseDto> getAll();

    ScheduleResponseDto getScheduleById(Integer id);

    ScheduleResponseDto addSchedule(ScheduleRequestDto schedule);

    ScheduleResponseDto updateSchedule(ScheduleRequestDto newSchedule, Integer id);

    ScheduleResponseDto updateMovie(Integer scheduleId, Movie newMovie);

    ScheduleResponseDto updatePrice(Integer scheduleId, Double newPrice);

    ScheduleResponseDto updateTheater(Integer scheduleId, Theater newTheater);

    ScheduleResponseDto changeTime(Integer scheduleId, LocalDateTime startTime, LocalDateTime endTime);

    MovieResponseDto showMovie(Integer scheduleId);

    void deleteSchedule(Integer id);

    List<ScheduleResponseDto> getTodaySchedules();

    List<ScheduleResponseDto> getUpcomingSchedules();

    ScheduleResponseDto getScheduleByDate(LocalDate date);

}
