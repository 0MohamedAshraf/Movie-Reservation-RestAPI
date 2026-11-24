package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.ReservationRequestDto;
import com.example.moviereservation.dto.request.ScheduleRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.dto.response.ScheduleResponseDto;
import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.Seat;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.ReservationStatus;
import com.example.moviereservation.enums.SeatClass;
import com.example.moviereservation.repository.ReservationRepository;
import com.example.moviereservation.repository.ReservationSeatRepository;
import com.example.moviereservation.repository.ScheduleSeatRepository;
import com.example.moviereservation.repository.SeatRepository;
import com.example.moviereservation.repository.TheaterRepository;
import com.example.moviereservation.repository.UserRepository;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end flow over the real services, repositories and the in-memory H2
 * database: create movie + theater + schedule, book seats, then cancel.
 */
@SpringBootTest
@Transactional
@DisplayName("Reservation flow (H2)")
class ReservationFlowIntegrationTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private TheaterService theaterService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ScheduleSeatRepository scheduleSeatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationSeatRepository reservationSeatRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("reserves seats, prices them, then releases them on cancellation")
    void reserveAndCancel() {
        TheaterResponseDto theaterDto = theaterService.addTheater(
                TestData.theaterRequest("Flow Cinema", "Cairo", 2));
        Theater theater = theaterRepository.findById(theaterDto.getId()).orElseThrow();
        Seat firstSeat = seatRepository.save(TestData.seat(null, 'A', 1, SeatClass.STANDARD, theater));
        Seat secondSeat = seatRepository.save(TestData.seat(null, 'A', 2, SeatClass.STANDARD, theater));

        MovieResponseDto movie = movieService.addMovie(TestData.movieRequest("Flow Movie", 4.5f, 120f));

        LocalDate date = LocalDate.now().plusDays(1);
        ScheduleRequestDto scheduleRequest = TestData.scheduleRequest(
                date.atTime(18, 0), date.atTime(20, 0), date, 10.0,
                movie.getId(), theaterDto.getId());
        ScheduleResponseDto schedule = scheduleService.addSchedule(scheduleRequest);

        List<ScheduleSeat> seats = scheduleSeatRepository.getByScheduleId(schedule.getId());
        assertThat(seats).hasSize(2);

        User user = userRepository.save(TestData.user(null, "flow@example.com"));
        List<Integer> seatIds = seats.stream().map(ScheduleSeat::getId).toList();
        ReservationRequestDto request = TestData.reservationRequest(
                user.getId(), schedule.getId(), seatIds);

        ReservationResponseDto reservation = reservationService.reserveSchedule(request);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(reservation.getTotalPrice()).isEqualTo(20f);
        assertThat(reservation.getReservedSeatsIds())
                .containsExactlyInAnyOrderElementsOf(seatIds);
        assertThat(scheduleSeatRepository.getByScheduleId(schedule.getId()))
                .allMatch(seat -> !seat.getAvailable());

        reservationService.cancelReservation(reservation.getId());

        Reservation cancelled = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(cancelled.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(scheduleSeatRepository.getByScheduleId(schedule.getId()))
                .allMatch(ScheduleSeat::getAvailable);
        assertThat(reservationSeatRepository.existsByReservation_Id(reservation.getId())).isFalse();
    }
}
