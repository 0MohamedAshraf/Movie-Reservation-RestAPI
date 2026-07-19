package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.ScheduleSeatRequestDto;
import com.example.moviereservation.dto.response.ScheduleSeatResponseDto;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;

import java.util.List;

public interface ScheduleSeatService {

    List<ScheduleSeatResponseDto> getAllSeats();

    ScheduleSeatResponseDto getById(Integer id);

    ScheduleSeatResponseDto markAvailable(Integer id);

    ScheduleSeatResponseDto markUnavailable(Integer id);

    ScheduleSeatResponseDto setAvailability(Integer id,Boolean availability);

    List<ScheduleSeatResponseDto> getByScheduleId(Integer scheduleId);

    List<ScheduleSeatResponseDto> getAvailableSeats(Integer scheduleId);

    boolean isAvailable(Integer seatId);

    List<ScheduleSeatResponseDto> generateScheduleSeats(Schedule schedule);
}
