package com.example.moviereservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TheaterScheduleDto {
    private Integer id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Date date;

    private Double price;

    private String movieTitle;
}
