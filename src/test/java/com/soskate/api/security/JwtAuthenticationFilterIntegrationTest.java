package com.soskate.api.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soskate.api.AbstractIntegrationTest;
import com.soskate.api.config.SoskateSecurityProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Named.named;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A token the API cannot trust counts as no token at all: public routes keep answering
 * and protected routes return the regular 401.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtAuthenticationFilterIntegrationTest extends AbstractIntegrationTest {

    private static final String PUBLIC_ROUTE = "/services/active";
    private static final String PROTECTED_ROUTE = "/admin/bookings";
    private static final String PASSWORD = "ValidPass1!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SoskateSecurityProperties securityProperties;

    Stream<Named<String>> untrustedTokens() {
        Instant tomorrow = Instant.now().plus(Duration.ofDays(1));
        return Stream.of(
                named("malformed", "not.a.token"),
                named("expired", expiredToken()),
                named("signed with another key", token("rider@soskate.test", tomorrow, Jwts.SIG.HS256.key().build())),
                named("for an unknown user", token("nobody@soskate.test", tomorrow, signingKey()))
        );
    }

    @ParameterizedTest
    @MethodSource("untrustedTokens")
    void publicRouteIgnoresAnUntrustedToken(String token) throws Exception {
        mockMvc.perform(get(PUBLIC_ROUTE).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("untrustedTokens")
    void protectedRouteRejectsAnUntrustedToken(String token) throws Exception {
        mockMvc.perform(get(PROTECTED_ROUTE).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void validTokenStillAuthenticates() throws Exception {
        String email = registerCustomer();
        JsonNode login = login(email, null);

        mockMvc.perform(get("/customers/" + login.get("customerId").asLong() + "/my-bookings")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + login.get("token").asText()))
                .andExpect(status().isOk());
    }

    @Test
    void loginSucceedsWithAnExpiredTokenInTheHeader() throws Exception {
        String email = registerCustomer();

        JsonNode login = login(email, expiredToken());

        assertEquals(email, login.get("email").asText());
    }

    private String registerCustomer() throws Exception {
        String email = "rider-" + UUID.randomUUID() + "@soskate.test";
        Map<String, String> body = Map.of(
                "email", email,
                "password", PASSWORD,
                "firstName", "Test",
                "lastName", "Rider"
        );
        mockMvc.perform(post("/customer/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
        return email;
    }

    private JsonNode login(String email, String staleToken) throws Exception {
        var request = post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", email, "password", PASSWORD)));
        if (staleToken != null) {
            request.header(HttpHeaders.AUTHORIZATION, "Bearer " + staleToken);
        }
        String response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response);
    }

    private String expiredToken() {
        return token("rider@soskate.test", Instant.now().minus(Duration.ofHours(1)), signingKey());
    }

    private String token(String subject, Instant expiration, SecretKey key) {
        return Jwts.builder()
                .subject(subject)
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(securityProperties.getJwtSecret()));
    }
}
