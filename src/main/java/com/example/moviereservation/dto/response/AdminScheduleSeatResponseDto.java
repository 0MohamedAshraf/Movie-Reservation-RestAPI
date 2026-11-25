package com.example.moviereservation.dto.response;

import com.example.moviereservation.enums.ReservationStatus;
import com.example.moviereservation.enums.SeatClass;
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
public class AdminScheduleSeatResponseDto {
    private Integer id;
    private Boolean available;
    private Integer scheduleId;
    private String movieTitle;
    private String theaterName;
    private LocalDate date;
    private LocalDateTime startTime;
    private Integer seatId;
    private String rowNum;
    private Integer seatNum;
    private SeatClass seatClass;
    private String seatLabel;
    private Integer reservationId;
    private ReservationStatus reservationStatus;
}
