package com.example.moviereservation.service;

import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.dto.response.ScheduleResponseDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.mapper.MovieMapper;
import com.example.moviereservation.mapper.ScheduleMapper;
import com.example.moviereservation.repository.MovieRepository;
import com.example.moviereservation.repository.ScheduleRepository;
import com.example.moviereservation.repository.TheaterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService{


    private final ScheduleRepository scheduleRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScheduleMapper mapper;
    private final MovieMapper movieMapper;
    public ScheduleServiceImpl(
            ScheduleRepository scheduleRepository,
            TheaterRepository theaterRepository,
            MovieRepository movieRepository,
            ScheduleMapper mapper, MovieMapper movieMapper) {
        this.scheduleRepository = scheduleRepository;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.mapper = mapper;
        this.movieMapper = movieMapper;
    }

    public void validateSchedule(ScheduleResponseDto schedule){
        if(schedule.getEndTime().isBefore(schedule.getStartTime())){
            throw new RuntimeException("End Time Must be After start time");
        }
        List<Schedule> schedules = scheduleRepository.findAll();
        for(Schedule theSchedule : schedules){

            // guarantee that there is no schedule overlap
            if(     schedule.getStartTime().isBefore(theSchedule.getEndTime())
                    && schedule.getEndTime().isAfter(theSchedule.getStartTime())
                    && schedule.getTheaterId().equals(theSchedule.getTheater().getId())   ){
                throw new RuntimeException("Schedule Overlapping");
            }
        }
    }
    @Override
    public List<ScheduleResponseDto> getAll() {
        return scheduleRepository.findAll()
                .stream()
                .map(mapper::entityToDto).toList();
    }

    @Override
    public ScheduleResponseDto getScheduleById(Integer id) {

        return scheduleRepository.findById(id).map(mapper::entityToDto).orElseThrow();
    }

    @Override
    public ScheduleResponseDto addSchedule(ScheduleResponseDto schedule) {


        validateSchedule(schedule);

        Movie movie = movieRepository.findById(schedule.getMovieId()).orElseThrow();
        Theater theater = theaterRepository.findById(schedule.getTheaterId()).orElseThrow();

        Schedule theSchedule = scheduleRepository.save(mapper.dtoToEntity(schedule,movie,theater));
        return mapper.entityToDto(theSchedule);
    }

    @Override
    public ScheduleResponseDto updateMovie(Integer scheduleId, Movie newMovie) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setMovie(newMovie);
        scheduleRepository.save(schedule);

        return mapper.entityToDto(schedule);
    }

    @Override
    public ScheduleResponseDto updatePrice(Integer scheduleId, Double newPrice) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setPrice(newPrice);
        scheduleRepository.save(schedule);

        return mapper.entityToDto(schedule);
    }

    @Override
    public ScheduleResponseDto updateTheater(Integer scheduleId, Theater newTheater) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setTheater(newTheater);
        scheduleRepository.save(schedule);

        return mapper.entityToDto(schedule);
    }

    @Override
    public ScheduleResponseDto changeTime(Integer scheduleId, LocalDateTime startTime, LocalDateTime endTime) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        scheduleRepository.save(schedule);

        return mapper.entityToDto(schedule);
    }

    @Override
    public MovieResponseDto showMovie(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        return movieMapper.entityToDto(schedule.getMovie());
    }
}
