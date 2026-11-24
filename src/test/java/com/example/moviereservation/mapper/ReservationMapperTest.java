package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.ReservationRequestDto;
import com.example.moviereservation.dto.response.ReservationResponseDto;
import com.example.moviereservation.entity.Reservation;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.PaymentMethod;
import com.example.moviereservation.enums.ReservationStatus;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReservationMapper")
class ReservationMapperTest {

    private final ReservationMapper mapper = new ReservationMapper();

    @Test
    @DisplayName("toDto maps the reservation, user id and schedule id")
    void toDto() {
        User user = TestData.user(3, "jane@example.com");
        Schedule schedule = TestData.schedule(TestData.movie(), TestData.theater());
        schedule.setId(4);
        Reservation reservation = new Reservation(8, PaymentMethod.WALLET, 25.5f,
                LocalDate.of(2026, 1, 2), ReservationStatus.CONFIRMED, user, schedule);

        ReservationResponseDto dto = mapper.toDto(reservation);

        assertThat(dto.getId()).isEqualTo(8);
        assertThat(dto.getPaymentMethod()).isEqualTo(PaymentMethod.WALLET);
        assertThat(dto.getTotalPrice()).isEqualTo(25.5f);
        assertThat(dto.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(dto.getBookingDate()).isEqualTo(LocalDate.of(2026, 1, 2));
        assertThat(dto.getUserId()).isEqualTo(3);
        assertThat(dto.getScheduleId()).isEqualTo(4);
        assertThat(dto.getReservedSeatsIds()).isEmpty();
    }

    @Test
    @DisplayName("toEntity maps the payment method and starts in PENDING")
    void toEntity() {
        ReservationRequestDto request = TestData.reservationRequest(1, 1, List.of(10, 11));

        Reservation reservation = mapper.toEntity(request);

        assertThat(reservation.getId()).isNull();
        assertThat(reservation.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(reservation.getUser()).isNull();
        assertThat(reservation.getSchedule()).isNull();
        assertThat(reservation.getTotalPrice()).isNull();
        assertThat(reservation.getBookingDate()).isNull();
    }
}
