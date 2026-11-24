package com.example.moviereservation.security;

import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.UserRole;
import com.example.moviereservation.exceptions.ResourceNotFoundException;
import com.example.moviereservation.repository.UserRepository;
import com.example.moviereservation.support.TestData;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("JwtAuthFilter")
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtAuthFilter filter;

    private final User user = TestData.user(1, "jane@example.com", UserRole.CUSTOMER);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private MockHttpServletRequest request(String path, String authorizationHeader) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.setServletPath(path);
        if (authorizationHeader != null) {
            request.setAuthType("Bearer");
            request.addHeader("Authorization", authorizationHeader);
        }
        return request;
    }

    private void invoke(MockHttpServletRequest request) throws Exception {
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
    }

    private Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Nested
    @DisplayName("[AS-IS] current behavior")
    class AsIs {

        @Test
        @DisplayName("continues without authentication when there is no Authorization header")
        void noHeader() throws Exception {
            invoke(request("/api/v1/movie", null));

            assertThat(authentication()).isNull();
            verifyNoInteractions(jwtService, userRepository);
        }

        @Test
        @DisplayName("continues without authentication for a non-Bearer header")
        void nonBearerHeader() throws Exception {
            invoke(request("/api/v1/movie", "Basic abc"));

            assertThat(authentication()).isNull();
            verifyNoInteractions(jwtService, userRepository);
        }

        @Test
        @DisplayName("skips the filter for the auth endpoints")
        void authPathSkipped() throws Exception {
            invoke(request("/api/v1/auth/login", "Bearer token"));

            assertThat(authentication()).isNull();
            verifyNoInteractions(jwtService, userRepository);
        }

        @Test
        @DisplayName("authenticates a request with a valid token")
        void validToken() throws Exception {
            when(jwtService.extractUserId("token")).thenReturn(1);
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(jwtService.isTokenValid("token", user)).thenReturn(true);

            invoke(request("/api/v1/movie", "Bearer token"));

            assertThat(authentication()).isNotNull();
            assertThat(authentication().getPrincipal()).isSameAs(user);
            assertThat(authentication().getAuthorities())
                    .extracting(Object::toString)
                    .containsExactly("ROLE_CUSTOMER");
        }

        @Test
        @DisplayName("keeps an already established authentication")
        void existingAuthenticationKept() throws Exception {
            Authentication existing = new UsernamePasswordAuthenticationToken(
                    "existing", null, List.of());
            SecurityContextHolder.getContext().setAuthentication(existing);
            when(jwtService.extractUserId("token")).thenReturn(1);

            invoke(request("/api/v1/movie", "Bearer token"));

            assertThat(authentication()).isSameAs(existing);
            verify(userRepository, never()).findById(1);
        }

        @Test
        @DisplayName("propagates JwtException for an invalid token")
        void invalidTokenAsIs() {
            when(jwtService.extractUserId("token")).thenThrow(new JwtException("invalid"));

            assertThatThrownBy(() -> invoke(request("/api/v1/movie", "Bearer token")))
                    .isInstanceOf(JwtException.class);
        }

        @Test
        @DisplayName("propagates ResourceNotFoundException when the token user no longer exists")
        void unknownUserAsIs() {
            when(jwtService.extractUserId("token")).thenReturn(1);
            when(userRepository.findById(1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> invoke(request("/api/v1/movie", "Bearer token")))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("[EXPECTED] correct behavior")
    @Tag("expected")
    class Expected {

        @Test
        @DisplayName("an invalid token should not break the request; it should continue unauthenticated")
        void invalidTokenShouldContinueUnauthenticated() {
            when(jwtService.extractUserId("token")).thenThrow(new JwtException("invalid"));

            assertThatCode(() -> invoke(request("/api/v1/movie", "Bearer token")))
                    .doesNotThrowAnyException();
            assertThat(authentication()).isNull();
        }

        @Test
        @DisplayName("a token for a deleted user should continue unauthenticated")
        void unknownUserShouldContinueUnauthenticated() {
            when(jwtService.extractUserId("token")).thenReturn(1);
            when(userRepository.findById(1)).thenReturn(Optional.empty());

            assertThatCode(() -> invoke(request("/api/v1/movie", "Bearer token")))
                    .doesNotThrowAnyException();
            assertThat(authentication()).isNull();
        }
    }
}
