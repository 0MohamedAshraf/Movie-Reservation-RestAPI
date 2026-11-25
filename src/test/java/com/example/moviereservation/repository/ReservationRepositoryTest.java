package com.example.moviereservation.repository;

import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.ReservationStatus;
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
@DisplayName("ReservationRepository (H2)")
class ReservationRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private User firstUser;
    private User secondUser;

    @BeforeEach
    void seed() {
        firstUser = userRepository.save(TestData.user(null, "first@example.com"));
        secondUser = userRepository.save(TestData.user(null, "second@example.com"));
        Movie movie = movieRepository.save(TestData.newMovie());
        Theater theater = theaterRepository.save(TestData.newTheater());
        LocalDate date = LocalDate.of(2026, 3, 1);
        Schedule schedule = scheduleRepository.save(TestData.schedule(null, date.atTime(18, 0),
                date.atTime(20, 0), date, 10.0, movie, theater));

        reservationRepository.saveAll(List.of(
                TestData.reservation(null, ReservationStatus.PENDING, firstUser, schedule),
                TestData.reservation(null, ReservationStatus.CONFIRMED, firstUser, schedule),
                TestData.reservation(null, ReservationStatus.CANCELLED, firstUser, schedule),
                TestData.reservation(null, ReservationStatus.EXPIRED, firstUser, schedule),
                TestData.reservation(null, ReservationStatus.PENDING, secondUser, schedule)
        ));
    }

    @Test
    @DisplayName("findByUserId only returns PENDING and CONFIRMED reservations of that user")
    void findByUserId() {
        List<Reservation> reservations = reservationRepository.findActiveByUserId(firstUser.getId());

        assertThat(reservations)
                .extracting(Reservation::getStatus)
                .containsExactlyInAnyOrder(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("findByUserId does not leak reservations from other users")
    void findByUserIdIsScopedToTheUser() {
        assertThat(reservationRepository.findActiveByUserId(secondUser.getId())).hasSize(1);
    }
}
