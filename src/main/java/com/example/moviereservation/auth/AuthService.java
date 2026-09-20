package com.example.moviereservation.auth;

import com.example.moviereservation.dto.request.LoginRequest;
import com.example.moviereservation.dto.request.RegisterRequest;
import com.example.moviereservation.dto.response.AuthenticationResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse login(LoginRequest request);
}
