package com.example.moviereservation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openApiSpecAndSwaggerUiArePubliclyAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Movie Reservation API"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.paths['/api/v1/auth/login'].post.summary").value("Log in"))
                .andExpect(jsonPath("$.paths['/api/v1/auth/login'].post.tags[0]").value("Authentication"))
                .andExpect(jsonPath("$.paths['/api/v1/auth/login'].post.security").doesNotExist())
                .andExpect(jsonPath("$.paths['/api/v1/movie']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/schedule']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/theater']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/reservation']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/user/{id}']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/admin/users']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/admin/summary'].get.tags[0]").value("Admin"))
                .andExpect(jsonPath("$.paths['/api/v1/admin/reservations/{reservationId}/approve'].post.tags[0]")
                        .value("Admin"))
                .andExpect(jsonPath("$.paths['/api/v1/movie/id/{id}'].get.security[0].bearerAuth").exists());

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
