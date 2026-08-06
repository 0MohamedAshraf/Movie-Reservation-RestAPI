package com.example.moviereservation.dto.request;

import com.example.moviereservation.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer scheduleId;

    @NotNull
    @NotEmpty
    private List<Integer> scheduleSeatIds;
}
