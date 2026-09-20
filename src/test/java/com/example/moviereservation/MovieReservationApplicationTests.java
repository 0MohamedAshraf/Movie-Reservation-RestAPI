package com.example.moviereservation;

import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MovieReservationApplicationTests {

	@Autowired
	private MovieService movieService;

	@Test
	void movieNotFoundTest() {

		assertThrows(ResourceNotFoundException.class,() -> movieService.getMovieByTitle("Spider Man : Brand New Day"));

	}

}
