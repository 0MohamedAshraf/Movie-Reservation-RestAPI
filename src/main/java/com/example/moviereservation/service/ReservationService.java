package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.ReservationRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;

public interface ReservationService {

    ReservationResponseDto reserveSchedule(ReservationRequestDto request);

    void cancelReservation(Integer reservationId);



}
