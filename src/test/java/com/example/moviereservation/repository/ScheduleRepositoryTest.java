package com.example.moviereservation.repository;

import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = JpaTestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@DisplayName("ScheduleRepository (H2)")
class ScheduleRepositoryTest {

    private static final LocalDate DAY_ONE = LocalDate.of(2026, 1, 15);
    private static final LocalDate DAY_TWO = LocalDate.of(2026, 1, 16);

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    private Schedule dayTwoSchedule;

    @BeforeEach
    void seed() {
        Movie movie = movieRepository.save(TestData.newMovie());
        Theater first = theaterRepository.save(TestData.theater(null, "First", "Cairo", 50));
        Theater second = theaterRepository.save(TestData.theater(null, "Second", "Cairo", 50));

        scheduleRepository.save(TestData.schedule(null, DAY_ONE.atTime(18, 0), DAY_ONE.atTime(20, 0),
                DAY_ONE, 10.0, movie, first));
        scheduleRepository.save(TestData.schedule(null, DAY_ONE.atTime(18, 0), DAY_ONE.atTime(20, 0),
                DAY_ONE, 10.0, movie, second));
        dayTwoSchedule = scheduleRepository.save(TestData.schedule(null, DAY_TWO.atTime(18, 0),
                DAY_TWO.atTime(20, 0), DAY_TWO, 12.0, movie, first));
    }

    @Test
    @DisplayName("getSchedulesByDate returns every schedule of the given date")
    void getSchedulesByDate() {
        assertThat(scheduleRepository.getSchedulesByDate(DAY_ONE)).hasSize(2);
    }

    @Test
    @DisplayName("getSchedulesByDateAfter excludes the given date")
    void getSchedulesByDateAfter() {
        assertThat(scheduleRepository.getSchedulesByDateAfter(DAY_ONE))
                .extracting(Schedule::getId)
                .containsExactly(dayTwoSchedule.getId());
    }

    @Test
    @DisplayName("getScheduleByDate returns the schedule for the given date")
    void getScheduleByDate() {
        assertThat(scheduleRepository.getScheduleByDate(DAY_TWO).getId())
                .isEqualTo(dayTwoSchedule.getId());
    }
}
