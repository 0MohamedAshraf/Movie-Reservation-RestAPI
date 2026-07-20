package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.ScheduleRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.dto.response.ScheduleResponseDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.exceptions.InvalidEntityException;
import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.exceptions.ScheduleOverlapException;
import com.example.moviereservation.mapper.MovieMapper;
import com.example.moviereservation.mapper.ScheduleMapper;
import com.example.moviereservation.repository.MovieRepository;
import com.example.moviereservation.repository.ScheduleRepository;
import com.example.moviereservation.repository.TheaterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
public class ScheduleServiceImpl implements ScheduleService{


    private final ScheduleSeatService scheduleSeatService;
    private final ScheduleRepository scheduleRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScheduleMapper mapper;
    private final MovieMapper movieMapper;

    public void validateSchedule(ScheduleRequestDto schedule){
        if(schedule.getEndTime().isBefore(schedule.getStartTime())){
            throw new InvalidEntityException("End Time Must be After start time");
        }
        if(schedule.getPrice() <= 0)
            throw new InvalidEntityException("Scheudle Price can't be less than or equal 0");

        List<Schedule> schedules = scheduleRepository.getSchedulesByDate(schedule.getDate());
        for(Schedule theSchedule : schedules){

            // guarantee that there is no schedule overlap
            if(     schedule.getStartTime().isBefore(theSchedule.getEndTime())
                    && schedule.getEndTime().isAfter(theSchedule.getStartTime())
                    && schedule.getTheaterId().equals(theSchedule.getTheater().getId())   ){
                throw new ScheduleOverlapException("Schedule Overlapping");
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

        return scheduleRepository.findById(id).map(mapper::entityToDto).orElseThrow(
                () -> new ResourceNotFoundException("Schedule With id: " + id +" Not Found")
        );
    }

    @Override
    public ScheduleResponseDto addSchedule(ScheduleRequestDto schedule) {


        validateSchedule(schedule);

        Movie movie = movieRepository.findById(schedule.getMovieId()).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't Find Movie with id: " + schedule.getMovieId())
        );
        Theater theater = theaterRepository.findById(schedule.getTheaterId()).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't Find Theater with id: " + schedule.getTheaterId())
        );

        Schedule theSchedule = scheduleRepository.save(mapper.dtoToEntity(schedule,movie,theater));

        if(scheduleSeatService.seatsExists(theSchedule.getId()))
            throw new IllegalArgumentException("Schedule Seats already generated");

        scheduleSeatService.generateScheduleSeats(theSchedule);

        return mapper.entityToDto(theSchedule);
    }

    @Override
    public ScheduleResponseDto updateSchedule(ScheduleRequestDto newSchedule, Integer id) {
        validateSchedule(newSchedule);
        getScheduleById(id);
        Movie movie = movieRepository.findById(newSchedule.getMovieId()).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't Find Movie with id: " + newSchedule.getMovieId())
        );
        Theater theater = theaterRepository.findById(newSchedule.getTheaterId()).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't Find Theater with id: " + newSchedule.getTheaterId())
        );
        Schedule theSchedule = mapper.dtoToEntity(newSchedule,movie,theater);
        theSchedule.setId(id);
        return mapper.entityToDto(scheduleRepository.save(theSchedule));
    }

    @Override
    public ScheduleResponseDto updateMovie(Integer scheduleId, Movie newMovie) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ResourceNotFoundException("Schedule With id: " + scheduleId + "Not Found")
        );
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
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ResourceNotFoundException("No Schedule with id: " + scheduleId +" Found")
        );
        return movieMapper.entityToDto(schedule.getMovie());
    }

    @Override
    public void deleteSchedule(Integer id) {
        if(getScheduleById(id) != null)
            scheduleRepository.deleteById(id);
    }

    @Override
    public List<ScheduleResponseDto> getTodaySchedules() {
        return scheduleRepository.getSchedulesByDate(LocalDate.now())
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    public List<ScheduleResponseDto> getUpcomingSchedules() {
        return scheduleRepository.getSchedulesByDateAfter(LocalDate.now())
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    public ScheduleResponseDto getScheduleByDate(LocalDate date) {
        return mapper.entityToDto(scheduleRepository.getScheduleByDate(date));
    }
}
