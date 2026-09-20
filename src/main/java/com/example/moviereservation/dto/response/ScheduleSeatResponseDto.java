package com.example.moviereservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScheduleSeatResponseDto {

    private Integer id;

    private Boolean available;

    private Integer scheduleId;

    private Integer seatId;


}
