package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.ScheduleSeatRequestDto;
import com.example.moviereservation.dto.response.ScheduleSeatResponseDto;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.Seat;
import org.springframework.stereotype.Component;

@Component
public class ScheduleSeatMapper {

    public ScheduleSeat dtoToEntity(ScheduleSeatRequestDto dto, Seat seat, Schedule schedule){
        return new ScheduleSeat(
                null,
                dto.getAvailable(),
                schedule,
                seat
        );
    }

    public ScheduleSeatResponseDto entityToDto(ScheduleSeat entity){
        return new ScheduleSeatResponseDto(
                entity.getId(),
                entity.getAvailable(),
                entity.getSchedule().getId(),
                entity.getSeat().getId()
        );
    }
}
