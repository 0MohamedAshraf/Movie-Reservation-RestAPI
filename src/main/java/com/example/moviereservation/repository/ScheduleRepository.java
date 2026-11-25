package com.example.moviereservation.repository;

import com.example.moviereservation.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Integer> {

    Schedule getScheduleByDate(LocalDate date);

    List<Schedule> getSchedulesByDate(LocalDate date);

    List<Schedule> getSchedulesByDateAfter(LocalDate dateAfter);

    List<Schedule> getScheduleByDateAndTheaterId(LocalDate date, Integer theaterId);

    @Query("""
            SELECT s FROM Schedule s
            JOIN FETCH s.movie
            JOIN FETCH s.theater
            ORDER BY s.date DESC, s.startTime DESC
            """)
    List<Schedule> findAllForAdmin();
}
