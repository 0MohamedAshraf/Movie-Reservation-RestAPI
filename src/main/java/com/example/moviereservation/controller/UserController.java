package com.example.moviereservation.controller;

import com.example.moviereservation.dto.request.UserRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.dto.response.UserResponseDto;
import com.example.moviereservation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService service;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable
                                                   Integer id){
        return ResponseEntity
                .ok(service.getById(id));
    }

    @GetMapping("/{userId}/reservations")
    public ResponseEntity<List<ReservationResponseDto>> getReservations(@PathVariable
                                                                        Integer userId){
        return ResponseEntity
                .ok(service.getReservations(userId));
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable
                                                      Integer id,@Valid @RequestBody UserRequestDto newUser){
        return ResponseEntity
                .ok(service.update(id,newUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable
                                             Integer id){
        service.deleteUser(id);

        return ResponseEntity
                .ok("Deleted Successfully");
    }
}
