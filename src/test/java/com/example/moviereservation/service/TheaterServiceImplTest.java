package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.TheaterRequestDto;
import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.dto.response.TheaterScheduleDto;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.exceptions.EntityAlreadyExistsException;
import com.example.moviereservation.exceptions.InvalidEntityException;
import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.mapper.TheaterMapper;
import com.example.moviereservation.mapper.TheaterScheduleMapper;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TheaterServiceImpl")
class TheaterServiceImplTest {

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private TheaterMapper mapper;

    @Mock
    private TheaterScheduleMapper theaterScheduleMapper;

    @InjectMocks
    private TheaterServiceImpl service;

    private final Theater theater = TestData.theater();
    private final TheaterResponseDto response = new TheaterResponseDto();

    @Nested
    @DisplayName("[AS-IS] current behavior")
    class AsIs {

        @Test
        @DisplayName("getAll maps every theater")
        void getAll() {
            when(theaterRepository.findAll()).thenReturn(List.of(theater));
            when(mapper.entityToDto(theater)).thenReturn(response);

            assertThat(service.getAll()).containsExactly(response);
        }

        @Test
        @DisplayName("getTheaterById returns the mapped theater")
        void getTheaterById() {
            when(theaterRepository.findById(1)).thenReturn(Optional.of(theater));
            when(mapper.entityToDto(theater)).thenReturn(response);

            assertThat(service.getTheaterById(1)).isSameAs(response);
        }

