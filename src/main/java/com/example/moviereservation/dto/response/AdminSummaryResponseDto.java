package com.example.moviereservation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminSummaryResponseDto {
    private long users;
    private long theaters;
    private long movies;
    private long schedules;
    private long scheduleSeats;
    private long reservations;
    private long pendingReservations;
}
