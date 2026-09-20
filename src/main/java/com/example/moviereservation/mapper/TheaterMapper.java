package com.example.moviereservation.mapper;


import com.example.moviereservation.dto.request.TheaterRequestDto;
import com.example.moviereservation.dto.response.ScheduleResponseDto;
import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.dto.response.TheaterScheduleDto;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class TheaterMapper {

    private final TheaterScheduleMapper scheduleMapper;

    public TheaterResponseDto entityToDto(Theater theater){
        List<TheaterScheduleDto> theaterSchedules;
        if(theater.getScheduleList() != null)
            theaterSchedules = theater.getScheduleList()
                    .stream()
                    .map(scheduleMapper::scheduleToDto)
                    .toList();
        else theaterSchedules = Collections.emptyList();

        return new TheaterResponseDto(
                theater.getId(),
                theater.getName(),
                theater.getCity(),
                theater.getTotalSeats(),
                theaterSchedules
        );
    }

    public Theater dtoToEntity(TheaterRequestDto dto){
        return new Theater(
                null,
                dto.getName(),
                dto.getCity(),
                dto.getTotalSeats()
        );
    }
}
