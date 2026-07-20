# Movie Reservation System

A REST API for browsing movies and showtimes, and booking seats at a theater, built with Spring Boot and Spring Data JPA.

## Features

- **Movies** — create movies and look them up by ID, title, or category; browse top-rated or recently released titles
- **Theaters** — create theaters, look them up by ID, name, or city, and view a theater's upcoming schedules
- **Schedules** — create and update showtimes linking a movie to a theater, with overlap validation so a theater can't be double-booked
- **Seats** — each schedule auto-generates its own set of bookable seats, tracked as available/unavailable
- **Reservations** — book one or more seats for a schedule, with automatic pricing, seat-availability checks, and cancellation (which releases the seats back for booking)

## Tech Stack

- Java 17
- Spring Boot 4.1.0 (`spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`)
- MySQL (via `mysql-connector-j`)
- Lombok
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

The API will start on `http://localhost:8080` by default. All endpoints are prefixed with `/api/v1`.

## API Reference

### Movies — `/api/v1/movie`

| Method | Endpoint                      | Description                        |
|--------|--------------------------------|-------------------------------------|
| GET    | `/movie`                       | Get all movies                      |
| GET    | `/movie/id/{id}`               | Get a movie by ID                   |
| GET    | `/movie/title/{title}`         | Get a movie by title                |
| GET    | `/movie/category/{category}`   | Get movies filtered by category     |
| GET    | `/movie/top`                   | Get top-rated movies                |
| GET    | `/movie/afterDate/{date}`      | Get movies released after a date    |
| POST   | `/movie`                       | Add a new movie                     |
| PUT    | `/movie/{id}`                  | Update a movie                      |
| DELETE | `/movie/{id}`                  | Delete a movie                      |

**Example movie payload:**

```json
{
  "title": "Inception",
  "description": "A thief who steals corporate secrets through dream-sharing technology.",
  "rating": 8.8,
  "releaseDate": "2010-07-16",
  "category": "Sci-Fi",
  "duration": 148.0
}
```

### Theaters — `/api/v1/theater`

| Method | Endpoint                  | Description                          |
|--------|----------------------------|----------------------------------------|
| GET    | `/theater`                 | Get all theaters                       |
| GET    | `/theater/id/{id}`         | Get a theater by ID                    |
| GET    | `/theater/name/{name}`     | Get a theater by name                  |
| GET    | `/theater/city/{city}`     | Get theaters filtered by city          |
| GET    | `/theater/{id}/schedules`  | Get a theater's upcoming schedules     |
| POST   | `/theater`                 | Add a new theater                      |
| PUT    | `/theater/{id}`            | Update a theater                       |
| DELETE | `/theater/{id}`            | Delete a theater                       |

**Example theater payload:**

```json
{
  "name": "Downtown Cinema",
  "city": "Cairo",
  "totalSeats": 120
}
```

### Schedules — `/api/v1/schedule`

| Method | Endpoint                     | Description                                       |
|--------|--------------------------------|-----------------------------------------------------|
| GET    | `/schedule`                    | Get all schedules                                   |
| GET    | `/schedule/{id}`               | Get a schedule by ID                                |
| GET    | `/schedule/upcoming`           | Get all upcoming schedules                          |
| GET    | `/schedule/date/today`         | Get today's schedules                               |
| GET    | `/schedule/date/{date}`        | Get the schedule for a given date                   |
| GET    | `/schedule/{scheduleId}/movie` | Get the movie tied to a schedule                    |
| POST   | `/schedule`                    | Add a new schedule (auto-generates its seat map)    |
| PUT    | `/schedule/{id}`               | Update an existing schedule                         |
| DELETE | `/schedule/{id}`               | Delete a schedule                                   |

**Example schedule payload:**

```json
{
  "startTime": "2026-07-10T18:00:00",
  "endTime": "2026-07-10T20:28:00",
  "date": "2026-07-10",
  "price": 12.5,
  "movieId": 1,
  "theaterId": 1
}
```

Schedules are validated on create/update: `endTime` must be after `startTime`, `price` must be greater than 0, and a schedule can't overlap another schedule already booked for the same theater on the same date.

### Reservations — `/api/v1/reservation`

| Method | Endpoint                            | Description                                  |
|--------|---------------------------------------|-------------------------------------------------|
| POST   | `/reservation`                        | Reserve one or more seats for a schedule         |
| POST   | `/reservation/{reservationId}/cancel` | Cancel a reservation and release its seats       |

**Example reservation payload:**

```json
{
  "paymentMethod": "CREDIT_CARD",
  "userId": 1,
  "scheduleId": 1,
  "scheduleSeatIds": [5, 6, 7]
}
```

The total price is calculated automatically from the schedule's price and number of seats booked. Reserving seats that are already taken, don't belong to the given schedule, or don't exist returns an error and nothing is booked. Cancelling a reservation marks its seats available again and can't be done twice on the same reservation.

## Data Model

- **Movie** — title, description, rating, release date, category, duration; has many `Schedule`s and many `Actor`s
- **Theater** — name, city, total seats; has many `Schedule`s
- **Schedule** — start/end time, date, price; belongs to one `Movie` and one `Theater`; has many `ScheduleSeat`s
- **Seat** — a physical seat (row, seat number, class: `STANDARD` / `PREMIUM` / `VIP`) belonging to a `Theater`
- **ScheduleSeat** — a `Seat` as booked/available for a specific `Schedule`
- **User** — first name, last name, email, password, city, role (`ADMIN` / `CUSTOMER`)
- **Reservation** — payment method (`CASH` / `CREDIT_CARD` / `DEBIT_CARD` / `WALLET`), total price, booking date, status (`PENDING` / `CONFIRMED` / `CANCELLED` / `EXPIRED`); belongs to one `User` and one `Schedule`
- **ReservationSeat** — join between a `Reservation` and the `ScheduleSeat`s it booked
- **Actor** — name, bio; many-to-many with `Movie`

## Error Handling

Errors are returned as plain-text messages with the following status codes:

| Exception                     | Status |
|--------------------------------|--------|
| `ResourceNotFoundException`    | 404 Not Found |
| `EntityAlreadyExistsException` | 409 Conflict |
| `ScheduleOverlapException`     | 409 Conflict |
| `InvalidEntityException`       | 400 Bad Request |
| `IllegalArgumentException`     | 400 Bad Request |

## Project Structure

```
src/main/java/com/example/moviereservation/
├── controller/       # REST controllers (Movie, Theater, Schedule, Reservation)
├── dto/
│   ├── request/       # Incoming request payloads
│   └── response/       # Outgoing response payloads
├── entity/            # JPA entities (Movie, Theater, Schedule, Seat, ScheduleSeat, User, Reservation, ReservationSeat, Actor)
├── enums/              # PaymentMethod, ReservationStatus, SeatClass, UserRole
├── exceptions/         # Custom exceptions + global exception handler
├── mapper/             # Entity <-> DTO mappers
├── repository/         # Spring Data JPA repositories
├── service/            # Business logic interfaces + implementations
└── MovieReservationApplication.java
```

## Running Tests

```bash
./mvnw test
```