package com.example.moviereservation.repository;

import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = JpaTestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@DisplayName("MovieRepository (H2)")
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void seed() {
        movieRepository.saveAll(List.of(
                TestData.movie(null, "Inception", 8.8f, LocalDate.of(2010, 7, 16), "Sci-Fi", 148f),
                TestData.movie(null, "Interstellar", 8.7f, LocalDate.of(2014, 11, 7), "Sci-Fi", 169f),
                TestData.movie(null, "Parasite", 8.5f, LocalDate.of(2019, 5, 30), "Drama", 132f),
                TestData.movie(null, "Dune", 8.0f, LocalDate.of(2021, 10, 22), "Sci-Fi", 155f)
        ));
    }

    @Test
    @DisplayName("findByTitle returns the matching movie")
    void findByTitle() {
        Movie movie = movieRepository.findByTitle("Dune");

        assertThat(movie).isNotNull();
        assertThat(movie.getRating()).isEqualTo(8.0f);
    }

    @Test
    @DisplayName("existsByTitle reflects the stored titles")
    void existsByTitle() {
        assertThat(movieRepository.existsByTitle("Dune")).isTrue();
        assertThat(movieRepository.existsByTitle("Avatar")).isFalse();
    }

    @Test
    @DisplayName("findByCategory returns every movie in the category")
    void findByCategory() {
        assertThat(movieRepository.findByCategory("Sci-Fi"))
                .extracting(Movie::getTitle)
                .containsExactlyInAnyOrder("Inception", "Interstellar", "Dune");
    }

    @Test
    @DisplayName("findByReleaseDateAfter is strictly after the given date")
    void findByReleaseDateAfter() {
        assertThat(movieRepository.findByReleaseDateAfter(LocalDate.of(2014, 11, 7)))
                .extracting(Movie::getTitle)
                .containsExactlyInAnyOrder("Parasite", "Dune");
    }

    @Test
    @DisplayName("findByReleaseDateAfter returns nothing for a future date")
    void findByReleaseDateAfterFuture() {
        assertThat(movieRepository.findByReleaseDateAfter(LocalDate.of(2030, 1, 1))).isEmpty();
    }

    @Test
    @DisplayName("findTopRated orders by rating descending and honours the page size")
    void findTopRated() {
        Page<Movie> page = movieRepository.findTopRated((Pageable) PageRequest.of(0, 2));

        assertThat(page.getContent())
                .extracting(Movie::getTitle)
                .containsExactly("Inception", "Interstellar");
        assertThat(page.getTotalElements()).isEqualTo(4);
    }
}
