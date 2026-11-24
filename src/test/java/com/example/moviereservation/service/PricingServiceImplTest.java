package com.example.moviereservation.service;

import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.Seat;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.enums.SeatClass;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PricingServiceImpl")
class PricingServiceImplTest {

    private final PricingServiceImpl service = new PricingServiceImpl();

    private final Theater theater = TestData.theater();
    private final Schedule schedule = TestData.schedule(TestData.movie(), theater);

    private ScheduleSeat seatOf(SeatClass seatClass) {
        Seat seat = TestData.seat(1, 'A', 1, seatClass, theater);
        return TestData.availableSeat(1, schedule, seat);
    }

    @Nested
    @DisplayName("[AS-IS] current behavior")
    class AsIs {

        @Test
        @DisplayName("charges the schedule base price for a STANDARD seat")
        void standardSeat() {
            assertThat(service.calculatePrice(List.of(seatOf(SeatClass.STANDARD)), schedule))
                    .isEqualTo(10.0);
        }

        @Test
        @DisplayName("charges base price * 1.3 for a PREMIUM seat")
        void premiumSeat() {
            assertThat(service.calculatePrice(List.of(seatOf(SeatClass.PREMIUM)), schedule))
                    .isEqualTo(13.0);
        }

        @Test
        @DisplayName("charges base price * 1.6 for a VIP seat")
        void vipSeat() {
            assertThat(service.calculatePrice(List.of(seatOf(SeatClass.VIP)), schedule))
                    .isEqualTo(16.0);
        }

        @Test
        @DisplayName("sums the price of mixed seat classes")
        void mixedSeats() {
            assertThat(service.calculatePrice(List.of(
                    seatOf(SeatClass.STANDARD),
                    seatOf(SeatClass.PREMIUM),
                    seatOf(SeatClass.VIP)), schedule))
                    .isEqualTo(39.0);
        }

        @Test
        @DisplayName("returns 0 when no seats are passed")
        void emptyList() {
            assertThat(service.calculatePrice(List.of(), schedule)).isZero();
        }

        @Test
        @DisplayName("throws NullPointerException when a seat has no class")
        void nullSeatClass() {
            Seat seat = TestData.seat(1, 'A', 1, null, theater);
            ScheduleSeat scheduleSeat = TestData.availableSeat(1, schedule, seat);

            assertThatThrownBy(() -> service.calculatePrice(List.of(scheduleSeat), schedule))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("[EXPECTED] correct behavior")
    @Tag("expected")
    class Expected {

        @Test
        @DisplayName("rejects a seat without a class instead of throwing a NullPointerException")
        void nullSeatClassShouldBeRejected() {
            Seat seat = TestData.seat(1, 'A', 1, null, theater);
            ScheduleSeat scheduleSeat = TestData.availableSeat(1, schedule, seat);

            assertThatThrownBy(() -> service.calculatePrice(List.of(scheduleSeat), schedule))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
