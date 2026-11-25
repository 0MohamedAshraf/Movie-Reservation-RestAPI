package com.example.moviereservation.security;

import com.example.moviereservation.enums.UserRole;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless REST APIs
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Static admin dashboard shell (data and actions stay protected below)
                        .requestMatchers("/admin", "/admin/**").permitAll()

                        // Swagger UI / OpenAPI documentation
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                "/v3/api-docs.yaml", "/webjars/**").permitAll()

                        .requestMatchers("/api/v1/admin/**").hasRole(UserRole.ADMIN.name())

                        .requestMatchers(HttpMethod.POST,"/api/v1/movie/**","/api/v1/schedule/**","/api/v1/theater/**")
                        .hasRole(UserRole.ADMIN.name())

                        .requestMatchers(HttpMethod.PUT,"/api/v1/movie/**","/api/v1/schedule/**","/api/v1/theater/**")
                        .hasRole(UserRole.ADMIN.name())

                        .requestMatchers(HttpMethod.DELETE,"/api/v1/movie/**","/api/v1/schedule/**","/api/v1/theater/**")
                        .hasRole(UserRole.ADMIN.name())


                        .anyRequest()
                        .authenticated()
                ).sessionManagement(
                        (session) ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
