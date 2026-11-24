package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.TheaterRequestDto;
import com.example.moviereservation.dto.response.TheaterResponseDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TheaterMapper")
class TheaterMapperTest {

    private final TheaterMapper mapper = new TheaterMapper(new TheaterScheduleMapper());

    @Test
    @DisplayName("entityToDto maps the theater and an empty schedule list when schedules are null")
    void entityToDtoWithoutSchedules() {
        Theater theater = TestData.theater();

        TheaterResponseDto dto = mapper.entityToDto(theater);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Downtown Cinema");
        assertThat(dto.getCity()).isEqualTo("Cairo");
        assertThat(dto.getTotalSeats()).isEqualTo(100);
        assertThat(dto.getUpcomingSchedules()).isEmpty();
    }

    @Test
    @DisplayName("entityToDto maps the theater schedules")
    void entityToDtoWithSchedules() {
        Theater theater = TestData.theater();
        Movie movie = TestData.movie();
        Schedule schedule = TestData.schedule(4, LocalDate.now().atTime(18, 0),
                LocalDate.now().atTime(20, 0), LocalDate.now(), 10.0, movie, theater);
        theater.setScheduleList(List.of(schedule));

        TheaterResponseDto dto = mapper.entityToDto(theater);

        assertThat(dto.getUpcomingSchedules()).hasSize(1);
        assertThat(dto.getUpcomingSchedules().get(0).getId()).isEqualTo(4);
        assertThat(dto.getUpcomingSchedules().get(0).getMovieTitle()).isEqualTo("Inception");
    }

    @Test
    @DisplayName("dtoToEntity maps every field and leaves the id null")
    void dtoToEntity() {
        TheaterRequestDto request = TestData.theaterRequest("New", "Giza", 60);

        Theater theater = mapper.dtoToEntity(request);

        assertThat(theater.getId()).isNull();
        assertThat(theater.getName()).isEqualTo("New");
        assertThat(theater.getCity()).isEqualTo("Giza");
        assertThat(theater.getTotalSeats()).isEqualTo(60);
    }
}
