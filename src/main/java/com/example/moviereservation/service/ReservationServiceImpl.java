package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.ReservationRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.entity.*;
import com.example.moviereservation.exceptions.InvalidEntityException;
import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.mapper.ReservationMapper;
import com.example.moviereservation.repository.ReservationRepository;
import com.example.moviereservation.repository.ReservationSeatRepository;
import com.example.moviereservation.repository.ScheduleRepository;
import com.example.moviereservation.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper mapper;
    private final ReservationRepository repository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleSeatService scheduleSeatService;

    private void validateScheduleSeats(List<ScheduleSeat> seats,Integer scheduleId){
        seats.forEach(
            seat -> {
                if (!seat.getAvailable())
                    throw new IllegalArgumentException("Schedule Seat with id: " + seat.getId() + " is already Reserved");
                if (!seat.getSchedule().getId().equals(scheduleId))
                    throw new InvalidEntityException("Schedule Seat with id: " + seat.getId() + " doesn't belong to schedule: " + scheduleId);

            }
            );
    }

    @Transactional
    @Override
    public ReservationResponseDto reserveSchedule(ReservationRequestDto request) {
        Schedule schedule = scheduleRepository.findById(request.getScheduleId()).orElseThrow(
                () -> new ResourceNotFoundException("Schedule with Id: " + request.getScheduleId() + "Not Found")
        );
        User user = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("User with Id: " + request.getUserId() + "Not Found")
        );
        List<ScheduleSeat> scheduleSeats = scheduleSeatService.getAllByIds(
                request.getScheduleSeatIds().stream()
                        .distinct()
                        .toList());

        if(request.getScheduleSeatIds().size() != scheduleSeats.size())
            throw new ResourceNotFoundException("Schedule Seats not Found");
        validateScheduleSeats(scheduleSeats,schedule.getId());

        // move it later to a separate method to add tax and for each seat class pricing
        float totalPrice = (float) (schedule.getPrice() * request.getScheduleSeatIds().size());

        Reservation reservation = mapper.toEntity(request);
        reservation.setSchedule(schedule);
        reservation.setUser(user);
        reservation.setTotalPrice(totalPrice);
        reservation.setBookingDate(LocalDate.now());

        List<ReservationSeat> reservationSeats = new ArrayList<>();
        Reservation saved = repository.save(reservation);
        for(ScheduleSeat seat : scheduleSeats){
            seat.setAvailable(false);
            reservationSeats.add(new ReservationSeat(null, saved, seat));
        }
        reservationSeatRepository.saveAll(reservationSeats);
        scheduleSeatService.saveAll(scheduleSeats);

        ReservationResponseDto dto = mapper.toDto(saved);

        dto.setReservedSeatsIds(reservationSeatRepository.getReservedSeatsIds(dto.getId()));
        return dto;
    }

    @Override
    public void cancelReservation(Integer reservationId) {

    }
}
