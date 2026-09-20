package com.example.moviereservation.auth;

import com.example.moviereservation.dto.request.LoginRequest;
import com.example.moviereservation.dto.request.RegisterRequest;
import com.example.moviereservation.dto.response.AuthenticationResponse;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.exceptions.InvalidEntityException;
import com.example.moviereservation.repository.UserRepository;
import com.example.moviereservation.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        User user = repository.findByEmail(request.getEmail()).orElse(null);
        if (user != null)
            throw new InvalidEntityException("User with this email already registered");

        User newUser = new User(
                null,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getCity(),
                request.getRole()
        );
        User savedUser = repository.save(newUser);
        String token = jwtService.generateToken(new HashMap<>(),savedUser);
        return new AuthenticationResponse(token);
    }

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = repository.findByEmail(request.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found")
        );
        String token = jwtService.generateToken(new HashMap<>(),user);

        return new AuthenticationResponse(token);
    }
}
