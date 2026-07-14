package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.TheaterRequestDto;
import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.dto.response.TheaterScheduleDto;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.exceptions.theater.TheaterNotFoundException;
import com.example.moviereservation.mapper.TheaterMapper;
import com.example.moviereservation.mapper.TheaterScheduleMapper;
import com.example.moviereservation.repository.TheaterRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class TheaterServiceImpl implements TheaterService{

    private final TheaterRepository theaterRepository;
    private final TheaterMapper mapper;
    private final TheaterScheduleMapper theaterScheduleMapper;



    @Override
    public List<TheaterResponseDto> getAll() {
        return theaterRepository.findAll()
                .stream()
                .map(mapper::entityToDto).toList();
    }

    @Override
    public TheaterResponseDto getTheaterById(Integer id) {
        Theater theater = theaterRepository.findById(id).orElse(null);
        if(theater != null) return mapper.entityToDto(theater);
        else throw new RuntimeException("Couldn't find theater with Id: " + id);

    }

    @Override
    public TheaterResponseDto getTheaterByName(String name) {
        Theater theater = theaterRepository.findByName(name);
        if(theater != null) return mapper.entityToDto(theater);
        else throw new RuntimeException("Couldn't find theater with name: " + name);
    }

    @Override
    public TheaterResponseDto addTheater(TheaterRequestDto theater) {
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
    public List<TheaterResponseDto> filterTheatersByCity(String city) {
        return List.of();
    }

    @Override
    public List<TheaterScheduleDto> getTheaterSchedules(Integer theaterId) {
        Theater theTheater = theaterRepository.findById(theaterId).orElseThrow(
                () -> new TheaterNotFoundException("Theater With Id: " + theaterId +" Not Found")
        );
        if(theTheater.getScheduleList() == null)
            return Collections.emptyList();
        return theTheater.getScheduleList().stream().map(theaterScheduleMapper::scheduleToDto).toList();
    }
}
