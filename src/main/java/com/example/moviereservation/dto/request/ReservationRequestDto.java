package com.example.moviereservation.dto.request;

import com.example.moviereservation.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDto {

    private PaymentMethod paymentMethod;

    private Integer userId;

    private Integer scheduleId;

    private List<Integer> scheduleSeatIds;
}
