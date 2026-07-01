package com.expensetracker.repository;

import com.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User}.
 * All finder methods generate JPQL via method-name derivation.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Used during JWT filter to reload the security principal by username. */
    Optional<User> findByUsername(String username);

    /** Supports login by e-mail address as an alternative to username. */
    Optional<User> findByEmail(String email);

    /** Registration guard — prevents duplicate usernames. */
    boolean existsByUsername(String username);

    /** Registration guard — prevents duplicate e-mail addresses. */
    boolean existsByEmail(String email);
}
