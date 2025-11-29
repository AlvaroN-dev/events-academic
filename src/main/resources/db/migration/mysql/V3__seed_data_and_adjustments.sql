-- =====================================================
-- V3__seed_data_and_adjustments.sql
-- Initial seed data and additional adjustments
-- MySQL specific version
-- =====================================================

-- =====================================================
-- Insert Sample Venues
-- =====================================================
INSERT INTO venue (name, address, city, country, capacity) VALUES
    ('Teatro Nacional', 'Calle 71 #10-25', 'Bogotá', 'Colombia', 1500),
    ('Estadio El Campín', 'Carrera 30 #45-03', 'Bogotá', 'Colombia', 36000),
    ('Centro de Convenciones', 'Carrera 37 #24-67', 'Medellín', 'Colombia', 5000),
    ('Arena del Río', 'Vía 40 #79-320', 'Barranquilla', 'Colombia', 15000),
    ('Teatro Colón', 'Calle 10 #5-32', 'Bogotá', 'Colombia', 800);

-- =====================================================
-- Insert Sample Categories
-- =====================================================
INSERT INTO category (name, description, color, active) VALUES
    ('Música', 'Conciertos y eventos musicales', '#FF5733', TRUE),
    ('Teatro', 'Obras de teatro y espectáculos escénicos', '#33FF57', TRUE),
    ('Deportes', 'Eventos deportivos y competencias', '#3357FF', TRUE),
    ('Conferencias', 'Conferencias y charlas educativas', '#FF33F5', TRUE),
    ('Festivales', 'Festivales y celebraciones culturales', '#F5FF33', TRUE),
    ('Cine', 'Proyecciones de películas y festivales de cine', '#33FFF5', TRUE);

-- =====================================================
-- Insert Sample Events
-- =====================================================
INSERT INTO evento (name, description, event_date, categoria, status, venue_id, capacity, price) VALUES
    ('Concierto Rock Nacional', 'Gran concierto de rock con bandas nacionales', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 30 DAY), 'Música', 'ACTIVE', 2, 20000, 150000.00),
    ('Hamlet - Shakespeare', 'Clásica obra de teatro de William Shakespeare', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 15 DAY), 'Teatro', 'ACTIVE', 1, 1200, 80000.00),
    ('Final Copa Nacional', 'Final del campeonato de fútbol nacional', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 45 DAY), 'Deportes', 'ACTIVE', 2, 35000, 200000.00),
    ('Tech Summit 2025', 'Conferencia de tecnología e innovación', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 60 DAY), 'Conferencias', 'ACTIVE', 3, 3000, 500000.00),
    ('Festival de Jazz', 'Festival internacional de jazz', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 20 DAY), 'Música', 'ACTIVE', 4, 10000, 120000.00);

-- =====================================================
-- Link Events with Categories (ManyToMany)
-- =====================================================
INSERT INTO evento_category (evento_id, category_id) VALUES
    (1, 1), -- Concierto Rock -> Música
    (1, 5), -- Concierto Rock -> Festivales
    (2, 2), -- Hamlet -> Teatro
    (3, 3), -- Final Copa -> Deportes
    (4, 4), -- Tech Summit -> Conferencias
    (5, 1), -- Jazz Festival -> Música
    (5, 5); -- Jazz Festival -> Festivales

-- =====================================================
-- Additional Performance Indexes
-- =====================================================

-- Index for price range queries
CREATE INDEX idx_evento_price ON evento(price);

-- Index for capacity queries on events
CREATE INDEX idx_evento_capacity ON evento(capacity);

-- Composite index for advanced event filtering
CREATE INDEX idx_evento_advanced ON evento(status, event_date, venue_id);

-- =====================================================
-- Stored Procedure for Venue Statistics (MySQL)
-- =====================================================
DELIMITER //

CREATE PROCEDURE get_venue_statistics(IN p_city VARCHAR(100))
BEGIN
    SELECT 
        v.id,
        v.name,
        v.city,
        v.capacity,
        COUNT(e.id) as total_events,
        COALESCE(SUM(e.capacity), 0) as total_event_capacity,
        COALESCE(AVG(e.price), 0) as avg_event_price
    FROM venue v
    LEFT JOIN evento e ON v.id = e.venue_id
    WHERE p_city IS NULL OR v.city = p_city
    GROUP BY v.id, v.name, v.city, v.capacity
    ORDER BY total_events DESC;
END //

DELIMITER ;
