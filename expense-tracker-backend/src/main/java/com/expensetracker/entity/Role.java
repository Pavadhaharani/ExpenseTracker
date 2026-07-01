package com.expensetracker.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity for the {@code roles} table.
 * Only two roles exist (seeded via data.sql): ROLE_USER and ROLE_ADMIN.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 20)
    private RoleName name;
}
