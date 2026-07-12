package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.ScheduleDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {

    public ScheduleDto entityToDto(Schedule schedule){
        return new ScheduleDto(
                schedule.getId(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getDate(),
                schedule.getPrice(),
                schedule.getMovie().getId(),
                schedule.getTheater().getId()
        );
    }

    public Schedule dtoToEntity(ScheduleDto scheduleDto, Movie movie, Theater theater){

        return new Schedule(
                scheduleDto.getId(),
                scheduleDto.getStartTime(),
                scheduleDto.getEndTime(),
                scheduleDto.getDate(),
                scheduleDto.getPrice(),
                movie,
                theater
        );
    }
}
