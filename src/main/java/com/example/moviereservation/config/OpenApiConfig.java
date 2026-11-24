package com.example.moviereservation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Movie Reservation API",
                version = "v1",
                description = "REST API for browsing movies and showtimes and booking theater seats. " +
                        "Register or log in under /api/v1/auth, then pass the returned JWT as " +
                        "'Bearer <token>' using the Authorize button. " +
                        "Creating, updating, and deleting movies, theaters, and schedules requires the ADMIN role."
        )
)
@SecurityScheme(
        name = OpenApiConfig.BEARER_AUTH,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT returned by /api/v1/auth/register or /api/v1/auth/login"
)
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";
}
