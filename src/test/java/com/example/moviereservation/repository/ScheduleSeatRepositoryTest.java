package com.example.moviereservation.repository;

import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.Seat;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.enums.SeatClass;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = JpaTestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@DisplayName("ScheduleSeatRepository (H2)")
class ScheduleSeatRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleSeatRepository scheduleSeatRepository;

    private Schedule schedule;
    private ScheduleSeat availableSeat;
    private ScheduleSeat reservedSeat;

    @BeforeEach
    void seed() {
        Movie movie = movieRepository.save(TestData.newMovie());
        Theater theater = theaterRepository.save(TestData.theater(null, "Theater", "Cairo", 2));
        Seat first = seatRepository.save(TestData.seat(null, 'A', 1, SeatClass.STANDARD, theater));
        Seat second = seatRepository.save(TestData.seat(null, 'A', 2, SeatClass.VIP, theater));
        LocalDate date = LocalDate.of(2026, 2, 1);
        schedule = scheduleRepository.save(TestData.schedule(null, date.atTime(18, 0),
                date.atTime(20, 0), date, 10.0, movie, theater));
        availableSeat = scheduleSeatRepository.save(TestData.availableSeat(null, schedule, first));
        reservedSeat = scheduleSeatRepository.save(TestData.unavailableSeat(null, schedule, second));
    }

    @Test
    @DisplayName("getByScheduleId returns every seat of the schedule")
    void getByScheduleId() {
        assertThat(scheduleSeatRepository.getByScheduleId(schedule.getId())).hasSize(2);
    }

    @Test
    @DisplayName("existsByScheduleId reflects whether seats were generated")
    void existsByScheduleId() {
        assertThat(scheduleSeatRepository.existsByScheduleId(schedule.getId())).isTrue();
        assertThat(scheduleSeatRepository.existsByScheduleId(999)).isFalse();
    }

    @Test
    @DisplayName("getScheduleSeatsForUpdate returns the requested seats")
    void getScheduleSeatsForUpdate() {
        List<ScheduleSeat> seats = scheduleSeatRepository.getScheduleSeatsForUpdate(
                List.of(availableSeat.getId(), reservedSeat.getId()));

        assertThat(seats).hasSize(2);
        assertThat(seats).extracting(ScheduleSeat::getId)
                .containsExactlyInAnyOrder(availableSeat.getId(), reservedSeat.getId());
    }
}
