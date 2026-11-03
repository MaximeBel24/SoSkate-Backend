package com.soskate.api.exceptions.handlers;

import com.soskate.api.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Checks error responses through the real application: security filters, controllers and the
 * single {@link ApiExceptionHandler}, which used to compete with seven other advices.
 */
class ErrorHandlingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unknownSpotIsNotFoundRatherThanInternalError() throws Exception {
        mockMvc.perform(get("/spots/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SPOT_NOT_FOUND"));
    }

    @Test
    void invalidRegistrationListsTheInvalidFields() throws Exception {
        mockMvc.perform(post("/customer/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors.email").isNotEmpty())
                .andExpect(jsonPath("$.errors.password").isNotEmpty());
    }

    @Test
    void protectedEndpointWithoutTokenIsUnauthorizedInTheSameFormat() throws Exception {
        mockMvc.perform(get("/admin/bookings"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
