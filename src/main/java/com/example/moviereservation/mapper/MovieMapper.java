package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.MovieRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.dto.response.ScheduleResponseDto;
import com.example.moviereservation.entity.Movie;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MovieMapper {
    private final ScheduleMapper scheduleMapper;

    public MovieMapper(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }


    public MovieResponseDto entityToDto(Movie movie){
        List<ScheduleResponseDto> movieSchedules;
        if(movie.getSchedules() != null)
            movieSchedules = movie.getSchedules().stream().map(scheduleMapper::entityToDto).toList();
        else movieSchedules = Collections.emptyList();
        return new MovieResponseDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getRating(),
                movie.getReleaseDate(),
                movie.getCategory(),
                movie.getDuration(),
                movieSchedules
        );
    }

    public Movie dtoToEntity(MovieRequestDto request){
        return new Movie(
                null,
                request.getTitle(),
                request.getDescription(),
                request.getReleaseDate(),
                request.getRating(),
                request.getCategory(),
                request.getDuration()
        );
    }
}
