package com.example.moviereservation.service;

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
    public List<ScheduleSeat> getAllSeats() {
        return repository.findAll();
    }

    @Override
    public List<ScheduleSeat> getAllByIds(List<Integer> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public ScheduleSeat getById(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("There is no Schedule Seat with id: " + id)
        );
    }

    @Override
    public ScheduleSeat markAvailable(Integer id) {
        ScheduleSeat theSeat = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("There is no Schedule Seat with id: " + id)
        );
        theSeat.setAvailable(true);
        repository.save(theSeat);
        return theSeat;
    }

    @Override
    public ScheduleSeat markUnavailable(Integer id) {
        ScheduleSeat theSeat = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("There is no Schedule Seat with id: " + id)
        );
        theSeat.setAvailable(false);
        repository.save(theSeat);
        return theSeat;
    }

    @Override
    public ScheduleSeat setAvailability(Integer id, Boolean availability) {
        ScheduleSeat theSeat = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("There is no Schedule Seat with id: " + id)
        );
        theSeat.setAvailable(availability);
        repository.save(theSeat);
        return theSeat;
    }

    @Override
    public List<ScheduleSeat> getByScheduleId(Integer scheduleId) {

        return repository.getByScheduleId(scheduleId);
    }

    @Override
    public List<ScheduleSeat> getAvailableSeats(Integer scheduleId) {
        return repository.getByScheduleId(scheduleId);
    }

    @Override
    public boolean isAvailable(Integer seatId) {
        ScheduleSeat theSeat = repository.findById(seatId).orElseThrow(
                () -> new ResourceNotFoundException("There is no Schedule Seat with id: " + seatId)
        );
        return theSeat.getAvailable();
    }

    @Override
    public void generateScheduleSeats(Schedule schedule) {
        Theater theater = schedule.getTheater();
        List<Seat> seats = seatRepository.getByTheaterId(theater.getId());
        if (seats.isEmpty())
            throw new IllegalArgumentException("Theater Has no seats");
        List<ScheduleSeat> scheduleSeats = new ArrayList<>();

        for(int i = 0;i < theater.getTotalSeats();i++){
            scheduleSeats.add(new ScheduleSeat(null,true,schedule,seats.get(i)));
        }
        List<ScheduleSeat> savedSeats =
                repository.saveAll(scheduleSeats);

    }

    @Override
    public boolean seatsExists(Integer scheduleId) {
        return repository.existsByScheduleId(scheduleId);
    }

    @Override
    public List<ScheduleSeat> saveAll(Iterable<ScheduleSeat> seats) {
        return repository.saveAll(seats);
    }
}
