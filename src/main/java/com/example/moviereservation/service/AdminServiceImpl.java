package com.example.moviereservation.service;

import com.example.moviereservation.dto.response.AdminMovieResponseDto;
import com.example.moviereservation.dto.response.AdminReservationResponseDto;
import com.example.moviereservation.dto.response.AdminScheduleResponseDto;
import com.example.moviereservation.dto.response.AdminScheduleSeatResponseDto;
import com.example.moviereservation.dto.response.AdminSummaryResponseDto;
import com.example.moviereservation.dto.response.AdminTheaterResponseDto;
import com.example.moviereservation.dto.response.UserResponseDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.entity.ReservationSeat;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.Seat;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.ReservationStatus;
import com.example.moviereservation.repository.MovieRepository;
import com.example.moviereservation.repository.ReservationRepository;
import com.example.moviereservation.repository.ReservationSeatRepository;
import com.example.moviereservation.repository.ScheduleRepository;
import com.example.moviereservation.repository.ScheduleSeatRepository;
import com.example.moviereservation.repository.TheaterRepository;
import com.example.moviereservation.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final TheaterRepository theaterRepository;
    private final MovieRepository movieRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    @Override
    public List<UserResponseDto> getUsers() {
        return userRepository.findAll(Sort.by("id")).stream()
                .map(this::toUserDto)
                .toList();
    }

    @Override
    public List<AdminTheaterResponseDto> getTheaters() {
        return theaterRepository.findAll(Sort.by("id")).stream()
                .map(theater -> new AdminTheaterResponseDto(
                        theater.getId(),
                        theater.getName(),
                        theater.getCity(),
                        theater.getTotalSeats()))
                .toList();
    }

    @Override
    public List<AdminMovieResponseDto> getMovies() {
        return movieRepository.findAll(Sort.by("id")).stream()
                .map(movie -> new AdminMovieResponseDto(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getCategory(),
                        movie.getRating(),
                        movie.getReleaseDate(),
                        movie.getDuration(),
                        movie.getDescription()))
                .toList();
    }

    @Override
    public List<AdminScheduleResponseDto> getSchedules() {
        return scheduleRepository.findAllForAdmin().stream()
                .map(this::toScheduleDto)
                .toList();
    }

    @Override
    public List<AdminScheduleSeatResponseDto> getScheduleSeats() {
        Map<Integer, ReservationSeat> reservationSeatByScheduleSeatId =
                reservationSeatRepository.findAllForAdmin().stream()
                        .filter(links -> links.getScheduleSeat() != null)
                        .collect(Collectors.toMap(
                                links -> links.getScheduleSeat().getId(),
                                links -> links,
                                (first, second) -> first));

        return scheduleSeatRepository.findAllForAdmin().stream()
                .map(seat -> toScheduleSeatDto(seat, reservationSeatByScheduleSeatId.get(seat.getId())))
                .toList();
    }

    @Override
    public List<AdminReservationResponseDto> getReservations() {
        Map<Integer, List<ReservationSeat>> reservationSeatsByReservationId =
                reservationSeatRepository.findAllForAdmin().stream()
                        .filter(links -> links.getReservation() != null)
                        .collect(Collectors.groupingBy(links -> links.getReservation().getId()));

        return reservationRepository.findAllForAdmin().stream()
                .map(reservation -> toReservationDto(
                        reservation,
                        reservationSeatsByReservationId.getOrDefault(reservation.getId(), List.of())))
                .toList();
    }

    @Override
    public AdminSummaryResponseDto getSummary() {
        return new AdminSummaryResponseDto(
                userRepository.count(),
                theaterRepository.count(),
                movieRepository.count(),
                scheduleRepository.count(),
                scheduleSeatRepository.count(),
                reservationRepository.count(),
                reservationRepository.countByStatus(ReservationStatus.PENDING));
    }

    private UserResponseDto toUserDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCity(),
                user.getRole());
    }

    private AdminScheduleResponseDto toScheduleDto(Schedule schedule) {
        Movie movie = schedule.getMovie();
        Theater theater = schedule.getTheater();
        return new AdminScheduleResponseDto(
                schedule.getId(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getPrice(),
                movie == null ? null : movie.getId(),
                movie == null ? null : movie.getTitle(),
                theater == null ? null : theater.getId(),
                theater == null ? null : theater.getName(),
                theater == null ? null : theater.getCity());
    }

    private AdminScheduleSeatResponseDto toScheduleSeatDto(ScheduleSeat scheduleSeat,
                                                             ReservationSeat reservationSeat) {
        Schedule schedule = scheduleSeat.getSchedule();
        Movie movie = schedule == null ? null : schedule.getMovie();
        Theater theater = schedule == null ? null : schedule.getTheater();
        Seat seat = scheduleSeat.getSeat();
        Reservation reservation = reservationSeat == null ? null : reservationSeat.getReservation();

        return new AdminScheduleSeatResponseDto(
                scheduleSeat.getId(),
                scheduleSeat.getAvailable(),
                schedule == null ? null : schedule.getId(),
                movie == null ? null : movie.getTitle(),
                theater == null ? null : theater.getName(),
                schedule == null ? null : schedule.getDate(),
                schedule == null ? null : schedule.getStartTime(),
                seat == null ? null : seat.getId(),
                seat == null || seat.getRowNum() == null ? null : String.valueOf(seat.getRowNum()),
                seat == null ? null : seat.getSeatNum(),
                seat == null ? null : seat.getSeatClass(),
                seatLabel(seat),
                reservation == null ? null : reservation.getId(),
                reservation == null ? null : reservation.getStatus());
    }

    private AdminReservationResponseDto toReservationDto(Reservation reservation,
                                                          List<ReservationSeat> reservationSeats) {
        User user = reservation.getUser();
        Schedule schedule = reservation.getSchedule();
        Movie movie = schedule == null ? null : schedule.getMovie();
        Theater theater = schedule == null ? null : schedule.getTheater();

        List<Integer> seatIds = reservationSeats.stream()
                .map(ReservationSeat::getScheduleSeat)
                .filter(Objects::nonNull)
                .map(ScheduleSeat::getId)
                .filter(Objects::nonNull)
                .toList();
        List<String> seatLabels = reservationSeats.stream()
                .map(ReservationSeat::getScheduleSeat)
                .filter(Objects::nonNull)
                .map(ScheduleSeat::getSeat)
                .map(this::seatLabel)
                .toList();

        return new AdminReservationResponseDto(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getPaymentMethod(),
                reservation.getTotalPrice(),
                reservation.getBookingDate(),
                user == null ? null : user.getId(),
                userName(user),
                user == null ? null : user.getEmail(),
                schedule == null ? null : schedule.getId(),
                movie == null ? null : movie.getTitle(),
                theater == null ? null : theater.getName(),
                schedule == null ? null : schedule.getDate(),
                schedule == null ? null : schedule.getStartTime(),
                schedule == null ? null : schedule.getEndTime(),
                seatIds,
                seatLabels);
    }

    private String userName(User user) {
        if (user == null) {
            return "Unknown user";
        }
        String first = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();
        String name = (first + " " + last).trim();
        return name.isEmpty() ? user.getEmail() : name;
    }

    private String seatLabel(Seat seat) {
        if (seat == null) {
            return "-";
        }
        String row = seat.getRowNum() == null ? "?" : String.valueOf(seat.getRowNum());
        String number = seat.getSeatNum() == null ? "?" : String.valueOf(seat.getSeatNum());
        String seatClass = seat.getSeatClass() == null ? "" : " (" + seat.getSeatClass() + ")";
        return row + number + seatClass;
    }
}
