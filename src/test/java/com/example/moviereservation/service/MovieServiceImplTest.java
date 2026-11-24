package com.example.moviereservation.service;

import com.example.moviereservation.dto.request.MovieRequestDto;
import com.example.moviereservation.dto.response.MovieResponseDto;
import com.example.moviereservation.entity.Movie;
import com.example.moviereservation.exceptions.EntityAlreadyExistsException;
import com.example.moviereservation.exceptions.InvalidEntityException;
import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.mapper.MovieMapper;
import com.example.moviereservation.repository.MovieRepository;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MovieServiceImpl")
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieMapper mapper;

    @InjectMocks
    private MovieServiceImpl service;

    private final Movie movie = TestData.movie();
    private final MovieResponseDto response = new MovieResponseDto();

    @Nested
    @DisplayName("[AS-IS] current behavior")
    class AsIs {

        @Test
        @DisplayName("getAll forwards paging/sorting and maps the page")
        void getAll() {
            when(movieRepository.findAll(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(movie)));
            when(mapper.entityToDto(movie)).thenReturn(response);

            Page<MovieResponseDto> result = service.getAll(1, 5, Sort.Direction.DESC, "rating");

            assertThat(result.getContent()).containsExactly(response);
            ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
            verify(movieRepository).findAll(pageable.capture());
            assertThat(pageable.getValue().getPageNumber()).isEqualTo(1);
            assertThat(pageable.getValue().getPageSize()).isEqualTo(5);
            assertThat(pageable.getValue().getSort().getOrderFor("rating").getDirection())
                    .isEqualTo(Sort.Direction.DESC);
        }

        @Test
        @DisplayName("getMovieById returns the mapped movie")
        void getMovieByIdFound() {
            when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
            when(mapper.entityToDto(movie)).thenReturn(response);

            assertThat(service.getMovieById(1)).isSameAs(response);
        }

        @Test
        @DisplayName("getMovieById throws ResourceNotFoundException when missing")
        void getMovieByIdMissing() {
            when(movieRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getMovieById(99))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("getMovieByTitle returns the mapped movie")
        void getMovieByTitleFound() {
            when(movieRepository.findByTitle("Inception")).thenReturn(movie);
            when(mapper.entityToDto(movie)).thenReturn(response);

            assertThat(service.getMovieByTitle("Inception")).isSameAs(response);
        }

        @Test
        @DisplayName("getMovieByTitle throws ResourceNotFoundException when missing")
        void getMovieByTitleMissing() {
            when(movieRepository.findByTitle("Unknown")).thenReturn(null);

            assertThatThrownBy(() -> service.getMovieByTitle("Unknown"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Unknown");
        }

        @Test
        @DisplayName("addMovie persists a valid movie")
        void addMovie() {
            MovieRequestDto request = TestData.movieRequest();
            when(movieRepository.existsByTitle(request.getTitle())).thenReturn(false);
            when(mapper.dtoToEntity(request)).thenReturn(movie);
            when(movieRepository.save(movie)).thenReturn(movie);
            when(mapper.entityToDto(movie)).thenReturn(response);

            assertThat(service.addMovie(request)).isSameAs(response);
            verify(movieRepository).save(movie);
        }

        @Test
        @DisplayName("addMovie rejects a duplicate title without saving")
        void addMovieDuplicate() {
            MovieRequestDto request = TestData.movieRequest();
            when(movieRepository.existsByTitle(request.getTitle())).thenReturn(true);

            assertThatThrownBy(() -> service.addMovie(request))
                    .isInstanceOf(EntityAlreadyExistsException.class);

            verify(movieRepository, never()).save(any());
        }

        @Test
        @DisplayName("addMovie rejects a rating above 5")
        void addMovieRatingTooHigh() {
            assertThatThrownBy(() -> service.addMovie(TestData.movieRequest("X", 5.1f, 120f)))
                    .isInstanceOf(InvalidEntityException.class);
            verifyNoInteractions(movieRepository);
        }

        @Test
        @DisplayName("addMovie rejects a rating below 0")
        void addMovieRatingTooLow() {
            assertThatThrownBy(() -> service.addMovie(TestData.movieRequest("X", -0.1f, 120f)))
                    .isInstanceOf(InvalidEntityException.class);
            verifyNoInteractions(movieRepository);
        }

        @Test
        @DisplayName("addMovie rejects a negative duration")
        void addMovieNegativeDuration() {
            assertThatThrownBy(() -> service.addMovie(TestData.movieRequest("X", 4f, -1f)))
                    .isInstanceOf(InvalidEntityException.class);
            verifyNoInteractions(movieRepository);
        }

        @ParameterizedTest(name = "rating {0} is accepted")
        @ValueSource(floats = {0f, 5f})
        @DisplayName("addMovie accepts the rating boundaries 0 and 5")
        void addMovieRatingBoundaries(float rating) {
            MovieRequestDto request = TestData.movieRequest("X", rating, 120f);
            when(movieRepository.existsByTitle("X")).thenReturn(false);
            when(mapper.dtoToEntity(request)).thenReturn(movie);
            when(movieRepository.save(movie)).thenReturn(movie);
            when(mapper.entityToDto(movie)).thenReturn(response);

            assertThat(service.addMovie(request)).isSameAs(response);
        }

        @Test
        @DisplayName("updateMovie throws ResourceNotFoundException when the movie is missing")
        void updateMovieMissing() {
            when(movieRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateMovie(TestData.movieRequest(), 99))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("updateMovie persists the new values with the given id")
        void updateMovie() {
            MovieRequestDto request = TestData.movieRequest("Interstellar", 4f, 169f);
            Movie updated = TestData.movie(null, "Interstellar", 4f,
                    LocalDate.of(2014, 11, 7), "Sci-Fi", 169f);
            when(movieRepository.findById(7)).thenReturn(Optional.of(movie));
            when(mapper.dtoToEntity(request)).thenReturn(updated);
            when(movieRepository.save(updated)).thenReturn(updated);
            when(mapper.entityToDto(updated)).thenReturn(response);

            assertThat(service.updateMovie(request, 7)).isSameAs(response);
            assertThat(updated.getId()).isEqualTo(7);
        }

        @Test
        @DisplayName("updateMovie validates before touching the repository")
        void updateMovieInvalidRating() {
            assertThatThrownBy(() ->
                    service.updateMovie(TestData.movieRequest("X", 9f, 120f), 1))
                    .isInstanceOf(InvalidEntityException.class);

            verifyNoInteractions(movieRepository);
        }

        @Test
        @DisplayName("deleteMovie removes an existing movie")
        void deleteMovie() {
            when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
            when(mapper.entityToDto(movie)).thenReturn(response);

            service.deleteMovie(1);

            verify(movieRepository).deleteById(1);
        }

        @Test
        @DisplayName("deleteMovie throws ResourceNotFoundException for an unknown movie")
        void deleteMovieMissing() {
            when(movieRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteMovie(99))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(movieRepository, never()).deleteById(99);
        }

        @Test
        @DisplayName("filterByCategory maps matching movies")
        void filterByCategory() {
            when(movieRepository.findByCategory("Sci-Fi")).thenReturn(List.of(movie));
            when(mapper.entityToDto(movie)).thenReturn(response);

            assertThat(service.filterByCategory("Sci-Fi")).containsExactly(response);
        }

        @Test
        @DisplayName("filterByCategory returns an empty list when nothing matches")
        void filterByCategoryEmpty() {
            when(movieRepository.findByCategory("Nope")).thenReturn(List.of());

            assertThat(service.filterByCategory("Nope")).isEmpty();
        }

        @Test
        @DisplayName("topRatedMovies maps the repository page")
        void topRatedMovies() {
            when(movieRepository.findTopRated(PageRequest.of(0, 3)))
                    .thenReturn(new PageImpl<>(List.of(movie)));
            when(mapper.entityToDto(movie)).thenReturn(response);

            Page<MovieResponseDto> result = service.topRatedMovies(0, 3);

            assertThat(result.getContent()).containsExactly(response);
        }

        @Test
        @DisplayName("moviesReleasedAfter maps movies released after the given date")
        void moviesReleasedAfter() {
            LocalDate date = LocalDate.now().minusYears(1);
            when(movieRepository.findByReleaseDateAfter(date)).thenReturn(List.of(movie));
            when(mapper.entityToDto(movie)).thenReturn(response);

            assertThat(service.moviesReleasedAfter(date)).containsExactly(response);
        }

        @Test
        @DisplayName("moviesReleasedAfter returns an empty list when nothing matches")
        void moviesReleasedAfterEmpty() {
            LocalDate date = LocalDate.now().minusYears(1);
            when(movieRepository.findByReleaseDateAfter(date)).thenReturn(List.of());

            assertThat(service.moviesReleasedAfter(date)).isEmpty();
        }

        @Test
        @DisplayName("moviesReleasedAfter rejects a future date with ResourceNotFoundException")
        void moviesReleasedAfterFutureDateAsIs() {
            LocalDate future = LocalDate.now().plusDays(1);

            assertThatThrownBy(() -> service.moviesReleasedAfter(future))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("[EXPECTED] correct behavior")
    @Tag("expected")
    class Expected {

        @Test
        @DisplayName("moviesReleasedAfter rejects a future date with InvalidEntityException")
        void moviesReleasedAfterFutureDate() {
            LocalDate future = LocalDate.now().plusDays(1);

            assertThatThrownBy(() -> service.moviesReleasedAfter(future))
                    .isInstanceOf(InvalidEntityException.class);
        }
    }
}
