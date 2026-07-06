package com.example.moviereservation.service;

import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService{


    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public void validateSchedule(Schedule schedule){
        if(schedule.getEndTime().isBefore(schedule.getStartTime())){
            throw new RuntimeException("End Time Must be After start time");
        }
        List<Schedule> schedules = getAll();
        for(Schedule theSchedule : schedules){

            // guarantee that there is no schedule overlap
            if(     schedule.getStartTime().isBefore(theSchedule.getEndTime())
                    && schedule.getEndTime().isAfter(theSchedule.getStartTime())
                    && schedule.getTheater().equals(theSchedule.getTheater())   ){
                throw new RuntimeException("Schedule Overlapping");
            }
        }
    }
    @Override
    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Schedule getScheduleById(String id) {
        return scheduleRepository.findById(id).orElseThrow();
    }

    @Override
    public Schedule addSchedule(Schedule schedule) {

        try {
            validateSchedule(schedule);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule updateMovie(String scheduleId, Movie newMovie) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setMovie(newMovie);
        scheduleRepository.save(schedule);

        return schedule;
    }

    @Override
    public Schedule updatePrice(String scheduleId, Double newPrice) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setPrice(newPrice);
        scheduleRepository.save(schedule);

        return schedule;
    }

    @Override
    public Schedule updateTheater(String scheduleId, Theater newTheater) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setTheater(newTheater);
        scheduleRepository.save(schedule);

        return schedule;
    }

    @Override
    public Schedule changeTime(String scheduleId, LocalDateTime startTime, LocalDateTime endTime) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        scheduleRepository.save(schedule);

        return schedule;
    }

    @Override
    public Movie showMovie(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        return schedule.getMovie();
    }
}
