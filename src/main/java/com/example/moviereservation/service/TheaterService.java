package com.example.moviereservation.service;

import com.example.moviereservation.entity.Theater;

import java.util.List;


public interface TheaterService {

    List<Theater> getAll();

    Theater getTheaterById(String id);

    Theater getTheaterByName(String name);

    Theater addTheater(Theater theater);

    List<Theater> filterTheatersByCity(String city);




}
