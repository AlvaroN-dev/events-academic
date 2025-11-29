-- =====================================================
-- V4: Create Users and Roles tables for JWT Authentication
-- =====================================================

-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de usuarios (matches UserEntity fields)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla intermedia para relación muchos a muchos entre usuarios y roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices para optimización
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_enabled ON users(enabled);
CREATE INDEX idx_roles_name ON roles(name);

-- Insertar roles por defecto (usando ROLE_ prefix para Spring Security)
INSERT INTO roles (name, description) VALUES 
    ('ROLE_ADMIN', 'Administrador con acceso completo al sistema'),
    ('ROLE_USER', 'Usuario estándar con permisos básicos'),
    ('ROLE_MODERATOR', 'Moderador con permisos de gestión de contenido');

-- Crear usuario administrador por defecto
-- Password: Admin123! (encriptado con BCrypt)
INSERT INTO users (username, email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired) VALUES 
    ('admin', 'admin@tiquetera.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQDOe5vFSRhfJZJDYjI8BOkDhHm6', 'Admin', 'Sistema', TRUE, TRUE, TRUE, TRUE);

-- Asignar rol ROLE_ADMIN al usuario administrador
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.email = 'admin@tiquetera.com' AND r.name = 'ROLE_ADMIN';
