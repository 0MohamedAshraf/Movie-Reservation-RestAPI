package com.example.moviereservation.controller;

import com.example.moviereservation.config.OpenApiConfig;
import com.example.moviereservation.dto.request.UserRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.dto.response.UserResponseDto;
import com.example.moviereservation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
@Tag(name = "Users", description = "User profiles and their reservations (JWT required)")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class UserController {

    private final UserService service;

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> getById(@PathVariable
                                                    Integer id){
        return ResponseEntity
                .ok(service.getById(id));
    }

    @GetMapping("/{userId}/reservations")
    @Operation(summary = "Get a user's reservations", description = "Lists all reservations belonging to the given user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservations returned"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<ReservationResponseDto>> getReservations(@PathVariable
                                                                         Integer userId){
        return ResponseEntity
                .ok(service.getReservations(userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Updates the user's profile, role included.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable
                                                       Integer id,@Valid @RequestBody UserRequestDto newUser){
        return ResponseEntity
                .ok(service.update(id,newUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<String> deleteUser(@PathVariable
                                              Integer id){
        service.deleteUser(id);

        return ResponseEntity
                .ok("Deleted Successfully");
    }
}
