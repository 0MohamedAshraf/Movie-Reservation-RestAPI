package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.UserRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.dto.response.UserResponseDto;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.mapper.ReservationMapper;
import com.example.moviereservation.mapper.UserMapper;
import com.example.moviereservation.repository.ReservationRepository;
import com.example.moviereservation.repository.ReservationSeatRepository;
import com.example.moviereservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository repository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final UserMapper mapper;
    private final ReservationMapper reservationMapper;


    @Override
    public UserResponseDto getById(Integer id) {
        User user = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with Id: " + id + " Not Found")
        );
        return mapper.toDto(user);
    }

    @Override
    public UserResponseDto update(Integer userId, UserRequestDto request) {
        User user = repository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User with Id: " + userId + " Not Found")
        );

        User updatedUser = mapper.toEntity(request);
        updatedUser.setId(userId);
        repository.save(updatedUser);

        return mapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = repository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User with Id: " + userId + " Not Found")
        );
        repository.deleteById(userId);
    }

    @Override
    public List<ReservationResponseDto> getReservations(Integer userId) {
        User user = repository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User with Id: " + userId + " Not Found")
        );

        return reservationRepository.findByUserId(userId)
                .stream()
                .map(reservation -> {
                    ReservationResponseDto dto = reservationMapper.toDto(reservation);
                    dto.setReservedSeatsIds(
                            reservationSeatRepository.getReservedSeatsIds(reservation.getId())
                    );
                    return dto;
                })
                .toList();
    }
}
