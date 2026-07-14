package com.example.moviereservation.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDto {
    private Integer id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Date date;

    private Double price;

    private Integer movieId;

    private Integer theaterId;

}
