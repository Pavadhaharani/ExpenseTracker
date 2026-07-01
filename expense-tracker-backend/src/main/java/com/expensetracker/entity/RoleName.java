package com.expensetracker.entity;

/**
 * Enumeration of supported user roles.
 * Stored as a VARCHAR in the database via @Enumerated(EnumType.STRING).
 */
public enum RoleName {
    ROLE_USER,
    ROLE_ADMIN
}
