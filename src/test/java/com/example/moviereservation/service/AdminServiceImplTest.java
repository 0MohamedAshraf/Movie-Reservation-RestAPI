package com.example.moviereservation.service;

import com.example.moviereservation.dto.response.AdminReservationResponseDto;
import com.example.moviereservation.dto.response.AdminScheduleSeatResponseDto;
import com.example.moviereservation.dto.response.AdminSummaryResponseDto;
import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.entity.ReservationSeat;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.ReservationStatus;
import com.example.moviereservation.repository.MovieRepository;
import com.example.moviereservation.repository.ReservationRepository;
import com.example.moviereservation.repository.ReservationSeatRepository;
import com.example.moviereservation.repository.ScheduleRepository;
import com.example.moviereservation.repository.ScheduleSeatRepository;
import com.example.moviereservation.repository.TheaterRepository;
import com.example.moviereservation.repository.UserRepository;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AdminServiceImpl")
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TheaterRepository theaterRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ScheduleSeatRepository scheduleSeatRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationSeatRepository reservationSeatRepository;

    @InjectMocks
    private AdminServiceImpl service;

    @Test
    @DisplayName("getSummary reports counts from every repository")
    void summary() {
        when(userRepository.count()).thenReturn(2L);
        when(theaterRepository.count()).thenReturn(3L);
        when(movieRepository.count()).thenReturn(4L);
        when(scheduleRepository.count()).thenReturn(5L);
        when(scheduleSeatRepository.count()).thenReturn(6L);
        when(reservationRepository.count()).thenReturn(7L);
        when(reservationRepository.countByStatus(ReservationStatus.PENDING)).thenReturn(8L);

        AdminSummaryResponseDto summary = service.getSummary();

        assertThat(summary.getUsers()).isEqualTo(2);
        assertThat(summary.getTheaters()).isEqualTo(3);
        assertThat(summary.getMovies()).isEqualTo(4);
        assertThat(summary.getSchedules()).isEqualTo(5);
        assertThat(summary.getScheduleSeats()).isEqualTo(6);
        assertThat(summary.getReservations()).isEqualTo(7);
        assertThat(summary.getPendingReservations()).isEqualTo(8);
    }

    @Test
    @DisplayName("getReservations joins customer, showtime, seats and status")
    void reservations() {
        Schedule schedule = TestData.schedule(TestData.movie(), TestData.theater());
        User user = TestData.user(1, "jane@example.com");
        Reservation reservation = TestData.reservation(5, ReservationStatus.PENDING, user, schedule);
        ScheduleSeat scheduleSeat = TestData.availableSeat(10, schedule, TestData.standardSeat(schedule.getTheater()));
        ReservationSeat link = new ReservationSeat(1, reservation, scheduleSeat);

        when(reservationRepository.findAllForAdmin()).thenReturn(List.of(reservation));
        when(reservationSeatRepository.findAllForAdmin()).thenReturn(List.of(link));

        List<AdminReservationResponseDto> result = service.getReservations();

        assertThat(result).hasSize(1);
        AdminReservationResponseDto dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(5);
        assertThat(dto.getUserName()).isEqualTo("Jane Doe");
        assertThat(dto.getUserEmail()).isEqualTo("jane@example.com");
        assertThat(dto.getMovieTitle()).isEqualTo("Inception");
        assertThat(dto.getTheaterName()).isEqualTo("Downtown Cinema");
        assertThat(dto.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(dto.getSeatIds()).containsExactly(10);
        assertThat(dto.getSeatLabels()).containsExactly("A1 (STANDARD)");
    }

    @Test
    @DisplayName("getReservations tolerates a reservation whose user was deleted")
    void reservationWithoutUser() {
        Schedule schedule = TestData.schedule(TestData.movie(), TestData.theater());
        Reservation reservation = TestData.reservation(5, ReservationStatus.CONFIRMED, null, schedule);

        when(reservationRepository.findAllForAdmin()).thenReturn(List.of(reservation));
        when(reservationSeatRepository.findAllForAdmin()).thenReturn(List.of());

        List<AdminReservationResponseDto> result = service.getReservations();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserName()).isEqualTo("Unknown user");
        assertThat(result.get(0).getUserId()).isNull();
        assertThat(result.get(0).getSeatLabels()).isEmpty();
    }

    @Test
    @DisplayName("getScheduleSeats exposes availability and the linked reservation status")
    void scheduleSeats() {
        Schedule schedule = TestData.schedule(TestData.movie(), TestData.theater());
        ScheduleSeat scheduleSeat = TestData.unavailableSeat(10, schedule, TestData.standardSeat(schedule.getTheater()));
        Reservation reservation = TestData.reservation(5, ReservationStatus.PENDING,
                TestData.user(1, "jane@example.com"), schedule);
        ReservationSeat link = new ReservationSeat(1, reservation, scheduleSeat);

        when(scheduleSeatRepository.findAllForAdmin()).thenReturn(List.of(scheduleSeat));
        when(reservationSeatRepository.findAllForAdmin()).thenReturn(List.of(link));

        List<AdminScheduleSeatResponseDto> result = service.getScheduleSeats();

        assertThat(result).hasSize(1);
        AdminScheduleSeatResponseDto dto = result.get(0);
        assertThat(dto.getSeatLabel()).isEqualTo("A1 (STANDARD)");
        assertThat(dto.getAvailable()).isFalse();
        assertThat(dto.getReservationId()).isEqualTo(5);
        assertThat(dto.getReservationStatus()).isEqualTo(ReservationStatus.PENDING);
    }
}
