-- =====================================================
-- V2__add_constraints_and_indexes.sql
-- Add foreign keys, unique constraints, and indexes
-- =====================================================

-- =====================================================
-- Foreign Key Constraints
-- =====================================================

-- FK: evento -> venue (ManyToOne relationship)
ALTER TABLE evento
    ADD CONSTRAINT fk_evento_venue 
    FOREIGN KEY (venue_id) 
    REFERENCES venue(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: evento_category -> evento
ALTER TABLE evento_category
    ADD CONSTRAINT fk_evento_category_evento 
    FOREIGN KEY (evento_id) 
    REFERENCES evento(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- FK: evento_category -> category
ALTER TABLE evento_category
    ADD CONSTRAINT fk_evento_category_category 
    FOREIGN KEY (category_id) 
    REFERENCES category(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

-- =====================================================
-- Unique Constraints
-- =====================================================

-- Unique venue name per city
ALTER TABLE venue
    ADD CONSTRAINT uk_venue_name_city UNIQUE (name, city);

-- Category name must be unique
ALTER TABLE category
    ADD CONSTRAINT uk_category_name UNIQUE (name);

-- =====================================================
-- Indexes for Performance Optimization
-- =====================================================

-- Index on evento.venue_id for faster venue-based queries
CREATE INDEX idx_evento_venue_id ON evento(venue_id);

-- Index on evento.event_date for date-based queries
CREATE INDEX idx_evento_event_date ON evento(event_date);

-- Index on evento.status for status-based filtering
CREATE INDEX idx_evento_status ON evento(status);

-- Index on evento.categoria for category filtering
CREATE INDEX idx_evento_categoria ON evento(categoria);

-- Composite index for common query: events by venue and status
CREATE INDEX idx_evento_venue_status ON evento(venue_id, status);

-- Composite index for date range queries with status
CREATE INDEX idx_evento_date_status ON evento(event_date, status);

-- Index on venue.city for location-based queries
CREATE INDEX idx_venue_city ON venue(city);

-- Index on venue.capacity for capacity filtering
CREATE INDEX idx_venue_capacity ON venue(capacity);

-- Composite index for city and capacity queries
CREATE INDEX idx_venue_city_capacity ON venue(city, capacity);

-- Index on category.active for filtering active categories
CREATE INDEX idx_category_active ON category(active);
