package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.ScheduleRequestDto;
import com.example.moviereservation.dto.response.ScheduleResponseDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {

    public ScheduleResponseDto entityToDto(Schedule schedule){
        return new ScheduleResponseDto(
                schedule.getId(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getDate(),
                schedule.getPrice(),
                schedule.getMovie().getId(),
                schedule.getTheater().getId()
        );
    }

    public Schedule dtoToEntity(ScheduleRequestDto scheduleRequestDto, Movie movie, Theater theater){

        return new Schedule(
                null,
                scheduleRequestDto.getStartTime(),
                scheduleRequestDto.getEndTime(),
                scheduleRequestDto.getDate(),
                scheduleRequestDto.getPrice(),
                movie,
                theater
        );
    }
}
