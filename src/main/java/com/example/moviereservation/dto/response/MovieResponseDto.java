package com.example.moviereservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDto {
    private Integer id;

    private String title;

    private String description;

    private Float rating;

    private LocalDate releaseDate;

    private String category;

    private Float duration;

    private List<ScheduleResponseDto> schedules;

}
