package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.ReservationRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.ReservationStatus;
import com.example.moviereservation.exceptions.InvalidEntityException;
import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.mapper.ReservationMapper;
import com.example.moviereservation.repository.ReservationRepository;
import com.example.moviereservation.repository.ReservationSeatRepository;
import com.example.moviereservation.repository.ScheduleRepository;
import com.example.moviereservation.repository.UserRepository;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ReservationServiceImpl")
class ReservationServiceImplTest {

    @Mock
    private ReservationMapper mapper;

    @Mock
    private ReservationRepository repository;

    @Mock
    private ReservationSeatRepository reservationSeatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ScheduleSeatService scheduleSeatService;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private ReservationServiceImpl service;

    private Schedule schedule;
    private User user;
    private ScheduleSeat firstSeat;
    private ScheduleSeat secondSeat;

    @BeforeEach
    void setUp() {
        schedule = TestData.schedule(TestData.movie(), TestData.theater());
        schedule.setId(1);
        user = TestData.user(1, "jane@example.com");
        firstSeat = TestData.availableSeat(10, schedule, TestData.standardSeat(schedule.getTheater()));
        secondSeat = TestData.availableSeat(11, schedule, TestData.premiumSeat(schedule.getTheater()));
    }

    private void stubReservationLookups(ReservationRequestDto request, List<ScheduleSeat> fetched) {
        when(scheduleRepository.findById(request.getScheduleId())).thenReturn(Optional.of(schedule));
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(scheduleSeatService.getAllByIdsForUpdate(request.getScheduleSeatIds().stream()
                .distinct().toList())).thenReturn(fetched);
    }

    private ReservationResponseDto stubSuccessfulSave(ReservationRequestDto request,
                                                      List<ScheduleSeat> fetched,
                                                      double price,
                                                      Reservation saved) {
        Reservation entity = TestData.reservation(null, ReservationStatus.PENDING, null, null);
        ReservationResponseDto response = new ReservationResponseDto();
        response.setId(saved.getId());
        when(mapper.toEntity(request)).thenReturn(entity);
        when(pricingService.calculatePrice(anyList(), eq(schedule))).thenReturn(price);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(response);
        when(reservationSeatRepository.getReservedSeatsIds(saved.getId()))
                .thenReturn(fetched.stream().map(ScheduleSeat::getId).toList());
        return response;
    }

    @Nested
    @DisplayName("[AS-IS] current behavior")
    class AsIs {

