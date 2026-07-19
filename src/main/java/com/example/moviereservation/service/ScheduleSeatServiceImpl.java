package com.example.moviereservation.service;

import com.example.moviereservation.dto.response.ScheduleSeatResponseDto;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.Seat;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.mapper.ScheduleSeatMapper;
import com.example.moviereservation.repository.ScheduleSeatRepository;
import com.example.moviereservation.repository.SeatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleSeatServiceImpl implements ScheduleSeatService {
    private final ScheduleSeatRepository repository;
    private final SeatRepository seatRepository;
    private final ScheduleSeatMapper mapper;

    @Override
    public List<ScheduleSeatResponseDto> getAllSeats() {
        return repository.findAll()
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    public ScheduleSeatResponseDto getById(Integer id) {
        ScheduleSeat theSeat = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("There is no Schedule Seat with id: " + id)
        );

        return mapper.entityToDto(theSeat);
    }

    @Override
    public ScheduleSeatResponseDto markAvailable(Integer id) {
        ScheduleSeat theSeat = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("There is no Schedule Seat with id: " + id)
        );
        theSeat.setAvailable(true);
        repository.save(theSeat);
        return mapper.entityToDto(theSeat);
    }

    @Override
    public ScheduleSeatResponseDto markUnavailable(Integer id) {
        ScheduleSeat theSeat = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("There is no Schedule Seat with id: " + id)
        );
        theSeat.setAvailable(false);
        repository.save(theSeat);
        return mapper.entityToDto(theSeat);
    }

    @Override
    public ScheduleSeatResponseDto setAvailability(Integer id, Boolean availability) {
        ScheduleSeat theSeat = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("There is no Schedule Seat with id: " + id)
        );
        theSeat.setAvailable(availability);
        repository.save(theSeat);
        return mapper.entityToDto(theSeat);
    }

    @Override
    public List<ScheduleSeatResponseDto> getByScheduleId(Integer scheduleId) {

        return repository.getByScheduleId(scheduleId)
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    public List<ScheduleSeatResponseDto> getAvailableSeats(Integer scheduleId) {
        return repository.getByScheduleId(scheduleId)
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    public boolean isAvailable(Integer seatId) {
        ScheduleSeat theSeat = repository.findById(seatId).orElseThrow(
                () -> new ResourceNotFoundException("There is no Schedule Seat with id: " + seatId)
        );
        return theSeat.getAvailable();
    }

    @Override
    public List<ScheduleSeatResponseDto> generateScheduleSeats(Schedule schedule) {
        Theater theater = schedule.getTheater();
        List<Seat> seats = seatRepository.getByTheaterId(theater.getId());
        List<ScheduleSeat> scheduleSeats = new ArrayList<>();

        for(int i = 0;i < theater.getTotalSeats();i++){
            scheduleSeats.add(new ScheduleSeat(null,true,schedule,seats.get(i)));
        }
        List<ScheduleSeat> savedSeats =
                repository.saveAll(scheduleSeats);

        return savedSeats
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }
}
