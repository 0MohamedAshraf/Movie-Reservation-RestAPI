package com.example.moviereservation.dto.request;

import jakarta.validation.constraints.*;
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

    @NotNull
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    @NotNull
    private Float rating;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    @NotBlank
    private String category;

    @Positive
    private Float duration;

}
