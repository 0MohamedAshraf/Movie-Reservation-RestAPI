package com.example.moviereservation.service;

import com.example.moviereservation.dto.response.AdminMovieResponseDto;
import com.example.moviereservation.dto.response.AdminReservationResponseDto;
import com.example.moviereservation.dto.response.AdminScheduleResponseDto;
import com.example.moviereservation.dto.response.AdminScheduleSeatResponseDto;
import com.example.moviereservation.dto.response.AdminSummaryResponseDto;
import com.example.moviereservation.dto.response.AdminTheaterResponseDto;
import com.example.moviereservation.dto.response.UserResponseDto;

import java.util.List;

public interface AdminService {

    List<UserResponseDto> getUsers();

    List<AdminTheaterResponseDto> getTheaters();

    List<AdminMovieResponseDto> getMovies();

    List<AdminScheduleResponseDto> getSchedules();

    List<AdminScheduleSeatResponseDto> getScheduleSeats();

    List<AdminReservationResponseDto> getReservations();

    AdminSummaryResponseDto getSummary();
}
