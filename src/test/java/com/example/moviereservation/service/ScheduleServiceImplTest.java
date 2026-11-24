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
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ScheduleServiceImpl")
class ScheduleServiceImplTest {

    @Mock
    private ScheduleSeatService scheduleSeatService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private ScheduleMapper mapper;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private ScheduleServiceImpl service;

    private final Movie movie = TestData.movie();
    private final Theater theater = TestData.theater();

    private ScheduleRequestDto validRequest() {
        return TestData.scheduleRequest(1, 1);
    }

    private void stubSuccessfulAdd(ScheduleRequestDto request, Schedule schedule,
                                   ScheduleResponseDto response) {
        when(scheduleRepository.getSchedulesByDate(request.getDate())).thenReturn(List.of());
        when(movieRepository.findById(request.getMovieId())).thenReturn(Optional.of(movie));
        when(theaterRepository.findById(request.getTheaterId())).thenReturn(Optional.of(theater));
        when(mapper.dtoToEntity(request, movie, theater)).thenReturn(schedule);
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(scheduleSeatService.seatsExists(schedule.getId())).thenReturn(false);
        when(mapper.entityToDto(schedule)).thenReturn(response);
    }

    @Nested
    @DisplayName("[AS-IS] current behavior")
    class AsIs {

        // -----------------------------------------------------------------
        // validateSchedule
        // -----------------------------------------------------------------

        @Test
        @DisplayName("rejects an end time before the start time")
        void endBeforeStart() {
            ScheduleRequestDto request = TestData.scheduleRequest(
                    LocalDate.now().plusDays(1).atTime(20, 0),
                    LocalDate.now().plusDays(1).atTime(18, 0),
                    LocalDate.now().plusDays(1), 10.0, 1, 1);

            assertThatThrownBy(() -> service.validateSchedule(request))
                    .isInstanceOf(InvalidEntityException.class);

            verifyNoInteractions(scheduleRepository);
        }

        @Test
        @DisplayName("rejects a price of zero")
        void zeroPrice() {
            ScheduleRequestDto request = TestData.scheduleRequest(1, 1);
            request.setPrice(0.0);

            assertThatThrownBy(() -> service.validateSchedule(request))
                    .isInstanceOf(InvalidEntityException.class);
        }

        @Test
        @DisplayName("rejects a negative price")
        void negativePrice() {
            ScheduleRequestDto request = TestData.scheduleRequest(1, 1);
            request.setPrice(-1.0);

            assertThatThrownBy(() -> service.validateSchedule(request))
                    .isInstanceOf(InvalidEntityException.class);
        }

        @Test
        @DisplayName("rejects an overlapping schedule in the same theater")
        void overlappingSchedule() {
            ScheduleRequestDto request = validRequest();
            Schedule existing = TestData.schedule(2,
                    request.getDate().atTime(18, 0), request.getDate().atTime(20, 0),
                    request.getDate(), 10.0, movie, theater);
            when(scheduleRepository.getSchedulesByDate(request.getDate()))
                    .thenReturn(List.of(existing));

            assertThatThrownBy(() -> service.validateSchedule(request))
                    .isInstanceOf(ScheduleOverlapException.class);
        }

