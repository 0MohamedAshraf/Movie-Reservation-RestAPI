package com.example.moviereservation.mapper;


import com.example.moviereservation.dto.TheaterDto;
import com.example.moviereservation.entity.Theater;
import org.springframework.stereotype.Component;

@Component
public class TheaterMapper {

    public TheaterDto entityToDto(Theater theater){
        return new TheaterDto(
                theater.getId(),
                theater.getName(),
                theater.getCity(),
                theater.getTotalSeats()
        );
    }

    public Theater dtoToEntity(TheaterDto dto){
        return new Theater(
                dto.getId(),
                dto.getName(),
                dto.getCity(),
                dto.getTotalSeats()
        );
    }
}
