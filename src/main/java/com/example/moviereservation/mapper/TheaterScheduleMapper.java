package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.response.TheaterScheduleDto;
import com.example.moviereservation.entity.Schedule;
import org.springframework.stereotype.Component;

@Component
public class TheaterScheduleMapper {

    public TheaterScheduleDto scheduleToDto(Schedule schedule){
        TheaterScheduleDto dto = new TheaterScheduleDto();

        dto.setId(schedule.getId());
        dto.setDate(schedule.getDate());
        dto.setPrice(schedule.getPrice());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setMovieTitle(schedule.getMovie().getTitle());

        return dto;
    }
}
