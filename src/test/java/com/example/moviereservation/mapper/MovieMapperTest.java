package com.example.moviereservation.mapper;

import com.example.moviereservation.dto.request.MovieRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.entity.Schedule;
import com.example.moviereservation.entity.Theater;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MovieMapper")
class MovieMapperTest {

    private final ScheduleMapper scheduleMapper = new ScheduleMapper();
    private final MovieMapper mapper = new MovieMapper(scheduleMapper);

    @Test
    @DisplayName("entityToDto maps the movie and an empty schedule list when schedules are null")
    void entityToDtoWithoutSchedules() {
        Movie movie = TestData.movie();

        MovieResponseDto dto = mapper.entityToDto(movie);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getTitle()).isEqualTo("Inception");
        assertThat(dto.getDescription()).isEqualTo("A movie about Inception");
        assertThat(dto.getRating()).isEqualTo(8.8f);
        assertThat(dto.getReleaseDate()).isEqualTo(LocalDate.of(2010, 7, 16));
        assertThat(dto.getCategory()).isEqualTo("Sci-Fi");
        assertThat(dto.getDuration()).isEqualTo(148f);
        assertThat(dto.getSchedules()).isEmpty();
    }

    @Test
    @DisplayName("entityToDto maps nested schedules")
    void entityToDtoWithSchedules() {
        Movie movie = TestData.movie();
        Theater theater = TestData.theater();
        Schedule schedule = TestData.schedule(3, LocalDate.now().atTime(18, 0),
                LocalDate.now().atTime(20, 0), LocalDate.now(), 12.5, movie, theater);
        movie.setSchedules(List.of(schedule));

        MovieResponseDto dto = mapper.entityToDto(movie);

        assertThat(dto.getSchedules()).hasSize(1);
        assertThat(dto.getSchedules().get(0).getId()).isEqualTo(3);
        assertThat(dto.getSchedules().get(0).getMovieId()).isEqualTo(1);
        assertThat(dto.getSchedules().get(0).getTheaterId()).isEqualTo(1);
    }

    @Test
    @DisplayName("dtoToEntity maps every field and leaves the id null")
    void dtoToEntity() {
        MovieRequestDto request = TestData.movieRequest("Interstellar", 4.5f, 169f);

        Movie movie = mapper.dtoToEntity(request);

        assertThat(movie.getId()).isNull();
        assertThat(movie.getTitle()).isEqualTo("Interstellar");
        assertThat(movie.getDescription()).isEqualTo("A movie about Interstellar");
        assertThat(movie.getRating()).isEqualTo(4.5f);
        assertThat(movie.getReleaseDate()).isEqualTo(LocalDate.of(2010, 7, 16));
        assertThat(movie.getCategory()).isEqualTo("Sci-Fi");
        assertThat(movie.getDuration()).isEqualTo(169f);
    }
}
