package com.example.moviereservation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleSeatRequestDto {

    @NotNull
    private Integer scheduleId;

    @NotNull
    private Boolean available;

    @NotNull
    private Integer seatId;
}
