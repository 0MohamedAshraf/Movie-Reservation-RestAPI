package com.example.moviereservation.controller;

import com.example.moviereservation.config.OpenApiConfig;
import com.example.moviereservation.dto.response.AdminMovieResponseDto;
import com.example.moviereservation.dto.response.AdminReservationResponseDto;
import com.example.moviereservation.dto.response.AdminScheduleResponseDto;
import com.example.moviereservation.dto.response.AdminScheduleSeatResponseDto;
import com.example.moviereservation.dto.response.AdminSummaryResponseDto;
import com.example.moviereservation.dto.response.AdminTheaterResponseDto;
import com.example.moviereservation.dto.response.UserResponseDto;
import com.example.moviereservation.service.AdminService;
import com.example.moviereservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Read-only system overview and reservation decisions (ADMIN role required)")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class AdminController {

    private final AdminService adminService;
    private final ReservationService reservationService;

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard counts", description = "Counts of users, theaters, movies, schedules, seats and reservations.")
    public ResponseEntity<AdminSummaryResponseDto> getSummary() {
        return ResponseEntity.ok(adminService.getSummary());
    }

    @GetMapping("/users")
    @Operation(summary = "List all users", description = "Returns every registered user with their role (passwords are never exposed).")
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    @GetMapping("/theaters")
    @Operation(summary = "List all theaters")
    public ResponseEntity<List<AdminTheaterResponseDto>> getTheaters() {
        return ResponseEntity.ok(adminService.getTheaters());
    }

    @GetMapping("/movies")
    @Operation(summary = "List all movies")
    public ResponseEntity<List<AdminMovieResponseDto>> getMovies() {
        return ResponseEntity.ok(adminService.getMovies());
    }

    @GetMapping("/schedules")
    @Operation(summary = "List all schedules", description = "Includes the movie and theater names for each showtime.")
    public ResponseEntity<List<AdminScheduleResponseDto>> getSchedules() {
        return ResponseEntity.ok(adminService.getSchedules());
    }

    @GetMapping("/schedule-seats")
    @Operation(summary = "List all schedule seats", description = "Includes seat labels, availability and any active reservation linked to each seat.")
    public ResponseEntity<List<AdminScheduleSeatResponseDto>> getScheduleSeats() {
        return ResponseEntity.ok(adminService.getScheduleSeats());
    }

    @GetMapping("/reservations")
    @Operation(summary = "List all reservations", description = "Includes customer, showtime, booked seats and reservation status.")
    public ResponseEntity<List<AdminReservationResponseDto>> getReservations() {
        return ResponseEntity.ok(adminService.getReservations());
    }

    @PostMapping("/reservations/{reservationId}/approve")
    @Operation(summary = "Approve a reservation", description = "Moves a PENDING reservation to CONFIRMED. Other statuses are rejected.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservation approved"),
            @ApiResponse(responseCode = "400", description = "Reservation is not PENDING"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<String> approveReservation(@PathVariable Integer reservationId) {
        reservationService.approveReservation(reservationId);
        return ResponseEntity.ok("Approved Successfully");
    }

    @PostMapping("/reservations/{reservationId}/cancel")
    @Operation(summary = "Cancel a reservation", description = "Cancels a reservation and releases its seats back for booking.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservation cancelled"),
            @ApiResponse(responseCode = "400", description = "Reservation was already cancelled"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<String> cancelReservation(@PathVariable Integer reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok("Cancelled Successfully");
    }
}
