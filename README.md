# TiqueteraCatalogo - Event Management System

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen)](https://spring.io/projects/spring-boot)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue)](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))
[![SOLID](https://img.shields.io/badge/Principles-SOLID-purple)](https://en.wikipedia.org/wiki/SOLID)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-red)](https://flywaydb.org/)
[![JPA](https://img.shields.io/badge/JPA-Specifications-blueviolet)](https://spring.io/projects/spring-data-jpa)

Event and venue management system built with **Hexagonal Architecture** and **SOLID principles**, featuring Spring Boot 3, JPA/Hibernate with optimized queries, Flyway migrations, and multi-database support (H2/MySQL).

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Technologies](#-technologies)
- [Project Structure](#-project-structure)
- [JPA Relationships](#-jpa-relationships)
- [Query Optimization](#-query-optimization)
- [Database Migrations](#-database-migrations)
- [Installation](#-installation)
- [API Endpoints](#-api-endpoints)
- [Swagger Documentation](#-swagger-documentation)
- [Design Principles](#-design-principles)
- [Usage Examples](#-usage-examples)

---

## ✨ Features

### Core Features
- ✅ **Full CRUD** for Events, Venues, and Categories
- ✅ **Hexagonal Architecture** (Ports & Adapters)
- ✅ **SOLID Principles** applied throughout the codebase
- ✅ **OpenAPI/Swagger** documentation
- ✅ **Bean Validation** for input validation
- ✅ **Centralized error handling** with secure messages
- ✅ **DTOs** for request/response separation
- ✅ **Mappers** for layer conversion

### Advanced JPA Features
- ✅ **JPA Relationships**: OneToMany, ManyToOne, ManyToMany
- ✅ **N+1 Prevention**: @BatchSize, @EntityGraph, JOIN FETCH
- ✅ **JpaSpecificationExecutor** for dynamic filtering
- ✅ **JPQL queries** (no native SQL)
- ✅ **Lazy/Eager loading** optimization

### Transaction & Migration
- ✅ **@Transactional** at Use Case layer (hexagonal compliance)
- ✅ **Flyway migrations** with versioned SQL scripts
- ✅ **Multi-database support**: H2 (dev) and MySQL (production)
- ✅ **Profile-based configuration** (dev, mysql, test)

---

## 🏗️ Architecture

This project implements **Hexagonal Architecture** (also known as Ports and Adapters), which separates business logic from implementation details.

### Main Layers

```
src/main/java/com/codeup/riwi/tiqueteracatalogo/
│
├── 📦 domain/                     # DOMAIN LAYER (Pure Business Logic)
│   ├── models/                    # Pure domain models (no frameworks)
│   │   ├── Evento.java
│   │   ├── Venue.java
│   │   ├── Category.java
│   │   └── EventStatus.java       # Event lifecycle enum
│   ├── ports/                     # Interfaces (contracts)
│   │   └── out/                   # Output ports
│   │       ├── EventoRepositoryPort.java
│   │       └── VenueRepositoryPort.java
│   └── excepcion/                 # Domain exceptions
│       └── RecursoNoEncontradoException.java
│
├── 📦 application/                # APPLICATION LAYER (Use Cases)
│   ├── usecases/                  # Use cases with @Transactional
│   │   ├── evento/
│   │   │   ├── CrearEventoUseCase.java
│   │   │   ├── ObtenerEventoUseCase.java
│   │   │   ├── ListarEventosUseCase.java
│   │   │   ├── ActualizarEventoUseCase.java
│   │   │   └── EliminarEventoUseCase.java
│   │   └── venue/
│   │       └── ... (same use cases)
│   ├── services/                  # Service orchestration
│   │   ├── EventoService.java
│   │   └── VenueService.java
│   ├── dto/                       # Data Transfer Objects
│   │   ├── EventoRequest.java
│   │   ├── EventoResponse.java
│   │   ├── VenueRequest.java
│   │   └── VenueResponse.java
│   └── mapper/                    # Mappers (DTO ↔ Domain)
│       ├── EventoMapper.java
│       └── VenueMapper.java
│
└── 📦 infrastructure/             # INFRASTRUCTURE LAYER
    ├── controllers/               # REST Controllers
    │   ├── EventController.java
    │   ├── VenueController.java
    │   └── advice/                # Exception handlers
    │       ├── GlobalExceptionHandler.java
    │       └── ErrorResponse.java
    ├── adapters/                  # Adapters (implement ports)
    │   ├── EventoRepositoryAdapter.java
    │   └── VenueRepositoryAdapter.java
    ├── repositories/              # JPA Repositories
    │   ├── EventoJpaRepository.java
    │   ├── VenueJpaRepository.java
    │   ├── CategoryJpaRepository.java
    │   └── specification/         # JPA Specifications
    │       ├── EventoSpecification.java
    │       ├── EventoSpecificationBuilder.java
    │       ├── VenueSpecification.java
    │       └── VenueSpecificationBuilder.java
    ├── entities/                  # JPA Entities
    │   ├── EventoJpaEntity.java   # ManyToOne, ManyToMany
    │   ├── VenueJpaEntity.java    # OneToMany
    │   └── CategoryJpaEntity.java # ManyToMany (inverse)
    ├── dto/                       # Infrastructure DTOs
    │   ├── EventoFilterDTO.java
    │   └── VenueFilterDTO.java
    └── config/                    # Configuration
        ├── OpenApiConfig.java
        └── UseCaseConfiguration.java  # @EnableTransactionManagement
```

### Data Flow

```
HTTP Request
     ↓
[Controller] ← Input Adapter
     ↓
[Service] ← Orchestration
     ↓
[Use Case] ← Business Logic with @Transactional
     ↓
[Repository Port] ← Interface (Output Port)
     ↓
[Repository Adapter] ← Port Implementation
     ↓
[JPA Repository] ← Spring Data JPA + Specifications
     ↓
[Database] ← H2 (dev) / MySQL (prod)
```

---

## 🛠️ Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming language |
| Spring Boot | 3.5.7 | Main framework |
| Spring Data JPA | 3.5.7 | Data persistence + Specifications |
| Hibernate | 6.6.33 | ORM with N+1 optimization |
| Flyway | 10.x | Database migrations |
| H2 Database | 2.3.232 | In-memory database (dev) |
| MySQL | 8.0 | Production database |
| Springdoc OpenAPI | 2.7.0 | Swagger documentation |
| Lombok | 1.18.36 | Boilerplate reduction |
| Bean Validation | 3.0 | Input validation |
| Maven | 3.9+ | Dependency management |

---

## 🔗 JPA Relationships

### Entity Relationship Diagram

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     Venue       │       │     Evento      │       │    Category     │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id              │       │ id              │       │ id              │
│ name            │       │ name            │       │ name            │
│ address         │       │ description     │       │ description     │
│ city            │       │ eventDate       │       │ color           │
│ country         │       │ status          │       │ active          │
│ capacity        │       │ capacity        │       │ createdAt       │
│ createdAt       │       │ price           │       │ updatedAt       │
│ updatedAt       │       │ createdAt       │       └─────────────────┘
│                 │       │ updatedAt       │               ▲
│ eventos ────────┼──────►│ venue ◄─────────┤               │
│ (OneToMany)     │       │ (ManyToOne)     │               │
└─────────────────┘       │                 │               │
                          │ categories ─────┼───────────────┘
                          │ (ManyToMany)    │
                          └─────────────────┘
```

### Relationship Details

#### 1. Venue ↔ Evento (OneToMany / ManyToOne)

```java
// VenueJpaEntity.java
@OneToMany(mappedBy = "venue", 
           cascade = CascadeType.ALL, 
           orphanRemoval = true,
           fetch = FetchType.LAZY)
@BatchSize(size = 20)
@Fetch(FetchMode.SUBSELECT)
private Set<EventoJpaEntity> eventos = new HashSet<>();

// EventoJpaEntity.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "venue_id", nullable = false)
private VenueJpaEntity venue;
```

#### 2. Evento ↔ Category (ManyToMany)

```java
// EventoJpaEntity.java (Owner side)
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "evento_category",
    joinColumns = @JoinColumn(name = "evento_id"),
    inverseJoinColumns = @JoinColumn(name = "category_id")
)
@BatchSize(size = 20)
private Set<CategoryJpaEntity> categories = new HashSet<>();

// CategoryJpaEntity.java (Inverse side)
@ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
private Set<EventoJpaEntity> eventos = new HashSet<>();
```

### Event Status Enum

```java
public enum EventStatus {
    ACTIVE("Activo"),
    CANCELLED("Cancelado"),
    POSTPONED("Pospuesto"),
    COMPLETED("Completado"),
    DRAFT("Borrador"),
    SOLD_OUT("Agotado");
}
```

---

## ⚡ Query Optimization

### N+1 Prevention Strategies

#### 1. @BatchSize for Collections
```java
@BatchSize(size = 20)
@Fetch(FetchMode.SUBSELECT)
private Set<EventoJpaEntity> eventos;
```

#### 2. @EntityGraph for Eager Fetching
```java
@EntityGraph(attributePaths = {"venue", "categories"})
@Query("SELECT e FROM EventoJpaEntity e WHERE e.id = :id")
Optional<EventoJpaEntity> findByIdWithDetails(@Param("id") Long id);
```

#### 3. JOIN FETCH in JPQL
```java
@Query("SELECT e FROM EventoJpaEntity e " +
       "JOIN FETCH e.venue v " +
       "WHERE v.id = :venueId " +
       "ORDER BY e.eventDate ASC")
List<EventoJpaEntity> findByVenueId(@Param("venueId") Long venueId);
```

#### 4. Hibernate Configuration
```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=20
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

### JPA Specifications for Dynamic Filtering

```java
// Build dynamic queries
Specification<EventoJpaEntity> spec = EventoSpecification.hasStatus(ACTIVE)
    .and(EventoSpecification.inDateRange(start, end))
    .and(EventoSpecification.byVenueId(1L))
    .and(EventoSpecification.byCategoryName("Music"));

repository.findAll(spec, pageable);
```

#### Available Specifications

**EventoSpecification:**
- `hasStatus(EventStatus status)`
- `hasStatusIn(List<EventStatus> statuses)`
- `inDateRange(LocalDateTime start, LocalDateTime end)`
- `isUpcoming()` / `isPast()`
- `byVenueId(Long venueId)`
- `byVenueCity(String city)`
- `byCategoryId(Long categoryId)`
- `byCategoryName(String name)`
- `nameLike(String name)`
- `searchByKeyword(String keyword)`
- `inPriceRange(Double min, Double max)`
- `inCapacityRange(Integer min, Integer max)`

**VenueSpecification:**
- `byCity(String city)`
- `byCountry(String country)`
- `addressContains(String address)`
- `minCapacity(Integer min)`
- `capacityBetween(Integer min, Integer max)`
- `hasEvents()` / `hasNoEvents()`
- `searchByKeyword(String keyword)`

---

## 📊 Database Migrations

### Flyway Configuration

Migrations are organized by database profile:

```
src/main/resources/db/migration/
├── h2/                          # H2 (Development)
│   ├── V1__init_schema.sql
│   ├── V2__add_constraints_and_indexes.sql
│   └── V3__seed_data_and_adjustments.sql
└── mysql/                       # MySQL (Production)
    ├── V1__init_schema.sql
    ├── V2__add_constraints_and_indexes.sql
    └── V3__seed_data_and_adjustments.sql
```

### Migration Scripts

#### V1 - Initial Schema
- Creates `venue`, `evento`, `category` tables
- Creates `evento_category` junction table for ManyToMany

#### V2 - Constraints and Indexes
- Foreign key constraints
- Unique constraints (venue name per city, category name)
- Performance indexes for common queries

#### V3 - Seed Data
- Sample venues, categories, and events
- Event-category relationships

### Profile Configuration

```properties
# application-dev.properties (H2)
spring.flyway.locations=classpath:db/migration/h2

# application-mysql.properties (MySQL)
spring.flyway.locations=classpath:db/migration/mysql
```

---

## 📁 Project Structure

### Domain Layer (Business Core)

**Characteristics:**
- ✅ No framework dependencies
- ✅ Pure models (POJOs)
- ✅ Defines interfaces (ports)
- ✅ Contains business exceptions

**Example:**
```java
// Pure domain model
public class Evento {
    private Long id;
    private String name;
    private LocalDateTime eventDate;
    private EventStatus status;
    private List<Category> categories;
    // No JPA annotations!
}

// Output Port (interface)
public interface EventoRepositoryPort {
    Evento save(Evento evento);
    Optional<Evento> findById(Long id);
    List<Evento> findByVenueId(Long venueId);
}
```

### Application Layer (Use Cases)

**Characteristics:**
- ✅ Contains business logic
- ✅ Depends only on ports (interfaces)
- ✅ Framework independent
- ✅ @Transactional at this layer only

**Example:**
```java
public class CrearEventoUseCase {
    private final EventoRepositoryPort eventoRepository;
    private final VenueRepositoryPort venueRepository;
    
    @Transactional(propagation = Propagation.REQUIRED, 
                   isolation = Isolation.READ_COMMITTED)
    public Evento ejecutar(Evento evento) {
        if (!venueRepository.existsById(evento.getVenueId())) {
            throw new IllegalArgumentException("Venue not found");
        }
        return eventoRepository.save(evento);
    }
}
```

### Infrastructure Layer (Implementation Details)

**Characteristics:**
- ✅ Implements ports
- ✅ Contains technical details (JPA, REST, etc.)
- ✅ Interchangeable adapters
- ✅ Controllers, Repositories, Entities

**Example:**
```java
@Component
public class EventoRepositoryAdapter implements EventoRepositoryPort {
    private final EventoJpaRepository jpaRepository;
    
    // No @Transactional here - only in Use Cases!
    @Override
    public Evento save(Evento evento) {
        EventoJpaEntity entity = toEntity(evento);
        EventoJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }
}
```

---

## 🚀 Installation

### Prerequisites

- Java 17 or higher
- Maven 3.9+ (included as `mvnw`)
- (Optional) MySQL 8.0 for production profile
- (Optional) Docker for MySQL container

### Steps

1. **Clone the repository**
```bash
git clone https://github.com/AlvaroN-dev/events-academic.git
cd TiqueteraCatalogo
```

2. **Compile the project**
```bash
./mvnw clean compile
```

3. **Run with development profile (H2)**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

4. **Run with MySQL profile (production)**
```bash
# Start MySQL with Docker
docker run --name tiquetera-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=tiqueteradb \
  -p 3306:3306 -d mysql:8.0

# Run application
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

5. **Verify it's running**
- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console (dev only): http://localhost:8080/h2-console

### Profile Configuration

| Profile | Database | Flyway Migrations | Use Case |
|---------|----------|-------------------|----------|
| `dev` | H2 (in-memory) | `db/migration/h2/` | Development |
| `mysql` | MySQL 8.0 | `db/migration/mysql/` | Production |
| `test` | H2 (in-memory) | `db/migration/h2/` | Testing |

---

## 📡 API Endpoints

### Events (`/api/events`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/events` | List all events |
| GET | `/api/events/{id}` | Get event by ID |
| GET | `/api/events/venue/{venueId}` | List events by venue |
| POST | `/api/events` | Create new event |
| PUT | `/api/events/{id}` | Update event |
| DELETE | `/api/events/{id}` | Delete event |

### Venues (`/api/venues`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/venues` | List all venues |
| GET | `/api/venues/{id}` | Get venue by ID |
| GET | `/api/venues/city/{city}` | List venues by city |
| POST | `/api/venues` | Create new venue |
| PUT | `/api/venues/{id}` | Update venue |
| DELETE | `/api/venues/{id}` | Delete venue |

---

## 📚 Swagger Documentation

Access interactive documentation at: **http://localhost:8080/swagger-ui.html**

Documentation features:
- ✅ Request/response examples
- ✅ Detailed schemas
- ✅ HTTP response codes
- ✅ Documented validations
- ✅ Live testing ("Try it out")

---

## 🎯 Design Principles

### Hexagonal Architecture ✅

1. **Domain at the center**: Business logic doesn't depend on frameworks
2. **Ports**: Interfaces that define contracts
3. **Adapters**: Interchangeable implementations
4. **Dependency inversion**: Infrastructure depends on domain

### SOLID Principles ✅

#### 1. Single Responsibility Principle (SRP)
Each class has a single responsibility:
- `CrearEventoUseCase`: Only creates events
- `EventoRepositoryAdapter`: Only adapts persistence
- `EventController`: Only handles HTTP

#### 2. Open/Closed Principle (OCP)
Open for extension, closed for modification:
```java
// We can add new adapters without modifying use cases
public class EventoMongoAdapter implements EventoRepositoryPort { }
public class EventoRedisAdapter implements EventoRepositoryPort { }
```

#### 3. Liskov Substitution Principle (LSP)
Adapters are interchangeable:
```java
EventoRepositoryPort repo = new EventoRepositoryAdapter();  // JPA
EventoRepositoryPort repo = new EventoMongoAdapter();       // MongoDB
// Use case works with either
```

#### 4. Interface Segregation Principle (ISP)
Specific and cohesive interfaces:
```java
public interface EventoRepositoryPort { /* event methods only */ }
public interface VenueRepositoryPort { /* venue methods only */ }
```

#### 5. Dependency Inversion Principle (DIP)
Dependencies on abstractions:
```java
public class CrearEventoUseCase {
    private final EventoRepositoryPort repository;  // ✅ Interface
    // NOT: private final EventoRepositoryAdapter repository;  // ❌ Implementation
}
```

### Transaction Management ✅

Transactions are managed at the **Use Case layer** only (hexagonal architecture compliance):

```java
// ✅ CORRECT: @Transactional in Use Case
public class CrearEventoUseCase {
    @Transactional(propagation = Propagation.REQUIRED, 
                   isolation = Isolation.READ_COMMITTED)
    public Evento ejecutar(Evento evento) { ... }
}

// ✅ CORRECT: Read-only for queries
public class ListarEventosUseCase {
    @Transactional(readOnly = true, 
                   propagation = Propagation.SUPPORTS)
    public List<Evento> ejecutar() { ... }
}

// ❌ WRONG: No @Transactional in Adapters
@Component
public class EventoRepositoryAdapter implements EventoRepositoryPort {
    // No @Transactional here!
}
```

---

## 💡 Usage Examples

### 1. Create a Venue

**Request:**
```bash
POST http://localhost:8080/api/venues
Content-Type: application/json

{
  "name": "Teatro Nacional",
  "address": "Calle 71 #10-25",
  "city": "Bogotá",
  "country": "Colombia",
  "capacity": 1500
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Teatro Nacional",
  "address": "Calle 71 #10-25",
  "city": "Bogotá",
  "country": "Colombia",
  "capacity": 1500
}
```

### 2. Create an Event

**Request:**
```bash
POST http://localhost:8080/api/events
Content-Type: application/json

{
  "name": "Rock Concert",
  "description": "Great rock concert",
  "eventDate": "2025-12-15T20:00:00",
  "categoria": "Music",
  "venueId": 1,
  "capacity": 1000,
  "price": 80000.0
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Rock Concert",
  "description": "Great rock concert",
  "eventDate": "2025-12-15T20:00:00",
  "categoria": "Music",
  "status": "ACTIVE",
  "venueId": 1,
  "capacity": 1000,
  "price": 80000.0
}
```

### 3. List Events by Venue

**Request:**
```bash
GET http://localhost:8080/api/events/venue/1
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Rock Concert",
    "eventDate": "2025-12-15T20:00:00",
    "status": "ACTIVE",
    "venueId": 1,
    ...
  }
]
```

### 4. Error Handling

**Error 404 - Resource not found:**
```json
{
  "timestamp": "2025-11-29T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Event with ID 999 not found",
  "path": "/api/events/999"
}
```

**Error 400 - Validation:**
```json
{
  "timestamp": "2025-11-29T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/events",
  "details": {
    "name": "Event name is required",
    "eventDate": "Event date is required"
  }
}
```

---

## 🔒 Security

- ✅ **Generic error messages**: No SQL details or stack traces exposed
- ✅ **Validations**: Bean Validation on all DTOs
- ✅ **Centralized exception handling**: `GlobalExceptionHandler`
- ✅ **Open-in-view disabled**: Better performance and security

---

## 📝 Configuration

### Database Profiles

#### H2 (Development)
```properties
# application-dev.properties
spring.datasource.url=jdbc:h2:mem:tiqueteradb;MODE=MySQL
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.flyway.locations=classpath:db/migration/h2
```

**Access H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:tiqueteradb`
- User: `sa`
- Password: (empty)

#### MySQL (Production)
```properties
# application-mysql.properties
spring.datasource.url=jdbc:mysql://localhost:3306/tiqueteradb
spring.datasource.username=root
spring.datasource.password=root
spring.flyway.locations=classpath:db/migration/mysql
```

### Performance Configuration

```properties
# Hibernate N+1 optimization
spring.jpa.properties.hibernate.default_batch_fetch_size=20
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Query statistics (dev only)
spring.jpa.properties.hibernate.generate_statistics=true
spring.jpa.properties.hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS=25

# Disable open-in-view (recommended)
spring.jpa.open-in-view=false
```

---

## 🧪 Testing

### Test with cURL

```bash
# Create venue
curl -X POST http://localhost:8080/api/venues \
  -H "Content-Type: application/json" \
  -d '{"name":"Teatro","address":"Calle 1","city":"Bogotá","country":"Colombia","capacity":500}'

# Create event
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"name":"Concert","description":"Show","eventDate":"2025-12-15T20:00:00","categoria":"Music","venueId":1,"capacity":500,"price":50000}'

# List all events
curl http://localhost:8080/api/events

# List events by venue
curl http://localhost:8080/api/events/venue/1

# Get venues by city
curl http://localhost:8080/api/venues/city/Bogotá
```

### Test with Swagger UI

1. Go to http://localhost:8080/swagger-ui.html
2. Select an endpoint
3. Click "Try it out"
4. Fill in the example JSON
5. Click "Execute"

---

## 📊 Benefits of this Architecture

### Maintainability
- Organized and easy-to-understand code
- Clear responsibilities
- Localized changes

### Testability
- Easy to create unit tests with mocks
- Independent use cases
- Ports allow injecting fake implementations

### Flexibility
- Easy to switch from JPA to MongoDB
- Easy to add new adapters
- Protected business logic

### Scalability
- Decoupled components
- Easy to add new features
- Architecture ready for microservices

### Performance
- N+1 query prevention
- Optimized batch loading
- Lazy loading by default
- Dynamic filtering with Specifications

---

## 👥 Author

**Tiquetera Team**
- Repository: [AlvaroN-dev/events-academic](https://github.com/AlvaroN-dev/events-academic)
- Branch: `feat/USM4`

---

## 📄 License

This project is under the Apache 2.0 license - see the [LICENSE](LICENSE) file for details.

---

## 🔗 Useful Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Hexagonal Architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Flyway Migrations](https://flywaydb.org/)
- [OpenAPI Specification](https://swagger.io/specification/)

---

**Questions or suggestions?** Open an issue on GitHub or contact the development team.
