package com.example.moviereservation.service;

import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.Seat;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.mapper.ScheduleSeatMapper;
import com.example.moviereservation.repository.ScheduleSeatRepository;
import com.example.moviereservation.repository.SeatRepository;
import com.example.moviereservation.support.TestData;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ScheduleSeatServiceImpl")
class ScheduleSeatServiceImplTest {

    @Mock
    private ScheduleSeatRepository repository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ScheduleSeatMapper mapper;

    @InjectMocks
    private ScheduleSeatServiceImpl service;

    private final Theater theater = TestData.theater();
    private final Schedule schedule = TestData.schedule(TestData.movie(), theater);
    private final Seat seat = TestData.standardSeat(theater);
    private final ScheduleSeat scheduleSeat = TestData.availableSeat(10, schedule, seat);

    @Nested
    @DisplayName("[AS-IS] current behavior")
    class AsIs {

        @Test
        @DisplayName("getAllSeats delegates to the repository")
        void getAllSeats() {
            when(repository.findAll()).thenReturn(List.of(scheduleSeat));

            assertThat(service.getAllSeats()).containsExactly(scheduleSeat);
        }

        @Test
        @DisplayName("getAllByIds delegates to the repository")
        void getAllByIds() {
            when(repository.findAllById(List.of(10))).thenReturn(List.of(scheduleSeat));

            assertThat(service.getAllByIds(List.of(10))).containsExactly(scheduleSeat);
        }

        @Test
        @DisplayName("getAllByIdsForUpdate delegates to the locking query")
        void getAllByIdsForUpdate() {
            when(repository.getScheduleSeatsForUpdate(List.of(10))).thenReturn(List.of(scheduleSeat));

            assertThat(service.getAllByIdsForUpdate(List.of(10))).containsExactly(scheduleSeat);
        }

        @Test
        @DisplayName("getByScheduleId delegates to the repository")
        void getByScheduleId() {
            when(repository.getByScheduleId(1)).thenReturn(List.of(scheduleSeat));

            assertThat(service.getByScheduleId(1)).containsExactly(scheduleSeat);
        }

        @Test
        @DisplayName("getById returns the seat")
        void getById() {
            when(repository.findById(10)).thenReturn(Optional.of(scheduleSeat));

            assertThat(service.getById(10)).isSameAs(scheduleSeat);
        }

        @Test
        @DisplayName("getById throws ResourceNotFoundException when missing")
        void getByIdMissing() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(99))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("markAvailable sets the seat available and saves it")
        void markAvailable() {
            scheduleSeat.setAvailable(false);
            when(repository.findById(10)).thenReturn(Optional.of(scheduleSeat));

            assertThat(service.markAvailable(10)).isSameAs(scheduleSeat);
            assertThat(scheduleSeat.getAvailable()).isTrue();
            verify(repository).save(scheduleSeat);
        }

        @Test
        @DisplayName("markUnavailable sets the seat unavailable and saves it")
        void markUnavailable() {
            when(repository.findById(10)).thenReturn(Optional.of(scheduleSeat));

            assertThat(service.markUnavailable(10)).isSameAs(scheduleSeat);
            assertThat(scheduleSeat.getAvailable()).isFalse();
            verify(repository).save(scheduleSeat);
        }

        @Test
        @DisplayName("setAvailability stores the requested value")
        void setAvailability() {
            when(repository.findById(10)).thenReturn(Optional.of(scheduleSeat));

            assertThat(service.setAvailability(10, false)).isSameAs(scheduleSeat);
            assertThat(scheduleSeat.getAvailable()).isFalse();
            verify(repository).save(scheduleSeat);
        }

