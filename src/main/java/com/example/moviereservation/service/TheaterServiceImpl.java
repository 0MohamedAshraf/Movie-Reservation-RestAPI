package com.example.moviereservation.service;

import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheaterServiceImpl implements TheaterService{
    private TheaterRepository theaterRepository;

    @Autowired
    public TheaterServiceImpl(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    @Override
    public List<Theater> getAll() {
        return theaterRepository.findAll();
    }

    @Override
    public Theater getTheaterById(String id) {
        Theater theater = theaterRepository.findById(id).orElse(null);
        if(theater != null) return theater;
        else throw new RuntimeException("Couldn't find theater with Id: " + id);

    }

    @Override
    public Theater getTheaterByName(String name) {
        Theater theater = theaterRepository.findByName(name);
        if(theater != null) return theater;
        else throw new RuntimeException("Couldn't find theater with name: " + name);
    }

    @Override
    public Theater addTheater(Theater theater) {
        try {
            return theaterRepository.save(theater);
        }catch (IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }catch (Exception e){
            System.out.println("Unknown Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Theater> filterTheatersByCity(String city) {
        return List.of();
    }
}
