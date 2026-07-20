package com.example.moviereservation.controller;


import com.example.moviereservation.dto.request.ReservationRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService service;

    @PostMapping
    public ResponseEntity<ReservationResponseDto> reserve(@RequestBody
                                                          ReservationRequestDto request){
        ReservationResponseDto dto = service.reserveSchedule(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<String> cancelReservation(@PathVariable
                                                    Integer reservationId){
        service.cancelReservation(reservationId);
        return ResponseEntity.ok("Cancelled Successfully");
    }
}
