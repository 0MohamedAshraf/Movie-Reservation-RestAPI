package com.example.moviereservation.controller;

import com.example.moviereservation.config.OpenApiConfig;
import com.example.moviereservation.dto.request.ScheduleRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.dto.response.ScheduleResponseDto;
import com.example.moviereservation.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/schedule")
@Tag(name = "Schedules", description = "Showtimes linking a movie and a theater; management operations require ADMIN")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class ScheduleController {

    private final ScheduleService scheduleService;


    @GetMapping
    @Operation(summary = "Get all schedules")
    @ApiResponse(responseCode = "200", description = "List of schedules")
    public List<ScheduleResponseDto> getAll(){
        return scheduleService.getAll();
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming schedules", description = "Returns schedules that have not taken place yet.")
    @ApiResponse(responseCode = "200", description = "Upcoming schedules")
    public ResponseEntity<List<ScheduleResponseDto>> getUpComingSchedules(){
        return ResponseEntity
                .ok(scheduleService.getUpcomingSchedules());
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get a schedule by date", description = "Date must be in ISO format (yyyy-MM-dd).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule found"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    public ResponseEntity<ScheduleResponseDto> getScheduleByDate(@PathVariable
                                                                  LocalDate date){
        return ResponseEntity
                .ok(scheduleService.getScheduleByDate(date));
    }

    @GetMapping("/date/today")
    @Operation(summary = "Get today's schedules")
    @ApiResponse(responseCode = "200", description = "Today's schedules")
    public ResponseEntity<List<ScheduleResponseDto>> getTodaySchedules(){
        return ResponseEntity
                .ok(scheduleService.getTodaySchedules());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a schedule by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule found"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    public ScheduleResponseDto getById(@PathVariable Integer id){
        return scheduleService.getScheduleById(id);
    }

    @GetMapping("/{scheduleId}/movie")
    @Operation(summary = "Get the movie for a schedule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie found"),
            @ApiResponse(responseCode = "404", description = "Schedule or movie not found")
    })
    public MovieResponseDto getMovie(@PathVariable Integer scheduleId){
        return scheduleService.showMovie(scheduleId);
    }

    @PostMapping
    @Operation(
            summary = "Add a schedule",
            description = "ADMIN only. Creates a showtime and auto-generates its seat map. " +
                    "endTime must be after startTime, price must be positive, and the theater cannot be double-booked."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or invalid time range"),
            @ApiResponse(responseCode = "404", description = "Movie or theater not found"),
            @ApiResponse(responseCode = "409", description = "Schedule overlaps an existing one"),
            @ApiResponse(responseCode = "403", description = "Caller does not have the ADMIN role")
    })
    public ScheduleResponseDto addSchedule(@Valid @RequestBody ScheduleRequestDto schedule){
        return scheduleService.addSchedule(schedule);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a schedule", description = "ADMIN only. Replaces the schedule with the given ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or invalid time range"),
            @ApiResponse(responseCode = "404", description = "Schedule, movie, or theater not found"),
            @ApiResponse(responseCode = "409", description = "Schedule overlaps an existing one"),
            @ApiResponse(responseCode = "403", description = "Caller does not have the ADMIN role")
    })
    public ScheduleResponseDto updateMovie(@Valid @RequestBody ScheduleRequestDto newSchedule,
                                            @PathVariable Integer id){
        return scheduleService.updateSchedule(newSchedule,id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a schedule", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule deleted"),
            @ApiResponse(responseCode = "404", description = "Schedule not found"),
            @ApiResponse(responseCode = "403", description = "Caller does not have the ADMIN role")
    })
    public ResponseEntity<String> deleteSchedule(@PathVariable Integer id){
        scheduleService.deleteSchedule(id);
        return ResponseEntity
                .ok("Deleted Successfully");
    }
}
