package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.response.TheaterScheduleDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TheaterScheduleMapper")
class TheaterScheduleMapperTest {

    private final TheaterScheduleMapper mapper = new TheaterScheduleMapper();

    @Test
    @DisplayName("scheduleToDto maps the schedule and the movie title")
    void scheduleToDto() {
        Movie movie = TestData.movie();
        Theater theater = TestData.theater();
        Schedule schedule = TestData.schedule(9, LocalDate.now().atTime(18, 0),
                LocalDate.now().atTime(20, 0), LocalDate.now(), 15.5, movie, theater);

        TheaterScheduleDto dto = mapper.scheduleToDto(schedule);

        assertThat(dto.getId()).isEqualTo(9);
        assertThat(dto.getStartTime()).isEqualTo(schedule.getStartTime());
        assertThat(dto.getEndTime()).isEqualTo(schedule.getEndTime());
        assertThat(dto.getDate()).isEqualTo(schedule.getDate());
        assertThat(dto.getPrice()).isEqualTo(15.5);
        assertThat(dto.getMovieTitle()).isEqualTo("Inception");
    }
}
