package com.example.moviereservation.support;

import com.example.moviereservation.dto.request.LoginRequest;
import com.example.moviereservation.dto.request.MovieRequestDto;
import com.example.moviereservation.dto.request.RegisterRequest;
import com.example.moviereservation.dto.request.ReservationRequestDto;
import com.example.moviereservation.dto.request.ScheduleRequestDto;
import com.example.moviereservation.dto.request.TheaterRequestDto;
import com.example.moviereservation.dto.request.UserRequestDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.Seat;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.PaymentMethod;
import com.example.moviereservation.enums.ReservationStatus;
import com.example.moviereservation.enums.SeatClass;
import com.example.moviereservation.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Small factories used by the unit tests so each test only states the data that
 * actually matters for the scenario it is covering.
 */
public final class TestData {

    private TestData() {
    }

    // ---------------------------------------------------------------------
    // Entities
    // ---------------------------------------------------------------------

    public static Movie movie() {
        return movie(1, "Inception", 8.8f, LocalDate.of(2010, 7, 16), "Sci-Fi", 148f);
    }

    public static Movie movie(Integer id, String title, Float rating, LocalDate releaseDate,
                              String category, Float duration) {
        return new Movie(id, title, "A movie about " + title, releaseDate, rating, category, duration);
    }

    public static Movie movie(String title, Float rating, LocalDate releaseDate,
                              String category, Float duration) {
        return movie(null, title, rating, releaseDate, category, duration);
    }

    /**
     * A movie without an id, i.e. the state expected by {@code save()} for a new
     * row. Repository tests must use this variant: {@code save()} merges (and thus
     * fails with a stale/optimistic-lock error) when the id is already set.
     */
    public static Movie newMovie() {
        return movie(null, "Inception", 8.8f, LocalDate.of(2010, 7, 16), "Sci-Fi", 148f);
    }

    public static Theater theater() {
        return theater(1, "Downtown Cinema", "Cairo", 100);
    }

    /** A theater without an id, the counterpart of {@link #newMovie()}. */
    public static Theater newTheater() {
        return theater(null, "Downtown Cinema", "Cairo", 100);
    }

    public static Theater theater(Integer id, String name, String city, Integer totalSeats) {
        return new Theater(id, name, city, totalSeats);
    }

    public static Schedule schedule(Movie movie, Theater theater) {
        return schedule(1, tomorrowAt(18), tomorrowAt(20), LocalDate.now().plusDays(1),
                10.0, movie, theater);
    }

    public static Schedule schedule(Integer id, LocalDateTime start, LocalDateTime end,
                                    LocalDate date, Double price, Movie movie, Theater theater) {
        return new Schedule(id, start, end, date, price, movie, theater);
    }

    public static Seat seat(Integer id, char row, int number, SeatClass seatClass, Theater theater) {
        return new Seat(id, row, number, seatClass, theater);
    }

    public static Seat standardSeat(Theater theater) {
        return seat(1, 'A', 1, SeatClass.STANDARD, theater);
    }

    public static Seat premiumSeat(Theater theater) {
        return seat(2, 'A', 2, SeatClass.PREMIUM, theater);
    }

    public static Seat vipSeat(Theater theater) {
        return seat(3, 'A', 3, SeatClass.VIP, theater);
    }

    public static ScheduleSeat scheduleSeat(Integer id, boolean available, Schedule schedule, Seat seat) {
        return new ScheduleSeat(id, available, schedule, seat);
    }

    public static ScheduleSeat availableSeat(Integer id, Schedule schedule, Seat seat) {
        return scheduleSeat(id, true, schedule, seat);
    }

    public static ScheduleSeat unavailableSeat(Integer id, Schedule schedule, Seat seat) {
        return scheduleSeat(id, false, schedule, seat);
    }

    public static User user(Integer id, String email, UserRole role) {
        return new User(id, "Jane", "Doe", email, "encoded-password", "Cairo", role);
    }

    public static User user(Integer id, String email) {
        return user(id, email, UserRole.CUSTOMER);
    }

    public static Reservation reservation(Integer id, ReservationStatus status, User user, Schedule schedule) {
        return new Reservation(id, PaymentMethod.CREDIT_CARD, 10f, LocalDate.now(), status, user, schedule);
    }

    // ---------------------------------------------------------------------
    // Request DTOs
    // ---------------------------------------------------------------------

    public static MovieRequestDto movieRequest() {
        return movieRequest("Inception", 4.5f, 148f);
    }

    public static MovieRequestDto movieRequest(String title, Float rating, Float duration) {
        return new MovieRequestDto(title, "A movie about " + title, rating,
                LocalDate.of(2010, 7, 16), "Sci-Fi", duration);
    }

    public static TheaterRequestDto theaterRequest() {
        return theaterRequest("Downtown Cinema", "Cairo", 100);
    }

    public static TheaterRequestDto theaterRequest(String name, String city, Integer totalSeats) {
        return new TheaterRequestDto(name, city, totalSeats);
    }

    public static ScheduleRequestDto scheduleRequest(Integer movieId, Integer theaterId) {
        return scheduleRequest(tomorrowAt(18), tomorrowAt(20), LocalDate.now().plusDays(1),
                10.0, movieId, theaterId);
    }

    public static ScheduleRequestDto scheduleRequest(LocalDateTime start, LocalDateTime end,
                                                     LocalDate date, Double price,
                                                     Integer movieId, Integer theaterId) {
        return new ScheduleRequestDto(start, end, date, price, movieId, theaterId);
    }

    public static ReservationRequestDto reservationRequest(Integer userId, Integer scheduleId,
                                                           List<Integer> seatIds) {
        return new ReservationRequestDto(PaymentMethod.CREDIT_CARD, userId, scheduleId, seatIds);
    }

    public static UserRequestDto userRequest(String email, String password) {
        return new UserRequestDto("Jane", "Doe", email, password, "Cairo", UserRole.CUSTOMER);
    }

    public static RegisterRequest registerRequest(String email, String password) {
        return new RegisterRequest("Jane", "Doe", email, password, "Cairo", UserRole.CUSTOMER);
    }

    public static LoginRequest loginRequest(String email, String password) {
        return new LoginRequest(email, password);
    }

    private static LocalDateTime tomorrowAt(int hour) {
        return LocalDate.now().plusDays(1).atTime(hour, 0);
    }
}
