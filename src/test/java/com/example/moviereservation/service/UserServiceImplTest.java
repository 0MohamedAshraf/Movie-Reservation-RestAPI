package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.UserRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.dto.response.UserResponseDto;
import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.ReservationStatus;
import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.mapper.ReservationMapper;
import com.example.moviereservation.mapper.UserMapper;
import com.example.moviereservation.repository.ReservationRepository;
import com.example.moviereservation.repository.ReservationSeatRepository;
import com.example.moviereservation.repository.UserRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationSeatRepository reservationSeatRepository;

    @Mock
    private UserMapper mapper;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private UserServiceImpl service;

    private final User user = TestData.user(1, "jane@example.com");

    @Nested
    @DisplayName("[AS-IS] current behavior")
    class AsIs {

        @Test
        @DisplayName("getById returns the mapped user")
        void getById() {
            UserResponseDto response = new UserResponseDto();
            when(repository.findById(1)).thenReturn(Optional.of(user));
            when(mapper.toDto(user)).thenReturn(response);

            assertThat(service.getById(1)).isSameAs(response);
        }

        @Test
        @DisplayName("getById throws ResourceNotFoundException when missing")
        void getByIdMissing() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(99))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("update replaces the user fields and keeps the id")
        void update() {
            UserRequestDto request = TestData.userRequest("new@example.com", "plain-password");
            User mapped = TestData.user(null, "new@example.com");
            mapped.setPassword("plain-password");
            UserResponseDto response = new UserResponseDto();
            when(repository.findById(1)).thenReturn(Optional.of(user));
            when(mapper.toEntity(request)).thenReturn(mapped);
            when(repository.save(mapped)).thenReturn(mapped);
            when(mapper.toDto(mapped)).thenReturn(response);

            assertThat(service.update(1, request)).isSameAs(response);
            assertThat(mapped.getId()).isEqualTo(1);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isEqualTo("plain-password");
        }

        @Test
        @DisplayName("update throws ResourceNotFoundException when the user is missing")
        void updateMissing() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(99, TestData.userRequest("x@example.com", "password1")))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deleteUser removes an existing user")
        void deleteUser() {
            when(repository.findById(1)).thenReturn(Optional.of(user));

            service.deleteUser(1);

            verify(repository).deleteById(1);
        }

        @Test
        @DisplayName("deleteUser throws ResourceNotFoundException when missing")
        void deleteUserMissing() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteUser(99))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository, never()).deleteById(99);
        }

        @Test
        @DisplayName("getReservations returns an empty list when the user has none")
        void getReservationsEmpty() {
            when(repository.findById(1)).thenReturn(Optional.of(user));
            when(reservationRepository.findByUserId(1)).thenReturn(List.of());

            assertThat(service.getReservations(1)).isEmpty();
        }

        @Test
        @DisplayName("getReservations maps every reservation and loads its seat ids")
        void getReservations() {
            Schedule schedule = TestData.schedule(TestData.movie(), TestData.theater());
            Reservation first = TestData.reservation(5, ReservationStatus.PENDING, user, schedule);
            Reservation second = TestData.reservation(6, ReservationStatus.CONFIRMED, user, schedule);
            ReservationResponseDto firstDto = new ReservationResponseDto();
            ReservationResponseDto secondDto = new ReservationResponseDto();
            when(repository.findById(1)).thenReturn(Optional.of(user));
            when(reservationRepository.findByUserId(1)).thenReturn(List.of(first, second));
            when(reservationMapper.toDto(first)).thenReturn(firstDto);
            when(reservationMapper.toDto(second)).thenReturn(secondDto);
            when(reservationSeatRepository.getReservedSeatsIds(5)).thenReturn(List.of(10, 11));
            when(reservationSeatRepository.getReservedSeatsIds(6)).thenReturn(List.of(12));

            assertThat(service.getReservations(1)).containsExactly(firstDto, secondDto);
            assertThat(firstDto.getReservedSeatsIds()).containsExactly(10, 11);
            assertThat(secondDto.getReservedSeatsIds()).containsExactly(12);
        }

        @Test
        @DisplayName("getReservations throws ResourceNotFoundException when the user is missing")
        void getReservationsMissingUser() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getReservations(99))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("[EXPECTED] correct behavior")
    @Tag("expected")
    class Expected {

        @Test
        @DisplayName("update must not persist the raw password")
        void updateShouldEncodePassword() {
            UserRequestDto request = TestData.userRequest("new@example.com", "plain-password");
            User mapped = TestData.user(null, "new@example.com");
            mapped.setPassword("plain-password");
            when(repository.findById(1)).thenReturn(Optional.of(user));
            when(mapper.toEntity(request)).thenReturn(mapped);
            when(repository.save(mapped)).thenReturn(mapped);
            when(mapper.toDto(mapped)).thenReturn(new UserResponseDto());

            service.update(1, request);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getPassword()).isNotEqualTo("plain-password");
        }
    }
}
