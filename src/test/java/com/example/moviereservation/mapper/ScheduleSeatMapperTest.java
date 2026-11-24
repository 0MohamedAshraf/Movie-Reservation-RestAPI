package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.ScheduleSeatRequestDto;
import com.example.moviereservation.dto.response.ScheduleSeatResponseDto;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.ScheduleSeat;
import com.example.moviereservation.entity.Seat;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScheduleSeatMapper")
class ScheduleSeatMapperTest {

    private final ScheduleSeatMapper mapper = new ScheduleSeatMapper();

    @Test
    @DisplayName("dtoToEntity maps the availability and both relations")
    void dtoToEntity() {
        Theater theater = TestData.theater();
        Seat seat = TestData.standardSeat(theater);
        Schedule schedule = TestData.schedule(TestData.movie(), theater);
        ScheduleSeatRequestDto request = new ScheduleSeatRequestDto(1, false, 1);

        ScheduleSeat scheduleSeat = mapper.dtoToEntity(request, seat, schedule);

        assertThat(scheduleSeat.getId()).isNull();
        assertThat(scheduleSeat.getAvailable()).isFalse();
        assertThat(scheduleSeat.getSeat()).isSameAs(seat);
        assertThat(scheduleSeat.getSchedule()).isSameAs(schedule);
    }

    @Test
    @DisplayName("entityToDto maps the ids and availability")
    void entityToDto() {
        Theater theater = TestData.theater();
        Seat seat = TestData.standardSeat(theater);
        Schedule schedule = TestData.schedule(TestData.movie(), theater);
        schedule.setId(2);
        ScheduleSeat scheduleSeat = TestData.availableSeat(10, schedule, seat);

        ScheduleSeatResponseDto dto = mapper.entityToDto(scheduleSeat);

        assertThat(dto.getId()).isEqualTo(10);
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getScheduleId()).isEqualTo(2);
        assertThat(dto.getSeatId()).isEqualTo(1);
    }
}
