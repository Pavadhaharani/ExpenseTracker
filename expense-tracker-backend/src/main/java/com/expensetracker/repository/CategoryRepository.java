package com.expensetracker.repository;

import com.expensetracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Category}.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /** Returns all categories sorted alphabetically for consistent UI display. */
    List<Category> findAllByOrderByNameAsc();

    /** Duplicate-name guard when adding custom categories in the future. */
    boolean existsByName(String name);
}
