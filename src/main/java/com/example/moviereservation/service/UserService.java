package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.UserRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto getById(Integer id);

    UserResponseDto update(Integer userId,UserRequestDto request);

    void deleteUser(Integer userId);

    List<ReservationResponseDto> getReservations(Integer userId);

}
