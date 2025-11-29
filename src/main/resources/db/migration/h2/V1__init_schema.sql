-- =====================================================
-- V1__init_schema.sql (H2 Compatible Version)
-- Initial schema creation for TiqueteraCatalogo
-- Creates base tables: venue, evento, category
-- =====================================================

-- =====================================================
-- Table: venue
-- Stores venue/location information for events
-- =====================================================
CREATE TABLE IF NOT EXISTS venue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(300) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Table: category
-- Stores event categories/tags for classification
-- =====================================================
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    color VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Table: evento
-- Stores event information
-- =====================================================
CREATE TABLE IF NOT EXISTS evento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    event_date TIMESTAMP NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    venue_id BIGINT NOT NULL,
    capacity INT NOT NULL,
    price DOUBLE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Table: evento_category (Junction table for ManyToMany)
-- Links events with categories
-- =====================================================
CREATE TABLE IF NOT EXISTS evento_category (
    evento_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (evento_id, category_id)
);
