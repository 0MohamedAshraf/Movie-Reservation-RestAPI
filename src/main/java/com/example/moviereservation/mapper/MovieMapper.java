package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.MovieDto;
import com.example.moviereservation.dto.ScheduleDto;
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


    public MovieDto entityToDto(Movie movie){
        List<ScheduleDto> movieSchedules;
        if(movie.getSchedules() != null)
            movieSchedules = movie.getSchedules().stream().map(scheduleMapper::entityToDto).toList();
        else movieSchedules = Collections.emptyList();
        return new MovieDto(
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

    public Movie dtoToEntity(MovieDto movieDto){
        return new Movie(
                movieDto.getId(),
                movieDto.getTitle(),
                movieDto.getDescription(),
                movieDto.getRating(),
                movieDto.getCategory(),
                movieDto.getDuration()
        );
    }
}
