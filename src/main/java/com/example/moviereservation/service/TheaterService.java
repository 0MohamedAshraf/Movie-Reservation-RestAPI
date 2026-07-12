package com.example.moviereservation.service;

import com.example.moviereservation.dto.TheaterDto;
import com.example.moviereservation.entity.Theater;

import java.util.List;


public interface TheaterService {

    List<TheaterDto> getAll();

    TheaterDto getTheaterById(String id);

    TheaterDto getTheaterByName(String name);

    TheaterDto addTheater(TheaterDto theater);

    List<TheaterDto> filterTheatersByCity(String city);




}
