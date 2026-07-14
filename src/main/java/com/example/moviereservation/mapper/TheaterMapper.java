package com.example.moviereservation.mapper;


import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.entity.Theater;
import org.springframework.stereotype.Component;

@Component
public class TheaterMapper {

    public TheaterResponseDto entityToDto(Theater theater){
        return new TheaterResponseDto(
                theater.getId(),
                theater.getName(),
                theater.getCity(),
                theater.getTotalSeats()
        );
    }

    public Theater dtoToEntity(TheaterResponseDto dto){
        return new Theater(
                dto.getId(),
                dto.getName(),
                dto.getCity(),
                dto.getTotalSeats()
        );
    }
}
