package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.TheaterRequestDto;
import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.dto.response.TheaterScheduleDto;

import java.util.List;


public interface TheaterService {

    List<TheaterResponseDto> getAll();

    TheaterResponseDto getTheaterById(Integer id);

    TheaterResponseDto getTheaterByName(String name);

    TheaterResponseDto addTheater(TheaterRequestDto theater);

    List<TheaterResponseDto> filterTheatersByCity(String city);

    List<TheaterScheduleDto> getTheaterSchedules(Integer theaterId);



}
