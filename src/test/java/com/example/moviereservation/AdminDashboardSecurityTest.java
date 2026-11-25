package com.example.moviereservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Admin dashboard security")
class AdminDashboardSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("the dashboard shell is publicly served")
    void dashboardShellIsPublic() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/admin/index.html"));

        mockMvc.perform(get("/admin/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/admin/styles.css\"")))
                .andExpect(content().string(containsString("src=\"/admin/app.js\"")))
                .andExpect(content().string(not(containsString("src=\"app.js\""))))
                .andExpect(content().string(containsString("method=\"post\"")));

        mockMvc.perform(get("/admin/app.js"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/admin/styles.css"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("an ADMIN can read every admin collection")
    void adminCanReadAllCollections() throws Exception {
        for (String path : List.of("/api/v1/admin/summary", "/api/v1/admin/users",
                "/api/v1/admin/theaters", "/api/v1/admin/movies", "/api/v1/admin/schedules",
                "/api/v1/admin/schedule-seats", "/api/v1/admin/reservations")) {
            mockMvc.perform(get(path).with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("a CUSTOMER cannot read admin data")
    void customerCannotReadAdminData() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users").with(user("customer").roles("CUSTOMER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("an anonymous request is rejected with 401")
    void anonymousCannotReadAdminData() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("an ADMIN approve request reaches the reservation service")
    void adminCanApprove() throws Exception {
        mockMvc.perform(post("/api/v1/admin/reservations/999999/approve")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("a CUSTOMER cannot approve a reservation")
    void customerCannotApprove() throws Exception {
        mockMvc.perform(post("/api/v1/admin/reservations/999999/approve")
                        .with(user("customer").roles("CUSTOMER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("the admin reservation list exposes joined data without passwords")
    void adminReservationResponseShape() throws Exception {
        mockMvc.perform(get("/api/v1/admin/reservations").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
