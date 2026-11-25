package com.example.moviereservation.dto.response;

import com.example.moviereservation.enums.PaymentMethod;
import com.example.moviereservation.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminReservationResponseDto {
    private Integer id;
    private ReservationStatus status;
    private PaymentMethod paymentMethod;
    private Float totalPrice;
    private LocalDate bookingDate;
    private Integer userId;
    private String userName;
    private String userEmail;
    private Integer scheduleId;
    private String movieTitle;
    private String theaterName;
    private LocalDate date;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Integer> seatIds;
    private List<String> seatLabels;
}
