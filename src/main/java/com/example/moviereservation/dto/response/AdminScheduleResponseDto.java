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
public class AdminScheduleResponseDto {
    private Integer id;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;
    private Integer movieId;
    private String movieTitle;
    private Integer theaterId;
    private String theaterName;
    private String theaterCity;
}