        @Test
        @DisplayName("setAvailability throws ResourceNotFoundException when missing")
        void setAvailabilityMissing() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.setAvailability(99, true))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("isAvailable returns the stored flag")
        void isAvailable() {
            when(repository.findById(10)).thenReturn(Optional.of(scheduleSeat));

            assertThat(service.isAvailable(10)).isTrue();
        }

        @Test
        @DisplayName("isAvailable throws ResourceNotFoundException when missing")
        void isAvailableMissing() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.isAvailable(99))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("getAvailableSeats asks the repository for the available seats of the schedule")
        void getAvailableSeatsAsIs() {
            when(repository.getByScheduleIdAndAvailable(1, true)).thenReturn(List.of(scheduleSeat));

            assertThat(service.getAvailableSeats(1)).containsExactly(scheduleSeat);
            verify(repository).getByScheduleIdAndAvailable(1, true);
        }

        @Test
        @DisplayName("generateScheduleSeats creates one available seat per theater seat")
        void generateScheduleSeats() {
            Theater smallTheater = TestData.theater(1, "Small", "Cairo", 2);
            Schedule smallSchedule = TestData.schedule(TestData.movie(), smallTheater);
            Seat first = TestData.seat(1, 'A', 1,
                    com.example.moviereservation.enums.SeatClass.STANDARD, smallTheater);
            Seat second = TestData.seat(2, 'A', 2,
                    com.example.moviereservation.enums.SeatClass.VIP, smallTheater);
            when(seatRepository.getByTheaterId(1)).thenReturn(List.of(first, second));
            when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

            service.generateScheduleSeats(smallSchedule);

            ArgumentCaptor<List<ScheduleSeat>> captor = ArgumentCaptor.forClass(List.class);
            verify(repository).saveAll(captor.capture());
            assertThat(captor.getValue()).hasSize(2);
            assertThat(captor.getValue()).allMatch(ScheduleSeat::getAvailable);
            assertThat(captor.getValue()).allMatch(s -> s.getSchedule() == smallSchedule);
            assertThat(captor.getValue()).extracting(ScheduleSeat::getSeat)
                    .containsExactly(first, second);
        }

        @Test
        @DisplayName("generateScheduleSeats rejects a theater without seat rows")
        void generateScheduleSeatsNoSeats() {
            when(seatRepository.getByTheaterId(1)).thenReturn(List.of());

            assertThatThrownBy(() -> service.generateScheduleSeats(schedule))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("generateScheduleSeats breaks when totalSeats exceeds the seat rows")
        void generateScheduleSeatsTooManySeatsAsIs() {
            Theater bigTheater = TestData.theater(1, "Big", "Cairo", 5);
            Schedule bigSchedule = TestData.schedule(TestData.movie(), bigTheater);
            Seat onlySeat = TestData.seat(1, 'A', 1,
                    com.example.moviereservation.enums.SeatClass.STANDARD, bigTheater);
            when(seatRepository.getByTheaterId(1)).thenReturn(List.of(onlySeat));

            assertThatThrownBy(() -> service.generateScheduleSeats(bigSchedule))
                    .isInstanceOf(IndexOutOfBoundsException.class);

            verify(repository, never()).saveAll(any());
        }

        @Test
        @DisplayName("seatsExists delegates to the repository")
        void seatsExists() {
            when(repository.existsByScheduleId(1)).thenReturn(true);
            when(repository.existsByScheduleId(2)).thenReturn(false);

            assertThat(service.seatsExists(1)).isTrue();
            assertThat(service.seatsExists(2)).isFalse();
        }

        @Test
        @DisplayName("saveAll delegates to the repository")
        void saveAll() {
            when(repository.saveAll(anyList())).thenReturn(List.of(scheduleSeat));

            assertThat(service.saveAll(List.of(scheduleSeat))).containsExactly(scheduleSeat);
        }
    }

    @Nested
    @DisplayName("[EXPECTED] correct behavior")
    @Tag("expected")
    class Expected {

        @Test
        @DisplayName("setAvailability rejects a null availability instead of storing it")
        void setAvailabilityShouldRejectNull() {
            when(repository.findById(10)).thenReturn(Optional.of(scheduleSeat));

            assertThatThrownBy(() -> service.setAvailability(10, null))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThat(scheduleSeat.getAvailable()).isTrue();
            verify(repository, never()).save(scheduleSeat);
        }

        @Test
        @DisplayName("generateScheduleSeats rejects a theater with fewer seats than totalSeats")
        void generateScheduleSeatsShouldValidateSeatCount() {
            Theater bigTheater = TestData.theater(1, "Big", "Cairo", 5);
            Schedule bigSchedule = TestData.schedule(TestData.movie(), bigTheater);
            Seat onlySeat = TestData.seat(1, 'A', 1,
                    com.example.moviereservation.enums.SeatClass.STANDARD, bigTheater);
            when(seatRepository.getByTheaterId(1)).thenReturn(List.of(onlySeat));

            assertThatThrownBy(() -> service.generateScheduleSeats(bigSchedule))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
