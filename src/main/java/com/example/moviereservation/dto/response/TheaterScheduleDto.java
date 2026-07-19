package com.example.moviereservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TheaterScheduleDto {
    private Integer id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDate date;

    private Double price;

    private String movieTitle;
}
