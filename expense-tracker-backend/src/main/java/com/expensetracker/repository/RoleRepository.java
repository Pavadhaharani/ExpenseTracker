package com.expensetracker.repository;

import com.expensetracker.entity.Role;
import com.expensetracker.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Role}.
 * Roles are looked up by enum name during user registration.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /** Fetches a role by its enum value (e.g. {@code ROLE_USER}). */
    Optional<Role> findByName(RoleName name);
}
