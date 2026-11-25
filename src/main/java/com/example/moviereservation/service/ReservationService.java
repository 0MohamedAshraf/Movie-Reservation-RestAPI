package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.ReservationRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;

import java.util.List;

public interface ReservationService {

    ReservationResponseDto reserveSchedule(ReservationRequestDto request);

    void cancelReservation(Integer reservationId);

    void approveReservation(Integer reservationId);

    List<ReservationResponseDto> getByUserId(Integer userId);
}
