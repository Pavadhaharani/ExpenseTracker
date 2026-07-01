package com.expensetracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test — verifies the Spring application context loads
 * without errors against the test profile (H2 in-memory database).
 */
@SpringBootTest
@ActiveProfiles("test")
class ExpenseTrackerApplicationTests {

    @Test
    void contextLoads() {
        // If the context fails to start, this test will fail with a
        // descriptive exception — no assertions needed here.
    }
}
