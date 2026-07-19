package com.example.moviereservation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleSeatRequestDto {

    private Integer scheduleId;

    private Boolean available;

    private Integer seatId;
}
