package com.kita.dienstplan.repository;

import com.kita.dienstplan.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Admin entity
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Find admin by username
     */
    Optional<Admin> findByUsername(String username);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Find active admin by username
     */
    @Query("SELECT a FROM Admin a WHERE a.username = :username AND a.isActive = true")
    Optional<Admin> findByUsernameAndIsActiveTrue(@Param("username") String username);
}
