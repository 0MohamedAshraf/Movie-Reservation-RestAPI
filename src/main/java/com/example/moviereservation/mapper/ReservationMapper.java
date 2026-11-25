package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.ReservationRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.entity.ReservationSeat;
import com.example.moviereservation.enums.ReservationStatus;
import com.example.moviereservation.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationMapper {
    private final ReservationSeatRepository reservationSeatRepository;

    public ReservationResponseDto toDto(Reservation entity){
        List<Integer> seats = reservationSeatRepository.getReservedSeatsIds(entity.getId());
        return new ReservationResponseDto(
                entity.getId(),
                entity.getPaymentMethod(),
                entity.getTotalPrice(),
                entity.getStatus(),
                entity.getBookingDate(),
                entity.getUser().getId(),
                entity.getSchedule().getId(),
                seats
        );
    }

    public Reservation toEntity(ReservationRequestDto request){
        Reservation reservation = new Reservation();

        reservation.setPaymentMethod(request.getPaymentMethod());
        reservation.setStatus(ReservationStatus.PENDING);
        return reservation;
    }
}
