package com.example.moviereservation.service;

import com.example.moviereservation.dto.TheaterDto;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.mapper.TheaterMapper;
import com.example.moviereservation.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheaterServiceImpl implements TheaterService{
    private final TheaterRepository theaterRepository;
    private final TheaterMapper mapper;

    @Autowired
    public TheaterServiceImpl(TheaterRepository theaterRepository, TheaterMapper mapper) {
        this.theaterRepository = theaterRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TheaterDto> getAll() {
        return theaterRepository.findAll()
                .stream()
                .map(mapper::entityToDto).toList();
    }

    @Override
    public TheaterDto getTheaterById(String id) {
        Theater theater = theaterRepository.findById(id).orElse(null);
        if(theater != null) return mapper.entityToDto(theater);
        else throw new RuntimeException("Couldn't find theater with Id: " + id);

    }

    @Override
    public TheaterDto getTheaterByName(String name) {
        Theater theater = theaterRepository.findByName(name);
        if(theater != null) return mapper.entityToDto(theater);
        else throw new RuntimeException("Couldn't find theater with name: " + name);
    }

    @Override
    public TheaterDto addTheater(TheaterDto theater) {
        try {
             Theater theTheater = theaterRepository.save(mapper.dtoToEntity(theater));
             return mapper.entityToDto(theTheater);
        }catch (IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }catch (Exception e){
            System.out.println("Unknown Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<TheaterDto> filterTheatersByCity(String city) {
        return List.of();
    }
}
