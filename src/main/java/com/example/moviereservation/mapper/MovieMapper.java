package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.MovieDto;
import com.example.moviereservation.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {
    private final ScheduleMapper scheduleMapper;

    public MovieMapper(ScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }


    public MovieDto entityToDto(Movie movie){
        return new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getRating(),
                movie.getReleaseDate(),
                movie.getCategory(),
                movie.getDuration(),
                movie.getSchedules().stream().map(scheduleMapper::entityToDto).toList()
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
