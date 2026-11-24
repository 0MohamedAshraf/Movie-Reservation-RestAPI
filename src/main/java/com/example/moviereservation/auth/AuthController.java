package com.example.moviereservation.auth;

import com.example.moviereservation.dto.request.LoginRequest;
import com.example.moviereservation.dto.request.RegisterRequest;
import com.example.moviereservation.dto.response.AuthenticationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Public registration and login; no JWT required")
public class AuthController {
    private final AuthService auth;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates an account with the given role (ADMIN or CUSTOMER) and returns a JWT. " +
                    "The role field is required."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered; JWT returned"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or email already registered")
    })
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(auth.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Log in",
            description = "Authenticates with email and password and returns a JWT for use with the Authorize button."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated; JWT returned"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password"),
            @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity
                .ok(auth.login(request));
    }

}
