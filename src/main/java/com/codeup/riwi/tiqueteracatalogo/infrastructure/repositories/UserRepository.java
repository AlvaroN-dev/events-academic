package com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories;

import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserEntity operations.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Finds a user by username.
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Finds a user by email.
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Finds a user by username or email.
     */
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    /**
     * Checks if a user exists by username.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by email.
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user exists by username or email.
     */
    boolean existsByUsernameOrEmail(String username, String email);

    /**
     * Finds a user by username with roles eagerly loaded.
     */
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<UserEntity> findByUsernameWithRoles(@Param("username") String username);

    /**
     * Finds a user by email with roles eagerly loaded.
     */
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);
}
