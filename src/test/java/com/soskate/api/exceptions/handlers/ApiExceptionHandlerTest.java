package com.soskate.api.exceptions.handlers;

import com.soskate.api.exceptions.booking.SlotNotAvailableException;
import com.soskate.api.exceptions.photo.PhotoUploadException;
import com.soskate.api.exceptions.spot.SpotNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FailingController())
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void businessErrorIsSentWithItsStatusCodeAndMessage() throws Exception {
        mockMvc.perform(get("/test/spot-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("SPOT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Spot not found with id: 42"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void subclassOfBookingExceptionKeepsItsOwnCode() throws Exception {
        mockMvc.perform(get("/test/slot-taken"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("SLOT_NOT_AVAILABLE"));
    }

    @Test
    void serverSideBusinessErrorDoesNotExposeItsMessage() throws Exception {
        mockMvc.perform(get("/test/upload-failed"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("PHOTO_UPLOAD_ERROR"))
                .andExpect(jsonPath("$.message").value(ApiExceptionHandler.INTERNAL_ERROR_MESSAGE))
                .andExpect(content().string(not(containsString("arn:aws"))));
    }

    @Test
    void unexpectedExceptionBecomesGenericInternalError() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value(ApiExceptionHandler.INTERNAL_ERROR_MESSAGE))
                .andExpect(content().string(not(containsString("internal detail"))));
    }

    @Test
    void invalidBodyListsTheInvalidFields() throws Exception {
        mockMvc.perform(post("/test/validated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                // The message comes from Hibernate Validator and depends on the JVM locale.
                .andExpect(jsonPath("$.errors.name").isNotEmpty());
    }

    @Test
    void malformedJsonIsAnInvalidRequest() throws Exception {
        mockMvc.perform(post("/test/validated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void missingParameterIsAnInvalidRequestNamingTheParameter() throws Exception {
        mockMvc.perform(get("/test/required-param"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value(containsString("page")));
    }

    @Test
    void unsupportedMethodIsReportedAsSuch() throws Exception {
        mockMvc.perform(post("/test/spot-not-found"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
    }

    @Test
    void accessDeniedByMethodSecurityIsForbidden() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    void databaseConstraintViolationIsAConflictWithoutDatabaseDetails() throws Exception {
        mockMvc.perform(get("/test/constraint-violated"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DATA_CONFLICT"))
                .andExpect(content().string(not(containsString("Duplicate entry"))));
    }

    @RestController
    @RequestMapping("/test")
    static class FailingController {

        @GetMapping("/spot-not-found")
        void spotNotFound() {
            throw new SpotNotFoundException(42L);
        }

        @GetMapping("/slot-taken")
        void slotTaken() {
            throw new SlotNotAvailableException();
        }

        @GetMapping("/upload-failed")
        void uploadFailed() {
            throw new PhotoUploadException("S3 upload failed for arn:aws:s3:::soskate-photos-prod");
        }

        @GetMapping("/unexpected")
        void unexpected() {
            throw new IllegalStateException("internal detail");
        }

        @GetMapping("/forbidden")
        void forbidden() {
            throw new AccessDeniedException("Access Denied");
        }

        @GetMapping("/constraint-violated")
        void constraintViolated() {
            throw new DataIntegrityViolationException("Duplicate entry 'a@b.c' for key 'users.email'");
        }

        @GetMapping("/required-param")
        void requiredParam(@RequestParam int page) {
        }

        @PostMapping("/validated")
        void validated(@Valid @RequestBody Payload payload) {
        }

        record Payload(@NotBlank String name) {
        }
    }
}
