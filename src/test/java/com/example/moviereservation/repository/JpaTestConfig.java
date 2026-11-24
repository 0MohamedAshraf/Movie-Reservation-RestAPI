package com.example.moviereservation.repository;

import com.example.moviereservation.entity.Movie;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Minimal JPA-only context used by the repository tests. Spring Boot 4.1 does not
 * ship a {@code @DataJpaTest} slice in this project, so the tests bootstrap just
 * the pieces they need against the in-memory H2 database.
 */
@Configuration
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@EntityScan(basePackageClasses = Movie.class)
@EnableJpaRepositories(basePackageClasses = MovieRepository.class)
class JpaTestConfig {
}
