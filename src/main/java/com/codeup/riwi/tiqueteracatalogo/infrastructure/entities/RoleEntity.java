package com.codeup.riwi.tiqueteracatalogo.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a user role in the system.
 * Roles define the permissions and access levels for users.
 * 
 * <p>Default roles:</p>
 * <ul>
 *   <li>ROLE_USER - Standard user access</li>
 *   <li>ROLE_ADMIN - Administrative access</li>
 *   <li>ROLE_MODERATOR - Moderation access</li>
 * </ul>
 * 
 * @author TiqueteraCatalogo Team
 * @version 1.0
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Column(name = "description", length = 255)
    private String description;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserEntity> users = new HashSet<>();

    /**
     * Enumeration of available role names.
     * All role names must start with "ROLE_" prefix for Spring Security compatibility.
     */
    public enum RoleName {
        ROLE_USER,
        ROLE_ADMIN,
        ROLE_MODERATOR
    }

    /**
     * Constructor with name only.
     */
    public RoleEntity(RoleName name) {
        this.name = name;
    }

    /**
     * Constructor with name and description.
     */
    public RoleEntity(RoleName name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleEntity that = (RoleEntity) o;
        return name == that.name;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name != null ? name.name() : "UNKNOWN";
    }
}
