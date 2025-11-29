package com.codeup.riwi.tiqueteracatalogo.infrastructure.repositories;

import com.codeup.riwi.tiqueteracatalogo.infrastructure.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for RoleEntity operations.
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    /**
     * Finds a role by its name.
     */
    Optional<RoleEntity> findByName(RoleEntity.RoleName name);

    /**
     * Checks if a role exists by name.
     */
    boolean existsByName(RoleEntity.RoleName name);
}
