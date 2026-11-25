package com.example.moviereservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminMovieResponseDto {
    private Integer id;
    private String title;
    private String category;
    private Float rating;
    private LocalDate releaseDate;
    private Float duration;
    private String description;
}
