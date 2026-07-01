package com.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Expense Tracker Spring Boot application.
 *
 * <p>Tech stack: Spring Boot 3.3 · Java 21 · MySQL 8 · Spring Security (JWT)
 */
@SpringBootApplication
public class ExpenseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }
}
