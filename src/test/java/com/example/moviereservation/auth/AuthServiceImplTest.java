package com.example.moviereservation.auth;

import com.example.moviereservation.dto.request.LoginRequest;
import com.example.moviereservation.dto.request.RegisterRequest;
import com.example.moviereservation.dto.response.AuthenticationResponse;
import com.example.moviereservation.entity.User;
import com.example.moviereservation.exceptions.InvalidEntityException;
import com.example.moviereservation.repository.UserRepository;
import com.example.moviereservation.security.JwtService;
import com.example.moviereservation.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuthServiceImpl")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl service;

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("rejects an email that is already registered")
        void duplicateEmail() {
            RegisterRequest request = TestData.registerRequest("jane@example.com", "secret123");
            when(userRepository.findByEmail("jane@example.com"))
                    .thenReturn(Optional.of(TestData.user(1, "jane@example.com")));

            assertThatThrownBy(() -> service.register(request))
                    .isInstanceOf(InvalidEntityException.class);

            verify(userRepository, never()).save(any());
            verify(jwtService, never()).generateToken(anyMap(), any());
        }

        @Test
        @DisplayName("encodes the password, saves the user and returns a token")
        void success() {
            RegisterRequest request = TestData.registerRequest("jane@example.com", "secret123");
            when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User toSave = invocation.getArgument(0);
                toSave.setId(7);
                return toSave;
            });
            when(jwtService.generateToken(anyMap(), any(User.class))).thenReturn("jwt-token");

            AuthenticationResponse response = service.register(request);

            assertThat(response.getToken()).isEqualTo("jwt-token");
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User saved = captor.getValue();
            assertThat(saved.getFirstName()).isEqualTo("Jane");
            assertThat(saved.getLastName()).isEqualTo("Doe");
            assertThat(saved.getEmail()).isEqualTo("jane@example.com");
            assertThat(saved.getCity()).isEqualTo("Cairo");
            assertThat(saved.getPassword()).isEqualTo("encoded-password");
            verify(jwtService).generateToken(anyMap(), eq(saved));
        }
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("authenticates, loads the user and returns a token")
        void success() {
            LoginRequest request = TestData.loginRequest("jane@example.com", "secret123");
            User user = TestData.user(1, "jane@example.com");
            when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
            when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
            when(jwtService.generateToken(anyMap(), eq(user))).thenReturn("jwt-token");

            AuthenticationResponse response = service.login(request);

            assertThat(response.getToken()).isEqualTo("jwt-token");
            ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
            verify(authenticationManager).authenticate(captor.capture());
            UsernamePasswordAuthenticationToken token =
                    (UsernamePasswordAuthenticationToken) captor.getValue();
            assertThat(token.getPrincipal()).isEqualTo("jane@example.com");
            assertThat(token.getCredentials()).isEqualTo("secret123");
        }

        @Test
        @DisplayName("propagates BadCredentialsException from the authentication manager")
        void badCredentials() {
            LoginRequest request = TestData.loginRequest("jane@example.com", "wrong-pass");
            when(authenticationManager.authenticate(any(Authentication.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> service.login(request))
                    .isInstanceOf(BadCredentialsException.class);

            verify(jwtService, never()).generateToken(anyMap(), any());
        }

        @Test
        @DisplayName("throws UsernameNotFoundException when the user disappears after authentication")
        void userMissingAfterAuthentication() {
            LoginRequest request = TestData.loginRequest("jane@example.com", "secret123");
            when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
            when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.login(request))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }
}
