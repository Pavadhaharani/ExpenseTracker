package com.expensetracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * JPA entity for the {@code categories} table.
 * The six default categories (Food, Travel, Shopping, Bills,
 * Entertainment, Others) are seeded via {@code data.sql}.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    /** Bootstrap Icons class name, e.g. {@code "bi-basket2-fill"}. */
    @Column(name = "icon", length = 50)
    private String icon;

    /** CSS hex color string used for badges/charts, e.g. {@code "#FF6B6B"}. */
    @Column(name = "color", length = 20)
    private String color;

    /** {@code true} for the six built-in categories, {@code false} for user-created ones. */
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
