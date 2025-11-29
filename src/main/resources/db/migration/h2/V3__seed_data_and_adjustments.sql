-- =====================================================
-- V3__seed_data_and_adjustments.sql (H2 Compatible)
-- Sample data and final adjustments
-- =====================================================

-- =====================================================
-- Sample Venues
-- =====================================================
INSERT INTO venue (name, address, city, country, capacity) VALUES
('Teatro Metropolitano', 'Calle 41 #57-30', 'Medellín', 'Colombia', 1500),
('Estadio Atanasio Girardot', 'Cra. 74 #48-140', 'Medellín', 'Colombia', 45000),
('Auditorio Pablo Tobón Uribe', 'Cra. 40 #51-24', 'Medellín', 'Colombia', 1200),
('Centro de Convenciones Plaza Mayor', 'Calle 41 #55-80', 'Medellín', 'Colombia', 3000),
('Parque Explora', 'Cra. 52 #73-75', 'Medellín', 'Colombia', 800);

-- =====================================================
-- Sample Categories
-- =====================================================
INSERT INTO category (name, description, color, active) VALUES
('Conciertos', 'Eventos musicales en vivo', '#FF5733', TRUE),
('Teatro', 'Obras de teatro y performances', '#33FF57', TRUE),
('Deportes', 'Eventos deportivos y competiciones', '#3357FF', TRUE),
('Conferencias', 'Charlas y eventos corporativos', '#FF33F5', TRUE),
('Festivales', 'Eventos culturales y festivales', '#F5FF33', TRUE),
('Stand-up Comedy', 'Shows de comedia', '#33FFF5', TRUE);

-- =====================================================
-- Sample Events
-- =====================================================
INSERT INTO evento (name, description, event_date, categoria, status, venue_id, capacity, price) VALUES
('Concierto de Navidad', 'Gran concierto navideño con artistas locales', DATEADD(DAY, 30, CURRENT_TIMESTAMP), 'Conciertos', 'ACTIVE', 1, 1000, 85000.00),
('Festival de Jazz', 'Festival internacional de jazz', DATEADD(DAY, 45, CURRENT_TIMESTAMP), 'Festivales', 'ACTIVE', 3, 800, 120000.00),
('Partido de Fútbol Nacional', 'Final del campeonato nacional', DATEADD(DAY, 15, CURRENT_TIMESTAMP), 'Deportes', 'ACTIVE', 2, 40000, 50000.00),
('Obra: Romeo y Julieta', 'Clásico de Shakespeare', DATEADD(DAY, 20, CURRENT_TIMESTAMP), 'Teatro', 'ACTIVE', 1, 500, 75000.00),
('Tech Conference 2024', 'Conferencia de tecnología e innovación', DATEADD(DAY, 60, CURRENT_TIMESTAMP), 'Conferencias', 'DRAFT', 4, 2000, 150000.00);

-- =====================================================
-- Link Events with Categories
-- =====================================================
INSERT INTO evento_category (evento_id, category_id) VALUES
(1, 1), -- Concierto de Navidad -> Conciertos
(2, 5), -- Festival de Jazz -> Festivales
(2, 1), -- Festival de Jazz -> Conciertos
(3, 3), -- Partido de Fútbol -> Deportes
(4, 2), -- Romeo y Julieta -> Teatro
(5, 4); -- Tech Conference -> Conferencias
