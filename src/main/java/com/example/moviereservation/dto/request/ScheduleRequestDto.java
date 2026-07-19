package com.example.moviereservation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDto {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDate date;

    private Double price;

    private Integer movieId;

    private Integer theaterId;

}
