package com.soskate.api;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class of the tests that start the whole application against a disposable MySQL.
 * <p>
 * Every subclass declares exactly the same configuration, so Spring reuses one application context,
 * and therefore one MySQL container, for all of them. Do not add configuration annotations to a
 * subclass unless it really needs its own context.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIntegrationTest {
}