        @Test
        @DisplayName("reserveSchedule throws ResourceNotFoundException when the schedule is missing")
        void reserveMissingSchedule() {
            ReservationRequestDto request = TestData.reservationRequest(1, 99, List.of(10));

            when(scheduleRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.reserveSchedule(request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(userRepository);
        }

        @Test
        @DisplayName("reserveSchedule throws ResourceNotFoundException when the user is missing")
        void reserveMissingUser() {
            ReservationRequestDto request = TestData.reservationRequest(99, 1, List.of(10));
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
            when(userRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.reserveSchedule(request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("reserveSchedule throws ResourceNotFoundException when a seat does not exist")
        void reserveMissingSeat() {
            ReservationRequestDto request = TestData.reservationRequest(1, 1, List.of(10, 11));
            stubReservationLookups(request, List.of(firstSeat));

            assertThatThrownBy(() -> service.reserveSchedule(request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("reserveSchedule rejects an already reserved seat")
        void reserveUnavailableSeat() {
            ReservationRequestDto request = TestData.reservationRequest(1, 1, List.of(10));
            firstSeat.setAvailable(false);
            stubReservationLookups(request, List.of(firstSeat));

            assertThatThrownBy(() -> service.reserveSchedule(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already Reserved");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("reserveSchedule rejects a seat that belongs to another schedule")
        void reserveSeatOfAnotherSchedule() {
            ReservationRequestDto request = TestData.reservationRequest(1, 1, List.of(10));
            Schedule otherSchedule = TestData.schedule(2, schedule.getStartTime(), schedule.getEndTime(),
                    schedule.getDate(), 10.0, schedule.getMovie(), schedule.getTheater());
            ScheduleSeat foreignSeat = TestData.availableSeat(10, otherSchedule,
                    TestData.standardSeat(schedule.getTheater()));
            stubReservationLookups(request, List.of(foreignSeat));

            assertThatThrownBy(() -> service.reserveSchedule(request))
                    .isInstanceOf(InvalidEntityException.class);
        }

        @Test
        @DisplayName("reserveSchedule treats duplicated seat ids as missing seats")
        void reserveDuplicateSeatIdsAsIs() {
            ReservationRequestDto request = TestData.reservationRequest(1, 1, List.of(10, 10));
            stubReservationLookups(request, List.of(firstSeat));

            assertThatThrownBy(() -> service.reserveSchedule(request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("reserveSchedule persists the reservation, pricing, seats and join rows")
        void reserveSuccessfully() {
            ReservationRequestDto request = TestData.reservationRequest(1, 1, List.of(10, 11));
            stubReservationLookups(request, List.of(firstSeat, secondSeat));
            Reservation saved = TestData.reservation(5, ReservationStatus.PENDING, user, schedule);
            ReservationResponseDto response = stubSuccessfulSave(request, List.of(firstSeat, secondSeat),
                    23.0, saved);

            ReservationResponseDto result = service.reserveSchedule(request);

            assertThat(result).isSameAs(response);
            assertThat(result.getReservedSeatsIds()).containsExactly(10, 11);

            ArgumentCaptor<Reservation> savedCaptor = ArgumentCaptor.forClass(Reservation.class);
            verify(repository).save(savedCaptor.capture());
            Reservation toSave = savedCaptor.getValue();
            assertThat(toSave.getSchedule()).isSameAs(schedule);
            assertThat(toSave.getUser()).isSameAs(user);
            assertThat(toSave.getTotalPrice()).isEqualTo(23f);
            assertThat(toSave.getBookingDate()).isEqualTo(LocalDate.now());

            assertThat(firstSeat.getAvailable()).isFalse();
            assertThat(secondSeat.getAvailable()).isFalse();

            ArgumentCaptor<List<com.example.moviereservation.entity.ReservationSeat>> joins =
                    ArgumentCaptor.forClass(List.class);
            verify(reservationSeatRepository).saveAll(joins.capture());
            assertThat(joins.getValue()).hasSize(2);
            assertThat(joins.getValue()).allMatch(join -> join.getReservation() == saved);
            verify(scheduleSeatService).saveAll(List.of(firstSeat, secondSeat));
        }

        @Test
        @DisplayName("reserveSchedule creates a zero price reservation for an empty seat list")
        void reserveEmptySeatListAsIs() {
            ReservationRequestDto request = TestData.reservationRequest(1, 1, List.of());
            stubReservationLookups(request, List.of());
            Reservation saved = TestData.reservation(5, ReservationStatus.PENDING, user, schedule);
            ReservationResponseDto response = stubSuccessfulSave(request, List.of(), 0.0, saved);

            assertThatCode(() -> service.reserveSchedule(request)).doesNotThrowAnyException();
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("cancelReservation throws ResourceNotFoundException when the reservation is missing")
        void cancelMissing() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.cancelReservation(99))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(reservationSeatRepository);
        }

        @Test
        @DisplayName("cancelReservation rejects an already cancelled reservation")
        void cancelAlreadyCancelled() {
            Reservation reservation = TestData.reservation(5, ReservationStatus.CANCELLED, user, schedule);
            when(repository.findById(5)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> service.cancelReservation(5))
                    .isInstanceOf(InvalidEntityException.class);

            verify(reservationSeatRepository, never()).getReservedSeats(any());
            verify(reservationSeatRepository, never()).deleteAllByReservationId(any());
        }

        @Test
        @DisplayName("cancelReservation releases the seats and deletes the join rows")
        void cancelSuccessfully() {
            Reservation reservation = TestData.reservation(5, ReservationStatus.PENDING, user, schedule);
            when(repository.findById(5)).thenReturn(Optional.of(reservation));
            when(reservationSeatRepository.getReservedSeats(5)).thenReturn(List.of(firstSeat, secondSeat));

            service.cancelReservation(5);

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
            assertThat(firstSeat.getAvailable()).isTrue();
            assertThat(secondSeat.getAvailable()).isTrue();
            verify(scheduleSeatService).saveAll(List.of(firstSeat, secondSeat));
            verify(reservationSeatRepository).deleteAllByReservationId(5);
        }

        @Test
        @DisplayName("cancelReservation allows cancelling a confirmed reservation")
        void cancelConfirmed() {
            Reservation reservation = TestData.reservation(5, ReservationStatus.CONFIRMED, user, schedule);
            when(repository.findById(5)).thenReturn(Optional.of(reservation));
            when(reservationSeatRepository.getReservedSeats(5)).thenReturn(List.of(firstSeat));

            assertThatCode(() -> service.cancelReservation(5)).doesNotThrowAnyException();
            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        }

        @Test
        @DisplayName("cancelReservation allows cancelling an expired reservation")
        void cancelExpiredAsIs() {
            Reservation reservation = TestData.reservation(5, ReservationStatus.EXPIRED, user, schedule);
            when(repository.findById(5)).thenReturn(Optional.of(reservation));
            when(reservationSeatRepository.getReservedSeats(5)).thenReturn(List.of(firstSeat));

            assertThatCode(() -> service.cancelReservation(5)).doesNotThrowAnyException();
            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("[EXPECTED] correct behavior")
    @Tag("expected")
    class Expected {

        @Test
        @DisplayName("reserveSchedule accepts duplicated seat ids and books each seat once")
        void reserveDuplicateSeatIds() {
            ReservationRequestDto request = TestData.reservationRequest(1, 1, List.of(10, 10));
            stubReservationLookups(request, List.of(firstSeat));
            Reservation saved = TestData.reservation(5, ReservationStatus.PENDING, user, schedule);
            stubSuccessfulSave(request, List.of(firstSeat), 10.0, saved);

            assertThatCode(() -> service.reserveSchedule(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("reserveSchedule rejects an empty seat list")
        void reserveEmptySeatList() {
            ReservationRequestDto request = TestData.reservationRequest(1, 1, List.of());
            stubReservationLookups(request, List.of());
            Reservation saved = TestData.reservation(5, ReservationStatus.PENDING, user, schedule);
            stubSuccessfulSave(request, List.of(), 0.0, saved);

            assertThatThrownBy(() -> service.reserveSchedule(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("cancelReservation rejects an expired reservation")
        void cancelExpired() {
            Reservation reservation = TestData.reservation(5, ReservationStatus.EXPIRED, user, schedule);
            when(repository.findById(5)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> service.cancelReservation(5))
                    .isInstanceOf(InvalidEntityException.class);
        }
    }
}
