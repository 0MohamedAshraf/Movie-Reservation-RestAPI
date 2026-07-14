package com.example.moviereservation.service;

import com.example.moviereservation.dto.response.TheaterResponseDto;

import java.util.List;


public interface TheaterService {

    List<TheaterResponseDto> getAll();

    TheaterResponseDto getTheaterById(Integer id);

    TheaterResponseDto getTheaterByName(String name);

    TheaterResponseDto addTheater(TheaterResponseDto theater);

    List<TheaterResponseDto> filterTheatersByCity(String city);




}
