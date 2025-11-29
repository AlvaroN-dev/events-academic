-- =====================================================
-- V2__add_constraints_and_indexes.sql (H2 Compatible)
-- Add foreign keys, unique constraints, and indexes
-- =====================================================

-- =====================================================
-- Foreign Key Constraints
-- =====================================================

-- FK: evento -> venue
ALTER TABLE evento 
ADD CONSTRAINT fk_evento_venue 
FOREIGN KEY (venue_id) REFERENCES venue(id);

-- FK: evento_category -> evento
ALTER TABLE evento_category 
ADD CONSTRAINT fk_evento_category_evento 
FOREIGN KEY (evento_id) REFERENCES evento(id) ON DELETE CASCADE;

-- FK: evento_category -> category
ALTER TABLE evento_category 
ADD CONSTRAINT fk_evento_category_category 
FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE;

-- =====================================================
-- Unique Constraints
-- =====================================================

-- Unique venue name per city
ALTER TABLE venue 
ADD CONSTRAINT uk_venue_name_city UNIQUE (name, city);

-- Unique category name
ALTER TABLE category 
ADD CONSTRAINT uk_category_name UNIQUE (name);

-- =====================================================
-- Performance Indexes
-- =====================================================

-- Venue indexes
CREATE INDEX idx_venue_city ON venue(city);
CREATE INDEX idx_venue_name ON venue(name);
CREATE INDEX idx_venue_capacity ON venue(capacity);

-- Event indexes
CREATE INDEX idx_evento_date ON evento(event_date);
CREATE INDEX idx_evento_status ON evento(status);
CREATE INDEX idx_evento_venue ON evento(venue_id);
CREATE INDEX idx_evento_categoria ON evento(categoria);
CREATE INDEX idx_evento_date_status ON evento(event_date, status);

-- Category indexes
CREATE INDEX idx_category_name ON category(name);
CREATE INDEX idx_category_active ON category(active);
