package com.example.moviereservation.service;

import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;

import java.util.List;

public interface ScheduleSeatService {

    List<ScheduleSeat> getAllSeats();

    List<ScheduleSeat> getAllByIds(List<Integer> ids);

    ScheduleSeat getById(Integer id);

    ScheduleSeat markAvailable(Integer id);

    ScheduleSeat markUnavailable(Integer id);

    ScheduleSeat setAvailability(Integer id, Boolean availability);

    List<ScheduleSeat> getByScheduleId(Integer scheduleId);

    List<ScheduleSeat> getAvailableSeats(Integer scheduleId);

    boolean isAvailable(Integer seatId);

    void generateScheduleSeats(Schedule schedule);

    boolean seatsExists(Integer scheduleId);

    List<ScheduleSeat> saveAll(Iterable<ScheduleSeat> seats);
}
