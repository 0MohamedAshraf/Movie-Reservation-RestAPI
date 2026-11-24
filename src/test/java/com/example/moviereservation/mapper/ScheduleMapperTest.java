package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.ScheduleRequestDto;
import com.example.moviereservation.dto.response.ScheduleResponseDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScheduleMapper")
class ScheduleMapperTest {

    private final ScheduleMapper mapper = new ScheduleMapper();

    @Test
    @DisplayName("entityToDto maps all fields and the related ids")
    void entityToDto() {
        Movie movie = TestData.movie();
        Theater theater = TestData.theater(2, "Other", "Giza", 60);
        LocalDateTime start = LocalDate.now().plusDays(1).atTime(18, 0);
        LocalDateTime end = LocalDate.now().plusDays(1).atTime(20, 0);
        Schedule schedule = TestData.schedule(7, start, end, LocalDate.now().plusDays(1),
                12.5, movie, theater);

        ScheduleResponseDto dto = mapper.entityToDto(schedule);

        assertThat(dto.getId()).isEqualTo(7);
        assertThat(dto.getStartTime()).isEqualTo(start);
        assertThat(dto.getEndTime()).isEqualTo(end);
        assertThat(dto.getDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(dto.getPrice()).isEqualTo(12.5);
        assertThat(dto.getMovieId()).isEqualTo(1);
        assertThat(dto.getTheaterId()).isEqualTo(2);
    }

    @Test
    @DisplayName("dtoToEntity maps all fields and leaves the id null")
    void dtoToEntity() {
        Movie movie = TestData.movie();
        Theater theater = TestData.theater();
        ScheduleRequestDto request = TestData.scheduleRequest(1, 1);

        Schedule schedule = mapper.dtoToEntity(request, movie, theater);

        assertThat(schedule.getId()).isNull();
        assertThat(schedule.getStartTime()).isEqualTo(request.getStartTime());
        assertThat(schedule.getEndTime()).isEqualTo(request.getEndTime());
        assertThat(schedule.getDate()).isEqualTo(request.getDate());
        assertThat(schedule.getPrice()).isEqualTo(request.getPrice());
        assertThat(schedule.getMovie()).isSameAs(movie);
        assertThat(schedule.getTheater()).isSameAs(theater);
    }
}
