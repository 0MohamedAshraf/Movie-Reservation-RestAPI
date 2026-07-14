package com.example.moviereservation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDto {

    private String title;

    private String description;

    private Float rating;

    private LocalDate releaseDate;

    private String category;

    private Float duration;

}
