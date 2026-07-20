package com.example.moviereservation.dto.response;

import com.example.moviereservation.enums.PaymentMethod;
import com.example.moviereservation.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDto {
    private Integer id;

    private PaymentMethod paymentMethod;

    private Float totalPrice;

    private ReservationStatus status;

    private LocalDate bookingDate;

    private Integer userId;

    private Integer scheduleId;

    private List<Integer> reservedSeatsIds;

}
