package com.example.moviereservation.repository;

import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.entity.ReservationSeat;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.Seat;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.ReservationStatus;
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
@DisplayName("ReservationSeatRepository (H2)")
class ReservationSeatRepositoryTest {

    @Autowired
    private UserRepository userRepository;

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

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationSeatRepository reservationSeatRepository;

    private Reservation reservation;
    private ScheduleSeat firstSeat;
    private ScheduleSeat secondSeat;

    @BeforeEach
    void seed() {
        User user = userRepository.save(TestData.user(null, "jane@example.com"));
        Movie movie = movieRepository.save(TestData.newMovie());
        Theater theater = theaterRepository.save(TestData.theater(null, "Theater", "Cairo", 2));
        Seat seatOne = seatRepository.save(TestData.seat(null, 'A', 1, SeatClass.STANDARD, theater));
        Seat seatTwo = seatRepository.save(TestData.seat(null, 'A', 2, SeatClass.PREMIUM, theater));
        LocalDate date = LocalDate.of(2026, 4, 1);
        Schedule schedule = scheduleRepository.save(TestData.schedule(null, date.atTime(18, 0),
                date.atTime(20, 0), date, 10.0, movie, theater));
        firstSeat = scheduleSeatRepository.save(TestData.availableSeat(null, schedule, seatOne));
        secondSeat = scheduleSeatRepository.save(TestData.availableSeat(null, schedule, seatTwo));
        reservation = reservationRepository.save(TestData.reservation(null, ReservationStatus.PENDING,
                user, schedule));
        reservationSeatRepository.saveAll(List.of(
                new ReservationSeat(null, reservation, firstSeat),
                new ReservationSeat(null, reservation, secondSeat)
        ));
        reservationSeatRepository.flush();
    }

    @Test
    @DisplayName("getReservedSeatsIds returns the ids of the booked schedule seats")
    void getReservedSeatsIds() {
        assertThat(reservationSeatRepository.getReservedSeatsIds(reservation.getId()))
                .containsExactlyInAnyOrder(firstSeat.getId(), secondSeat.getId());
    }

    @Test
    @DisplayName("getReservedSeats returns the booked schedule seats")
    void getReservedSeats() {
        assertThat(reservationSeatRepository.getReservedSeats(reservation.getId()))
                .extracting(ScheduleSeat::getId)
                .containsExactlyInAnyOrder(firstSeat.getId(), secondSeat.getId());
    }

    @Test
    @DisplayName("existsByReservation_Id reports whether the reservation has seats")
    void existsByReservation() {
        assertThat(reservationSeatRepository.existsByReservation_Id(reservation.getId())).isTrue();
    }

    @Test
    @DisplayName("existsByReservation_IdAndScheduleSeat_Id checks a single seat")
    void existsByReservationAndSeat() {
        assertThat(reservationSeatRepository.existsByReservation_IdAndScheduleSeat_Id(
                reservation.getId(), firstSeat.getId())).isTrue();
    }

    @Test
    @DisplayName("deleteAllByReservationId removes every join row of the reservation")
    void deleteAllByReservationId() {
        reservationSeatRepository.deleteAllByReservationId(reservation.getId());
        reservationSeatRepository.flush();

        assertThat(reservationSeatRepository.getReservedSeatsIds(reservation.getId())).isEmpty();
        assertThat(reservationSeatRepository.existsByReservation_Id(reservation.getId())).isFalse();
    }
}
