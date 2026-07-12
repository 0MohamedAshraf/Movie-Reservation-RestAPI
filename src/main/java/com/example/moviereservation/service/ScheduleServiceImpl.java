package com.example.moviereservation.service;

import com.example.moviereservation.dto.ScheduleDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
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
    public ScheduleServiceImpl(
            ScheduleRepository scheduleRepository,
            TheaterRepository theaterRepository,
            MovieRepository movieRepository) {
        this.scheduleRepository = scheduleRepository;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
    }

    private ScheduleDto entityToDto(Schedule schedule){
        return new ScheduleDto(
                schedule.getId(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getDate(),
                schedule.getPrice(),
                schedule.getMovie().getId(),
                schedule.getTheater().getId()
        );
    }

    private Schedule dtoToEntity(ScheduleDto scheduleDto){
        Movie movie = movieRepository.findById(scheduleDto.getMovieId()).orElseThrow();
        Theater theater = theaterRepository.findById(scheduleDto.getTheaterId()).orElseThrow();

        return new Schedule(
                scheduleDto.getId(),
                scheduleDto.getStartTime(),
                scheduleDto.getEndTime(),
                scheduleDto.getDate(),
                scheduleDto.getPrice(),
                movie,theater

        );
    }
    public void validateSchedule(ScheduleDto schedule){
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
    public List<ScheduleDto> getAll() {
        return scheduleRepository.findAll()
                .stream()
                .map(this::entityToDto).toList();
    }

    @Override
    public ScheduleDto getScheduleById(String id) {

        return scheduleRepository.findById(id).map(this::entityToDto).orElseThrow();
    }

    @Override
    public ScheduleDto addSchedule(ScheduleDto schedule) {


        validateSchedule(schedule);

        Schedule theSchedule = scheduleRepository.save(dtoToEntity(schedule));
        return entityToDto(theSchedule);
    }

    @Override
    public ScheduleDto updateMovie(String scheduleId, Movie newMovie) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setMovie(newMovie);
        scheduleRepository.save(schedule);

        return entityToDto(schedule);
    }

    @Override
    public ScheduleDto updatePrice(String scheduleId, Double newPrice) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setPrice(newPrice);
        scheduleRepository.save(schedule);

        return entityToDto(schedule);
    }

    @Override
    public ScheduleDto updateTheater(String scheduleId, Theater newTheater) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setTheater(newTheater);
        scheduleRepository.save(schedule);

        return entityToDto(schedule);
    }

    @Override
    public ScheduleDto changeTime(String scheduleId, LocalDateTime startTime, LocalDateTime endTime) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        scheduleRepository.save(schedule);

        return entityToDto(schedule);
    }

    @Override
    public Movie showMovie(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        return schedule.getMovie();
    }
}
