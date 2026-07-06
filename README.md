# Movie Reservation System

A REST API for managing movies, theaters, and showtime schedules, built with Spring Boot and Spring Data JPA.

## Features

- **Movies** — create movies and look them up by ID or title
- **Theaters** — create theaters and look them up by ID or name
- **Schedules** — create and update showtimes, linking a movie to a theater with a start/end time, date, and ticket price

## Tech Stack

- Java 17
- Spring Boot 4.1.0 (`spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`)
- MySQL (via `mysql-connector-j`)
- Maven

## Prerequisites

- JDK 17+
- Maven 
- MySQL server running locally

## Getting Started

### 1. Create the database

```sql
CREATE DATABASE movie_reservation_db;
```

### 2. Configure the connection

Edit `src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.application.name=movie-reservation

spring.datasource.url=jdbc:mysql://localhost:3306/movie_reservation_db
spring.datasource.username=your-username
spring.datasource.password=your-password
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API will start on `http://localhost:8080` by default.

## API Reference

### Movies — `/movie`

| Method | Endpoint            | Description                  |
|--------|---------------------|-------------------------------|
| GET    | `/movie`             | Get all movies                |
| GET    | `/movie/id/{id}`     | Get a movie by ID             |
| GET    | `/movie/title/{title}` | Get a movie by title        |
| POST   | `/movie`             | Add a new movie               |

**Example movie payload:**

```json
{
  "id": "m1",
  "title": "Inception",
  "description": "A thief who steals corporate secrets through dream-sharing technology.",
  "rating": 8.8,
  "category": "Sci-Fi",
  "duration": 148.0
}
```

### Theaters — `/theater`

| Method | Endpoint             | Description                 |
|--------|----------------------|-------------------------------|
| GET    | `/theater`            | Get all theaters              |
| GET    | `/theater/id/{id}`    | Get a theater by ID           |
| GET    | `/theater/name/{name}`| Get a theater by name         |
| POST   | `/theater`            | Add a new theater             |

**Example theater payload:**

```json
{
  "id": "t1",
  "name": "Downtown Cinema",
  "city": "Cairo",
  "totalSeats": 120
}
```

### Schedules — `/schedule`

| Method | Endpoint                     | Description                             |
|--------|-------------------------------|------------------------------------------|
| GET    | `/schedule`                   | Get all schedules                        |
| GET    | `/schedule/{id}`               | Get a schedule by ID                     |
| GET    | `/schedule/{scheduleId}/movie` | Get the movie tied to a schedule          |
| POST   | `/schedule`                    | Add a new schedule                       |
| PUT    | `/schedule`                    | Update an existing schedule               |

**Example schedule payload:**

```json
{
  "id": "s1",
  "startTime": "2026-07-10T18:00:00",
  "endTime": "2026-07-10T20:28:00",
  "date": "2026-07-10",
  "price": 12.5,
  "movie": { "id": "m1" },
  "theater": { "id": "t1" }
}
```

## Project Structure

```
src/main/java/com/example/moviereservation/
├── controller/       # REST controllers (Movie, Theater, Schedule)
├── service/          # Business logic interfaces + implementations
├── repository/       # Spring Data JPA repositories
├── entity/           # JPA entities (Movie, Theater, Schedule, Actor)
└── MovieReservationApplication.java
```

## Data Model

- **Movie** — title, description, rating, release date, category, duration; has many `Schedule`s
- **Theater** — name, city, total seats
- **Schedule** — start/end time, date, price; belongs to one `Movie` and one `Theater`
- **Actor** — name, bio 

## Running Tests

```bash
./mvnw test
```