        @Test
        @DisplayName("allows back-to-back schedules that only touch at the boundary")
        void adjacentScheduleAllowed() {
            ScheduleRequestDto request = TestData.scheduleRequest(
                    LocalDate.now().plusDays(1).atTime(20, 0),
                    LocalDate.now().plusDays(1).atTime(22, 0),
                    LocalDate.now().plusDays(1), 10.0, 1, 1);
            Schedule existing = TestData.schedule(2,
                    request.getDate().atTime(18, 0), request.getDate().atTime(20, 0),
                    request.getDate(), 10.0, movie, theater);
            when(scheduleRepository.getSchedulesByDate(request.getDate()))
                    .thenReturn(List.of(existing));

            assertThatCode(() -> service.validateSchedule(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("allows the same time slot in a different theater")
        void differentTheaterAllowed() {
            ScheduleRequestDto request = validRequest();
            Theater otherTheater = TestData.theater(2, "Other", "Cairo", 50);
            Schedule existing = TestData.schedule(2,
                    request.getStartTime(), request.getEndTime(), request.getDate(),
                    10.0, movie, otherTheater);
            when(scheduleRepository.getSchedulesByDate(request.getDate()))
                    .thenReturn(List.of(existing));

            assertThatCode(() -> service.validateSchedule(request)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("allows the same theater and time on a different date")
        void differentDateAllowed() {
            ScheduleRequestDto request = validRequest();
            when(scheduleRepository.getSchedulesByDate(request.getDate())).thenReturn(List.of());

            assertThatCode(() -> service.validateSchedule(request)).doesNotThrowAnyException();
        }

        // -----------------------------------------------------------------
        // addSchedule
        // -----------------------------------------------------------------

        @Test
        @DisplayName("addSchedule saves the schedule and generates its seats")
        void addSchedule() {
            ScheduleRequestDto request = validRequest();
            Schedule schedule = TestData.schedule(1, request.getStartTime(), request.getEndTime(),
                    request.getDate(), request.getPrice(), movie, theater);
            ScheduleResponseDto response = new ScheduleResponseDto();
            stubSuccessfulAdd(request, schedule, response);

            assertThat(service.addSchedule(request)).isSameAs(response);
            verify(scheduleSeatService).generateScheduleSeats(schedule);
        }

        @Test
        @DisplayName("addSchedule throws ResourceNotFoundException when the movie is missing")
        void addScheduleMissingMovie() {
            ScheduleRequestDto request = validRequest();
            when(scheduleRepository.getSchedulesByDate(request.getDate())).thenReturn(List.of());
            when(movieRepository.findById(1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.addSchedule(request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(theaterRepository);
        }

        @Test
        @DisplayName("addSchedule throws ResourceNotFoundException when the theater is missing")
        void addScheduleMissingTheater() {
            ScheduleRequestDto request = validRequest();
            when(scheduleRepository.getSchedulesByDate(request.getDate())).thenReturn(List.of());
            when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
            when(theaterRepository.findById(1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.addSchedule(request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("addSchedule persists the schedule before rejecting already generated seats")
        void addScheduleSeatsAlreadyExistAsIs() {
            ScheduleRequestDto request = validRequest();
            Schedule schedule = TestData.schedule(1, request.getStartTime(), request.getEndTime(),
                    request.getDate(), request.getPrice(), movie, theater);
            when(scheduleRepository.getSchedulesByDate(request.getDate())).thenReturn(List.of());
            when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
            when(theaterRepository.findById(1)).thenReturn(Optional.of(theater));
            when(mapper.dtoToEntity(request, movie, theater)).thenReturn(schedule);
            when(scheduleRepository.save(schedule)).thenReturn(schedule);
            when(scheduleSeatService.seatsExists(1)).thenReturn(true);

            assertThatThrownBy(() -> service.addSchedule(request))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(scheduleRepository).save(schedule);
            verify(scheduleSeatService, never()).generateScheduleSeats(any());
        }

        // -----------------------------------------------------------------
        // reads
        // -----------------------------------------------------------------

        @Test
        @DisplayName("getAll maps every schedule")
        void getAll() {
            Schedule schedule = TestData.schedule(movie, theater);
            ScheduleResponseDto response = new ScheduleResponseDto();
            when(scheduleRepository.findAll()).thenReturn(List.of(schedule));
            when(mapper.entityToDto(schedule)).thenReturn(response);

            assertThat(service.getAll()).containsExactly(response);
        }

        @Test
        @DisplayName("getScheduleById returns the mapped schedule")
        void getScheduleById() {
            Schedule schedule = TestData.schedule(movie, theater);
            ScheduleResponseDto response = new ScheduleResponseDto();
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
            when(mapper.entityToDto(schedule)).thenReturn(response);

            assertThat(service.getScheduleById(1)).isSameAs(response);
        }

        @Test
        @DisplayName("getScheduleById throws ResourceNotFoundException when missing")
        void getScheduleByIdMissing() {
            when(scheduleRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getScheduleById(99))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("getTodaySchedules maps today's schedules")
        void getTodaySchedules() {
            Schedule schedule = TestData.schedule(movie, theater);
            ScheduleResponseDto response = new ScheduleResponseDto();
            when(scheduleRepository.getSchedulesByDate(LocalDate.now())).thenReturn(List.of(schedule));
            when(mapper.entityToDto(schedule)).thenReturn(response);

            assertThat(service.getTodaySchedules()).containsExactly(response);
        }

        @Test
        @DisplayName("getUpcomingSchedules maps future schedules")
        void getUpcomingSchedules() {
            Schedule schedule = TestData.schedule(movie, theater);
            ScheduleResponseDto response = new ScheduleResponseDto();
            when(scheduleRepository.getSchedulesByDateAfter(LocalDate.now())).thenReturn(List.of(schedule));
            when(mapper.entityToDto(schedule)).thenReturn(response);

            assertThat(service.getUpcomingSchedules()).containsExactly(response);
        }

        @Test
        @DisplayName("getScheduleByDate maps the schedule for a date")
        void getScheduleByDate() {
            LocalDate date = LocalDate.now().plusDays(1);
            Schedule schedule = TestData.schedule(movie, theater);
            ScheduleResponseDto response = new ScheduleResponseDto();
            when(scheduleRepository.getScheduleByDate(date)).thenReturn(schedule);
            when(mapper.entityToDto(schedule)).thenReturn(response);

            assertThat(service.getScheduleByDate(date)).isSameAs(response);
        }

        @Test
        @DisplayName("showMovie returns the movie of the schedule")
        void showMovie() {
            Schedule schedule = TestData.schedule(movie, theater);
            MovieResponseDto response = new MovieResponseDto();
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
            when(movieMapper.entityToDto(movie)).thenReturn(response);

            assertThat(service.showMovie(1)).isSameAs(response);
        }

        @Test
        @DisplayName("showMovie throws ResourceNotFoundException when the schedule is missing")
        void showMovieMissing() {
            when(scheduleRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.showMovie(99))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        // -----------------------------------------------------------------
        // updates
        // -----------------------------------------------------------------

        @Test
        @DisplayName("updateSchedule replaces the schedule values")
        void updateSchedule() {
            ScheduleRequestDto request = validRequest();
            Schedule existing = TestData.schedule(movie, theater);
            Schedule updated = TestData.schedule(null, request.getStartTime(), request.getEndTime(),
                    request.getDate(), request.getPrice(), movie, theater);
            ScheduleResponseDto response = new ScheduleResponseDto();
            when(scheduleRepository.getSchedulesByDate(request.getDate())).thenReturn(List.of());
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(existing));
            when(mapper.entityToDto(existing)).thenReturn(response);
            when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
            when(theaterRepository.findById(1)).thenReturn(Optional.of(theater));
            when(mapper.dtoToEntity(request, movie, theater)).thenReturn(updated);
            when(scheduleRepository.save(updated)).thenReturn(updated);
            when(mapper.entityToDto(updated)).thenReturn(response);

            assertThat(service.updateSchedule(request, 1)).isSameAs(response);
            assertThat(updated.getId()).isEqualTo(1);
        }

        @Test
        @DisplayName("updateSchedule throws ResourceNotFoundException when the schedule is missing")
        void updateScheduleMissing() {
            ScheduleRequestDto request = validRequest();
            when(scheduleRepository.getSchedulesByDate(request.getDate())).thenReturn(List.of());
            when(scheduleRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateSchedule(request, 99))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("updateMovie replaces the movie of a schedule")
        void updateMovie() {
            Schedule schedule = TestData.schedule(movie, theater);
            Movie newMovie = TestData.movie(2, "Dune", 4.4f, LocalDate.of(2021, 10, 22),
                    "Sci-Fi", 155f);
            ScheduleResponseDto response = new ScheduleResponseDto();
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
            when(mapper.entityToDto(schedule)).thenReturn(response);

            assertThat(service.updateMovie(1, newMovie)).isSameAs(response);
            assertThat(schedule.getMovie()).isSameAs(newMovie);
        }

        @Test
        @DisplayName("updateMovie throws ResourceNotFoundException when the schedule is missing")
        void updateMovieMissing() {
            when(scheduleRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateMovie(99, movie))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("updatePrice replaces the price")
        void updatePrice() {
            Schedule schedule = TestData.schedule(movie, theater);
            ScheduleResponseDto response = new ScheduleResponseDto();
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
            when(mapper.entityToDto(schedule)).thenReturn(response);

            assertThat(service.updatePrice(1, 25.0)).isSameAs(response);
            assertThat(schedule.getPrice()).isEqualTo(25.0);
        }

        @Test
        @DisplayName("updatePrice throws NoSuchElementException for an unknown schedule")
        void updatePriceMissingAsIs() {
            when(scheduleRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updatePrice(99, 25.0))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("updatePrice accepts a negative price")
        void updatePriceNegativeAsIs() {
            Schedule schedule = TestData.schedule(movie, theater);
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
            when(mapper.entityToDto(schedule)).thenReturn(new ScheduleResponseDto());

            assertThatCode(() -> service.updatePrice(1, -5.0)).doesNotThrowAnyException();
            assertThat(schedule.getPrice()).isEqualTo(-5.0);
        }

        @Test
        @DisplayName("updateTheater replaces the theater")
        void updateTheater() {
            Schedule schedule = TestData.schedule(movie, theater);
            Theater newTheater = TestData.theater(2, "Other", "Giza", 80);
            ScheduleResponseDto response = new ScheduleResponseDto();
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
            when(mapper.entityToDto(schedule)).thenReturn(response);

            assertThat(service.updateTheater(1, newTheater)).isSameAs(response);
            assertThat(schedule.getTheater()).isSameAs(newTheater);
        }

        @Test
        @DisplayName("updateTheater throws NoSuchElementException for an unknown schedule")
        void updateTheaterMissingAsIs() {
            when(scheduleRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateTheater(99, theater))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("changeTime replaces the start and end time")
        void changeTime() {
            Schedule schedule = TestData.schedule(movie, theater);
            LocalDateTime start = LocalDate.now().plusDays(2).atTime(9, 0);
            LocalDateTime end = LocalDate.now().plusDays(2).atTime(11, 0);
            ScheduleResponseDto response = new ScheduleResponseDto();
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
            when(mapper.entityToDto(schedule)).thenReturn(response);

            assertThat(service.changeTime(1, start, end)).isSameAs(response);
            assertThat(schedule.getStartTime()).isEqualTo(start);
            assertThat(schedule.getEndTime()).isEqualTo(end);
        }

        @Test
        @DisplayName("changeTime accepts an end time before the start time")
        void changeTimeInvalidAsIs() {
            Schedule schedule = TestData.schedule(movie, theater);
            LocalDateTime start = LocalDate.now().plusDays(2).atTime(12, 0);
            LocalDateTime end = LocalDate.now().plusDays(2).atTime(9, 0);
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
            when(mapper.entityToDto(schedule)).thenReturn(new ScheduleResponseDto());

            assertThatCode(() -> service.changeTime(1, start, end)).doesNotThrowAnyException();
            assertThat(schedule.getEndTime()).isEqualTo(end);
        }

        @Test
        @DisplayName("deleteSchedule removes an existing schedule")
        void deleteSchedule() {
            Schedule schedule = TestData.schedule(movie, theater);
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
            when(mapper.entityToDto(schedule)).thenReturn(new ScheduleResponseDto());

            service.deleteSchedule(1);

            verify(scheduleRepository).deleteById(1);
        }

        @Test
        @DisplayName("deleteSchedule throws ResourceNotFoundException when missing")
        void deleteScheduleMissing() {
            when(scheduleRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteSchedule(99))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(scheduleRepository, never()).deleteById(99);
        }
    }

    @Nested
    @DisplayName("[EXPECTED] correct behavior")
    @Tag("expected")
    class Expected {

        @Test
        @DisplayName("addSchedule must run in a transaction so a failure after save is rolled back")
        void addScheduleShouldBeTransactional() throws NoSuchMethodException {
            Transactional transactional = ScheduleServiceImpl.class
                    .getMethod("addSchedule", ScheduleRequestDto.class)
                    .getAnnotation(Transactional.class);

            assertThat(transactional)
                    .as("addSchedule persists the schedule before checking/generating its seats")
                    .isNotNull();
        }

        @Test
        @DisplayName("updatePrice throws ResourceNotFoundException for an unknown schedule")
        void updatePriceMissing() {
            when(scheduleRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updatePrice(99, 25.0))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("updatePrice rejects a non-positive price")
        void updatePriceNegative() {
            Schedule schedule = TestData.schedule(movie, theater);
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));

            assertThatThrownBy(() -> service.updatePrice(1, -5.0))
                    .isInstanceOf(InvalidEntityException.class);
        }

        @Test
        @DisplayName("updateTheater throws ResourceNotFoundException for an unknown schedule")
        void updateTheaterMissing() {
            when(scheduleRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateTheater(99, theater))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("changeTime rejects an end time before the start time")
        void changeTimeInvalid() {
            Schedule schedule = TestData.schedule(movie, theater);
            LocalDateTime start = LocalDate.now().plusDays(2).atTime(12, 0);
            LocalDateTime end = LocalDate.now().plusDays(2).atTime(9, 0);
            when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));

            assertThatThrownBy(() -> service.changeTime(1, start, end))
                    .isInstanceOf(InvalidEntityException.class);
        }
    }
}
