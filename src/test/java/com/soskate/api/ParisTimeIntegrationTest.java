package com.soskate.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

public class ParisTimeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void theApplicationRunsOnParisTime() {
        assertThat(TimeZone.getDefault().getID()).isEqualTo("Europe/Paris");
    }

    @Test
    void aTimeReachesTheDatabaseExactlyAsWritten() {
        Timestamp twoPm = Timestamp.valueOf(LocalDateTime.of(2027, 1, 15, 14, 0));

        String seenByMySql = jdbcTemplate.queryForObject(
                "SELECT DATE_FORMAT(?, '%H:%i')", String.class, twoPm);

        assertThat(seenByMySql).isEqualTo("14:00");
    }
}
