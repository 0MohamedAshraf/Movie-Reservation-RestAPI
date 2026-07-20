package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.ReservationRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.entity.Schedule;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ReservationMapper {

    public ReservationResponseDto toDto(Reservation entity){
        return new ReservationResponseDto(
                entity.getId(),
                entity.getPaymentMethod(),
                entity.getTotalPrice(),
                entity.getStatus(),
                entity.getBookingDate(),
                entity.getUser().getId(),
                entity.getSchedule().getId(),
                Collections.emptyList()
        );
    }

    public Reservation toEntity(ReservationRequestDto request){
        Reservation reservation = new Reservation();

        reservation.setPaymentMethod(request.getPaymentMethod());
        reservation.setStatus("PENDING");
        return reservation;
    }
}