        @Test
        @DisplayName("getTheaterById throws ResourceNotFoundException when missing")
        void getTheaterByIdMissing() {
            when(theaterRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getTheaterById(99))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("getTheaterByName returns the mapped theater")
        void getTheaterByName() {
            when(theaterRepository.findByName("Downtown Cinema")).thenReturn(theater);
            when(mapper.entityToDto(theater)).thenReturn(response);

            assertThat(service.getTheaterByName("Downtown Cinema")).isSameAs(response);
        }

        @Test
        @DisplayName("getTheaterByName throws ResourceNotFoundException when missing")
        void getTheaterByNameMissing() {
            when(theaterRepository.findByName("Unknown")).thenReturn(null);

            assertThatThrownBy(() -> service.getTheaterByName("Unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("addTheater persists a valid theater")
        void addTheater() {
            TheaterRequestDto request = TestData.theaterRequest();
            when(theaterRepository.existsByNameAndCity(request.getName(), request.getCity()))
                    .thenReturn(false);
            when(mapper.dtoToEntity(request)).thenReturn(theater);
            when(theaterRepository.save(theater)).thenReturn(theater);
            when(mapper.entityToDto(theater)).thenReturn(response);

            assertThat(service.addTheater(request)).isSameAs(response);
            verify(theaterRepository).save(theater);
        }

        @Test
        @DisplayName("addTheater rejects a theater with no seats")
        void addTheaterNoSeats() {
            assertThatThrownBy(() -> service.addTheater(TestData.theaterRequest("T", "C", 0)))
                    .isInstanceOf(InvalidEntityException.class);

            verifyNoInteractions(theaterRepository);
        }

        @Test
        @DisplayName("addTheater rejects a duplicate name/city pair")
        void addTheaterDuplicate() {
            TheaterRequestDto request = TestData.theaterRequest();
            when(theaterRepository.existsByNameAndCity(request.getName(), request.getCity()))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.addTheater(request))
                    .isInstanceOf(EntityAlreadyExistsException.class);

            verify(theaterRepository, never()).save(any());
        }

        @Test
        @DisplayName("filterTheatersByCity maps matching theaters")
        void filterByCity() {
            when(theaterRepository.findByCity("Cairo")).thenReturn(List.of(theater));
            when(mapper.entityToDto(theater)).thenReturn(response);

            assertThat(service.filterTheatersByCity("Cairo")).containsExactly(response);
        }

        @Test
        @DisplayName("filterTheatersByCity returns an empty list when nothing matches")
        void filterByCityEmpty() {
            when(theaterRepository.findByCity("Nowhere")).thenReturn(List.of());

            assertThat(service.filterTheatersByCity("Nowhere")).isEmpty();
        }

        @Test
        @DisplayName("getTheaterSchedules throws ResourceNotFoundException when the theater is missing")
        void getTheaterSchedulesMissingTheater() {
            when(theaterRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getTheaterSchedules(99))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("getTheaterSchedules returns an empty list when the theater has no schedules")
        void getTheaterSchedulesEmpty() {
            theater.setScheduleList(null);
            when(theaterRepository.findById(1)).thenReturn(Optional.of(theater));

            assertThat(service.getTheaterSchedules(1)).isEmpty();
        }

        @Test
        @DisplayName("getTheaterSchedules maps every schedule of the theater (including past ones)")
        void getTheaterSchedulesReturnsEverything() {
            Schedule past = scheduleWithDate(LocalDate.now().minusDays(2));
            Schedule future = scheduleWithDate(LocalDate.now().plusDays(2));
            theater.setScheduleList(List.of(past, future));
            TheaterScheduleDto pastDto = new TheaterScheduleDto();
            TheaterScheduleDto futureDto = new TheaterScheduleDto();
            when(theaterRepository.findById(1)).thenReturn(Optional.of(theater));
            lenient().when(theaterScheduleMapper.scheduleToDto(past)).thenReturn(pastDto);
            lenient().when(theaterScheduleMapper.scheduleToDto(future)).thenReturn(futureDto);

            assertThat(service.getTheaterSchedules(1)).containsExactlyInAnyOrder(pastDto, futureDto);
        }

        @Test
        @DisplayName("updateTheater throws ResourceNotFoundException when missing")
        void updateTheaterMissing() {
            when(theaterRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateTheater(TestData.theaterRequest(), 99))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("updateTheater persists the new values with the given id")
        void updateTheater() {
            TheaterRequestDto request = TestData.theaterRequest("New Name", "Giza", 50);
            Theater updated = TestData.theater(null, "New Name", "Giza", 50);
            when(theaterRepository.findById(1)).thenReturn(Optional.of(theater));
            when(mapper.dtoToEntity(request)).thenReturn(updated);
            when(theaterRepository.save(updated)).thenReturn(updated);
            when(mapper.entityToDto(updated)).thenReturn(response);

            assertThat(service.updateTheater(request, 1)).isSameAs(response);
            assertThat(updated.getId()).isEqualTo(1);
        }

        @Test
        @DisplayName("updateTheater validates the seat count first")
        void updateTheaterInvalidSeats() {
            assertThatThrownBy(() -> service.updateTheater(TestData.theaterRequest("T", "C", -1), 1))
                    .isInstanceOf(InvalidEntityException.class);

            verifyNoInteractions(theaterRepository);
        }

        @Test
        @DisplayName("deleteTheater removes an existing theater")
        void deleteTheater() {
            when(theaterRepository.findById(1)).thenReturn(Optional.of(theater));
            when(mapper.entityToDto(theater)).thenReturn(response);

            service.deleteTheater(1);

            verify(theaterRepository).deleteById(1);
        }

        @Test
        @DisplayName("deleteTheater throws ResourceNotFoundException when missing")
        void deleteTheaterMissing() {
            when(theaterRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteTheater(99))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(theaterRepository, never()).deleteById(99);
        }
    }

    @Nested
    @DisplayName("[EXPECTED] correct behavior")
    @Tag("expected")
    class Expected {

        @Test
        @DisplayName("getTheaterSchedules only returns upcoming schedules")
        void getTheaterSchedulesShouldSkipPastSchedules() {
            Schedule past = scheduleWithDate(LocalDate.now().minusDays(2));
            Schedule future = scheduleWithDate(LocalDate.now().plusDays(2));
            theater.setScheduleList(List.of(past, future));
            TheaterScheduleDto pastDto = new TheaterScheduleDto();
            TheaterScheduleDto futureDto = new TheaterScheduleDto();
            when(theaterRepository.findById(1)).thenReturn(Optional.of(theater));
            lenient().when(theaterScheduleMapper.scheduleToDto(past)).thenReturn(pastDto);
            lenient().when(theaterScheduleMapper.scheduleToDto(future)).thenReturn(futureDto);

            assertThat(service.getTheaterSchedules(1)).containsExactly(futureDto);
        }
    }

    private Schedule scheduleWithDate(LocalDate date) {
        return TestData.schedule(1, date.atTime(18, 0), date.atTime(20, 0), date, 10.0,
                TestData.movie(), theater);
    }
}
