package com.example.moviereservation.controller;

import com.example.moviereservation.config.OpenApiConfig;
import com.example.moviereservation.dto.request.ReservationRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/reservation")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Book and cancel seats for a schedule (JWT required)")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class ReservationController {

    private final ReservationService service;

    @PostMapping
    @Operation(
            summary = "Reserve seats",
            description = "Books one or more seats for a schedule. The total price is calculated from the " +
                    "schedule price and the number of seats; unavailable seats cause the whole booking to fail."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Seats reserved"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or unavailable seats"),
            @ApiResponse(responseCode = "404", description = "User, schedule, or seat not found")
    })
    public ResponseEntity<ReservationResponseDto> reserve(@Valid @RequestBody
                                                           ReservationRequestDto request){
        ReservationResponseDto dto = service.reserveSchedule(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    @PostMapping("/{reservationId}/cancel")
    @Operation(summary = "Cancel a reservation", description = "Releases the reserved seats so they can be booked again.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservation cancelled"),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "409", description = "Reservation was already cancelled")
    })
    public ResponseEntity<String> cancelReservation(@PathVariable
                                                     Integer reservationId){
        service.cancelReservation(reservationId);
        return ResponseEntity.ok("Cancelled Successfully");
    }
}
