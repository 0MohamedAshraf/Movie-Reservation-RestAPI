package com.example.moviereservation.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDto {
    private Integer id;

    private String paymentMethod;

    private Float totalPrice;

    private String status;

    private LocalDate bookingDate;

    private Integer userId;

    private Integer scheduleId;

    private List<Integer> reservedSeatsIds;

}
