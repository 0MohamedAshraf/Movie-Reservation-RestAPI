package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.TheaterRequestDto;
import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.dto.response.TheaterScheduleDto;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.exceptions.EntityAlreadyExistsException;
import com.example.moviereservation.exceptions.InvalidEntityException;
import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.mapper.TheaterMapper;
import com.example.moviereservation.mapper.TheaterScheduleMapper;
import com.example.moviereservation.repository.TheaterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class TheaterServiceImpl implements TheaterService{

    private final TheaterRepository theaterRepository;
    private final TheaterMapper mapper;
    private final TheaterScheduleMapper theaterScheduleMapper;



    private void validateTheater(TheaterRequestDto theater){

        if(theater.getTotalSeats() <= 0)
            throw new InvalidEntityException("Theater should contain at least 1 seat");
    }
    @Override
    public List<TheaterResponseDto> getAll() {
        return theaterRepository.findAll()
                .stream()
                .map(mapper::entityToDto).toList();
    }

    @Override
    public TheaterResponseDto getTheaterById(Integer id) {
        Theater theater = theaterRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Theater with id: " + id + " Not Found")
        );
        return mapper.entityToDto(theater);
    }

    @Override
    public TheaterResponseDto getTheaterByName(String name) {
        Theater theater = theaterRepository.findByName(name);
        if(theater != null) return mapper.entityToDto(theater);
        else throw new ResourceNotFoundException("Couldn't find theater with name \"" + name + "\"");
    }

    @Override
    public TheaterResponseDto addTheater(TheaterRequestDto theater) {
        validateTheater(theater);
        if(theaterRepository.existsByNameAndCity(theater.getName(),theater.getCity()))
            throw new EntityAlreadyExistsException("There is a Theater with this name in the same city");
        Theater theTheater = theaterRepository.save(mapper.dtoToEntity(theater));
        return mapper.entityToDto(theTheater);
    }

    @Override
    public List<TheaterResponseDto> filterTheatersByCity(String city) {
        List<Theater> theaters = theaterRepository.findByCity(city);
        return theaters
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    public List<TheaterScheduleDto> getTheaterSchedules(Integer theaterId) {
        Theater theTheater = theaterRepository.findById(theaterId).orElseThrow(
                () -> new ResourceNotFoundException("Theater With Id: " + theaterId +" Not Found")
        );
        if(theTheater.getScheduleList() == null)
            return Collections.emptyList();
        return theTheater.getScheduleList().stream().map(theaterScheduleMapper::scheduleToDto).toList();
    }

    @Override
    public TheaterResponseDto updateTheater(TheaterRequestDto theater, Integer id) {
        validateTheater(theater);
        getTheaterById(id);
        Theater newTheater = mapper.dtoToEntity(theater);
        newTheater.setId(id);
            return mapper.entityToDto(theaterRepository.save(newTheater));
    }

    @Override
    public void deleteTheater(Integer theaterId) {
        if (getTheaterById(theaterId) != null)
            theaterRepository.deleteById(theaterId);
    }
}
