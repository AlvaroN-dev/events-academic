# TiqueteraCatalogo - Event Management System

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-green)](https://spring.io/projects/spring-security)
[![JWT](https://img.shields.io/badge/JWT-0.12.6-yellow)](https://github.com/jwtk/jjwt)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue)](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))
[![SOLID](https://img.shields.io/badge/Principles-SOLID-purple)](https://en.wikipedia.org/wiki/SOLID)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-red)](https://flywaydb.org/)
[![JPA](https://img.shields.io/badge/JPA-Specifications-blueviolet)](https://spring.io/projects/spring-data-jpa)

Event and venue management system built with **Hexagonal Architecture** and **SOLID principles**, featuring Spring Boot 3, **JWT Authentication**, JPA/Hibernate with optimized queries, Flyway migrations, structured logging, and multi-database support (H2/MySQL).

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Security & Authentication](#-security--authentication)
- [Technologies](#-technologies)
- [Project Structure](#-project-structure)
- [JPA Relationships](#-jpa-relationships)
- [Query Optimization](#-query-optimization)
- [Logging System](#-logging-system)
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
- ✅ **OpenAPI/Swagger** documentation with JWT support
- ✅ **Bean Validation** with groups (OnCreate, OnUpdate)
- ✅ **Centralized error handling** with RFC 7807 ProblemDetail
- ✅ **DTOs** for request/response separation
- ✅ **Mappers** for layer conversion

### Security Features
- ✅ **JWT Authentication** with access tokens
- ✅ **Role-Based Access Control (RBAC)** with @PreAuthorize
- ✅ **Stateless Security** (no sessions)
- ✅ **CORS Configuration** for cross-origin requests
- ✅ **Password Encryption** with BCrypt
- ✅ **Secure Error Responses** (no stack traces exposed)

### Advanced JPA Features
- ✅ **JPA Relationships**: OneToMany, ManyToOne, ManyToMany
- ✅ **N+1 Prevention**: @BatchSize, @EntityGraph, JOIN FETCH
- ✅ **JpaSpecificationExecutor** for dynamic filtering
- ✅ **JPQL queries** (no native SQL)
- ✅ **Lazy/Eager loading** optimization

### Logging & Monitoring
- ✅ **Structured Logging** with SLF4J/Logback
- ✅ **MDC Context** (traceId, userId, requestPath)
- ✅ **Security Event Logging** (login, register, failures)
- ✅ **Performance Logging** for slow operations

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
├── 📦 domain/                          # DOMAIN LAYER (Pure Business Logic)
│   ├── models/                         # Pure domain models (no frameworks)
│   │   ├── Evento.java
│   │   ├── Venue.java
│   │   ├── Category.java
│   │   ├── EventStatus.java
│   │   ├── AuthResult.java             # Authentication result model
│   │   ├── CredencialesLogin.java      # Login credentials model
│   │   └── RegistroUsuario.java        # User registration model
│   ├── ports/
│   │   ├── in/                         # INPUT PORTS (Use Case interfaces)
│   │   │   ├── RegistrarUsuarioUseCase.java
│   │   │   ├── AutenticarUsuarioUseCase.java
│   │   │   └── RefrescarTokenUseCase.java
│   │   └── out/                        # OUTPUT PORTS (Repository interfaces)
│   │       ├── EventoRepositoryPort.java
│   │       ├── VenueRepositoryPort.java
│   │       ├── UserRepositoryPort.java
│   │       ├── JwtPort.java
│   │       └── PasswordEncoderPort.java
│   └── excepcion/                      # Domain exceptions
│       └── RecursoNoEncontradoException.java
│
├── 📦 application/                     # APPLICATION LAYER (Use Cases)
│   ├── usecases/
│   │   ├── evento/
│   │   │   ├── CrearEventoUseCase.java
│   │   │   ├── ObtenerEventoUseCase.java
│   │   │   ├── ListarEventosUseCase.java
│   │   │   ├── ActualizarEventoUseCase.java
│   │   │   └── EliminarEventoUseCase.java
│   │   ├── venue/
│   │   │   └── ... (same use cases)
│   │   └── auth/                       # Authentication Use Cases
│   │       ├── RegistrarUsuarioUseCaseImpl.java
│   │       ├── AutenticarUsuarioUseCaseImpl.java
│   │       └── RefrescarTokenUseCaseImpl.java
│   ├── services/
│   │   ├── EventoService.java
│   │   └── VenueService.java
│   ├── dto/
│   │   ├── EventoRequest.java
│   │   ├── EventoResponse.java
│   │   ├── VenueRequest.java
│   │   └── VenueResponse.java
│   ├── mapper/
│   │   ├── EventoMapper.java
│   │   ├── VenueMapper.java
│   │   └── AuthMapper.java             # Auth DTO ↔ Domain mapper
│   └── validation/
│       └── ValidationGroups.java       # OnCreate, OnUpdate groups
│
└── 📦 infrastructure/                  # INFRASTRUCTURE LAYER
    ├── controllers/
    │   ├── EventController.java        # @PreAuthorize protected
    │   ├── VenueController.java        # @PreAuthorize protected
    │   ├── AuthController.java         # Public auth endpoints
    │   └── advice/
    │       └── GlobalExceptionHandler.java
    ├── adapters/                       # Port implementations
    │   ├── EventoRepositoryAdapter.java
    │   ├── VenueRepositoryAdapter.java
    │   ├── UserRepositoryAdapter.java
    │   ├── JwtAdapter.java
    │   └── PasswordEncoderAdapter.java
    ├── repositories/
    │   ├── EventoJpaRepository.java
    │   ├── VenueJpaRepository.java
    │   ├── UserRepository.java
    │   ├── RoleRepository.java
    │   └── specification/
    │       ├── EventoSpecification.java
    │       └── VenueSpecification.java
    ├── entities/
    │   ├── EventoJpaEntity.java
    │   ├── VenueJpaEntity.java
    │   ├── CategoryJpaEntity.java
    │   ├── UserEntity.java             # Implements UserDetails
    │   └── RoleEntity.java             # ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR
    ├── dto/auth/
    │   ├── RegisterRequest.java
    │   ├── LoginRequest.java
    │   └── AuthResponse.java
    └── config/
        ├── OpenApiConfig.java          # Swagger with JWT security
        ├── AuthUseCasesConfig.java     # Auth beans configuration
        ├── UseCaseConfiguration.java
        ├── logging/
        │   └── LoggingService.java     # Structured logging service
        └── security/
            ├── SecurityConfig.java     # SecurityFilterChain, CORS
            ├── JwtService.java         # JWT generation/validation
            ├── JwtAuthenticationFilter.java
            └── CustomUserDetailsService.java
```

### Authentication Data Flow

```
HTTP Request (with JWT)
         ↓
[JwtAuthenticationFilter] ← Validates token
         ↓
[SecurityContext] ← Sets authentication
         ↓
[Controller] ← @PreAuthorize checks roles
         ↓
[Use Case] ← Business logic via Input Port
         ↓
[Repository Port] ← Output Port interface
         ↓
[Adapter] ← Port implementation
         ↓
[Database]
```

### Authentication Flow

```
┌─────────────────────────────────────────────────────────────────────┐
│                        AUTHENTICATION FLOW                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1. REGISTER (/auth/register)                                       │
│     ┌──────────────┐    ┌──────────────────────┐    ┌────────────┐ │
│     │ AuthController│───►│RegistrarUsuarioUseCase│───►│UserRepoPort│ │
│     └──────────────┘    └──────────────────────┘    └────────────┘ │
│            │                       │                                │
│            │                       ▼                                │
│            │              ┌─────────────┐                          │
│            │              │  JwtPort    │ ← Generate JWT            │
│            │              └─────────────┘                          │
│            │                       │                                │
│            ◄───────────────────────┘                                │
│            ▼                                                        │
│     { accessToken, user info, roles }                              │
│                                                                     │
│  2. LOGIN (/auth/login)                                             │
│     ┌──────────────┐    ┌───────────────────────┐   ┌────────────┐ │
│     │ AuthController│───►│AutenticarUsuarioUseCase│──►│UserRepoPort│ │
│     └──────────────┘    └───────────────────────┘   └────────────┘ │
│            │                       │                                │
│            │                       ▼                                │
│            │           ┌──────────────────┐                        │
│            │           │PasswordEncoderPort│ ← Verify password      │
│            │           └──────────────────┘                        │
│            │                       │                                │
│            │                       ▼                                │
│            │              ┌─────────────┐                          │
│            │              │  JwtPort    │ ← Generate JWT            │
│            │              └─────────────┘                          │
│            ◄───────────────────────┘                                │
│            ▼                                                        │
│     { accessToken, user info, roles }                              │
│                                                                     │
│  3. PROTECTED REQUEST                                               │
│     ┌──────────────┐    ┌─────────────────────┐                    │
│     │    Request   │───►│JwtAuthenticationFilter│                   │
│     │ (with Bearer)│    └─────────────────────┘                    │
│            │                       │                                │
│            │                       ▼                                │
│            │              ┌─────────────┐                          │
│            │              │ JwtService  │ ← Validate token          │
│            │              └─────────────┘                          │
│            │                       │                                │
│            │                       ▼                                │
│            │           ┌────────────────────┐                      │
│            │           │ SecurityContext    │ ← Set authentication  │
│            │           └────────────────────┘                      │
│            │                       │                                │
│            │                       ▼                                │
│            │           ┌────────────────────┐                      │
│            │           │ @PreAuthorize      │ ← Check roles         │
│            │           └────────────────────┘                      │
│            ◄───────────────────────┘                                │
│            ▼                                                        │
│     Controller → Use Case → Response                               │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔒 Security & Authentication

### JWT Configuration

The system uses **JSON Web Tokens (JWT)** for stateless authentication:

```properties
# application.properties
jwt.secret=your-256-bit-secret-key-here-minimum-32-characters
jwt.expiration=86400000        # 24 hours in milliseconds
jwt.refresh-expiration=604800000  # 7 days
```

### User Roles

| Role | Description | Permissions |
|------|-------------|-------------|
| `ROLE_USER` | Standard user | Create/Update events |
| `ROLE_MODERATOR` | Content moderator | Create/Update events and venues |
| `ROLE_ADMIN` | Administrator | Full access (CRUD all resources) |

### Protected Endpoints

#### Events (`/api/events`)
| Method | Endpoint | Required Role |
|--------|----------|---------------|
| GET | `/api/events` | Public |
| GET | `/api/events/{id}` | Public |
| POST | `/api/events` | USER, MODERATOR, ADMIN |
| PUT | `/api/events/{id}` | USER, MODERATOR, ADMIN |
| DELETE | `/api/events/{id}` | ADMIN only |

#### Venues (`/api/venues`)
| Method | Endpoint | Required Role |
|--------|----------|---------------|
| GET | `/api/venues` | Public |
| GET | `/api/venues/{id}` | Public |
| POST | `/api/venues` | MODERATOR, ADMIN |
| PUT | `/api/venues/{id}` | MODERATOR, ADMIN |
| DELETE | `/api/venues/{id}` | ADMIN only |

### Security Implementation

#### SecurityFilterChain
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

#### Role-Based Access with @PreAuthorize
```java
@RestController
@RequestMapping("/api/events")
public class EventController {
    
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<EventoResponse> createEvent(@RequestBody EventoRequest request) {
        // Only authenticated users with proper roles can create
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        // Only ADMIN can delete
    }
}
```

### Authentication Ports (Hexagonal Architecture)

The authentication system follows hexagonal architecture with proper port definitions:

#### Input Ports (domain/ports/in/)
```java
public interface RegistrarUsuarioUseCase {
    AuthResult ejecutar(RegistroUsuario registro);
}

public interface AutenticarUsuarioUseCase {
    AuthResult ejecutar(CredencialesLogin credenciales);
}

public interface RefrescarTokenUseCase {
    AuthResult ejecutar(String refreshToken);
}
```

#### Output Ports (domain/ports/out/)
```java
public interface UserRepositoryPort {
    Optional<UserDomainData> findByEmail(String email);
    Optional<UserDomainData> findByUsernameOrEmail(String usernameOrEmail);
    boolean existsByEmail(String email);
    UserDomainData save(UserDomainData userData);
}

public interface JwtPort {
    String generateToken(UserDomainData userData);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDomainData userData);
    long getExpirationInSeconds();
}

public interface PasswordEncoderPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
```

---

## 🛠️ Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming language |
| Spring Boot | 3.5.7 | Main framework |
| Spring Security | 6.x | Authentication & Authorization |
| JJWT | 0.12.6 | JWT token handling |
| Spring Data JPA | 3.5.7 | Data persistence + Specifications |
| Hibernate | 6.6.33 | ORM with N+1 optimization |
| Flyway | 10.x | Database migrations |
| H2 Database | 2.3.232 | In-memory database (dev) |
| MySQL | 8.0 | Production database |
| Springdoc OpenAPI | 2.7.0 | Swagger documentation |
| SLF4J + Logback | 2.x | Structured logging |
| Lombok | 1.18.36 | Boilerplate reduction |
| Bean Validation | 3.0 | Input validation |
| Maven | 3.9+ | Dependency management |

---

## 📁 Project Structure

### Domain Layer (Business Core)

**Characteristics:**
- ✅ No framework dependencies
- ✅ Pure models (POJOs)
- ✅ Defines Input & Output Ports
- ✅ Contains business exceptions

**Example - Domain Models:**
```java
// Pure domain model for authentication
public class AuthResult {
    private boolean success;
    private String accessToken;
    private Long userId;
    private String username;
    private Set<String> roles;
    
    public static AuthResult success(String token, Long userId, 
                                     String username, Set<String> roles) {
        // Factory method
    }
}
```

### Application Layer (Use Cases)

**Characteristics:**
- ✅ Implements Input Ports
- ✅ Contains business logic
- ✅ Depends only on Output Ports (interfaces)
- ✅ @Transactional at this layer only

**Example - Authentication Use Case:**
```java
public class AutenticarUsuarioUseCaseImpl implements AutenticarUsuarioUseCase {
    
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtPort jwtPort;
    
    @Override
    @Transactional(readOnly = true)
    public AuthResult ejecutar(CredencialesLogin credenciales) {
        UserDomainData user = userRepository
            .findByUsernameOrEmail(credenciales.getUsernameOrEmail())
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        
        if (!passwordEncoder.matches(credenciales.getPassword(), user.password())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        
        String token = jwtPort.generateToken(user);
        
        return AuthResult.success(token, user.id(), user.username(), user.roles());
    }
}
```

### Infrastructure Layer (Implementation Details)

**Characteristics:**
- ✅ Implements Output Ports (Adapters)
- ✅ Contains technical details (JPA, JWT, Security)
- ✅ Interchangeable adapters
- ✅ Controllers, Repositories, Entities

**Example - JWT Adapter:**
```java
@Component
public class JwtAdapter implements JwtPort {
    
    private final JwtService jwtService;
    
    @Override
    public String generateToken(UserDomainData userData) {
        UserEntity userEntity = toUserEntity(userData);
        return jwtService.generateToken(userEntity);
    }
    
    @Override
    public boolean isTokenValid(String token, UserDomainData userData) {
        UserEntity userEntity = toUserEntity(userData);
        return jwtService.isTokenValid(token, userEntity);
    }
}
```

---

## 📊 Logging System

### Structured Logging with MDC

The system implements structured logging with contextual information:

```java
@Service
public class LoggingService {
    
    public void logSecurityEvent(String eventType, String username, Map<String, Object> details) {
        MDC.put("eventType", eventType);
        MDC.put("username", username);
        log.info("Security event: {} for user {}", eventType, username);
        MDC.clear();
    }
    
    public void logAuthenticationSuccess(String username) {
        logSecurityEvent("AUTH_SUCCESS", username, Map.of("action", "login"));
    }
    
    public void logAuthenticationFailure(String username, String reason) {
        logSecurityEvent("AUTH_FAILURE", username, Map.of("reason", reason));
    }
}
```

### Log Output Format

```
2025-11-29 10:30:45.123 INFO  [abc123] [user@email.com] - Security event: AUTH_SUCCESS for user john_doe
2025-11-29 10:30:45.456 WARN  [abc123] [] - Security event: AUTH_FAILURE - bad_credentials
2025-11-29 10:30:46.789 INFO  [def456] [admin] - User registered successfully: newuser@email.com
```

### MDC Context Fields

| Field | Description |
|-------|-------------|
| `traceId` | Unique request identifier for tracing |
| `userId` | Authenticated user email/username |
| `requestPath` | HTTP request path |
| `eventType` | Security event type (AUTH_SUCCESS, etc.) |

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
│ capacity        │       │ status          │       │ active          │
│ createdAt       │       │ capacity        │       │ createdAt       │
│ updatedAt       │       │ price           │       │ updatedAt       │
│                 │       │ createdAt       │       └─────────────────┘
│ eventos ────────┼──────►│ updatedAt       │               ▲
│ (OneToMany)     │       │ venue ◄─────────┤               │
└─────────────────┘       │ (ManyToOne)     │               │
                          │                 │               │
                          │ categories ─────┼───────────────┘
                          │ (ManyToMany)    │
                          └─────────────────┘

┌─────────────────┐       ┌─────────────────┐
│      User       │       │      Role       │
├─────────────────┤       ├─────────────────┤
│ id              │       │ id              │
│ username        │       │ name            │
│ email           │       │ description     │
│ password        │       │                 │
│ firstName       │       │ users ◄─────────┤
│ lastName        │       │ (ManyToMany)    │
│ enabled         │       └─────────────────┘
│ accountNonLocked│               ▲
│ createdAt       │               │
│ lastLogin       │               │
│                 │               │
│ roles ──────────┼───────────────┘
│ (ManyToMany)    │
└─────────────────┘
```

### User-Role Relationship

```java
// UserEntity.java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<RoleEntity> roles = new HashSet<>();

// RoleEntity.java
public enum RoleName {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_MODERATOR
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

### JPA Specifications for Dynamic Filtering

```java
Specification<EventoJpaEntity> spec = EventoSpecification.hasStatus(ACTIVE)
    .and(EventoSpecification.inDateRange(start, end))
    .and(EventoSpecification.byVenueId(1L))
    .and(EventoSpecification.byCategoryName("Music"));

repository.findAll(spec, pageable);
```

---

## 📊 Database Migrations

### Flyway Configuration

Migrations are organized by database profile:

```
src/main/resources/db/migration/
├── h2/                              # H2 (Development)
│   ├── V1__init_schema.sql
│   ├── V2__add_constraints_and_indexes.sql
│   ├── V3__seed_data_and_adjustments.sql
│   └── V4__create_users_roles_tables.sql  # Auth tables
└── mysql/                           # MySQL (Production)
    ├── V1__init_schema.sql
    ├── V2__add_constraints_and_indexes.sql
    ├── V3__seed_data_and_adjustments.sql
    └── V4__create_users_roles_tables.sql  # Auth tables
```

### V4 - Users and Roles Tables

```sql
-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    enabled BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Roles table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- User-Roles junction table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Default roles
INSERT INTO roles (name, description) VALUES 
    ('ROLE_USER', 'Standard user with basic permissions'),
    ('ROLE_ADMIN', 'Administrator with full access'),
    ('ROLE_MODERATOR', 'Content moderator');
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

2. **Configure JWT Secret** (in `application.properties`)
```properties
jwt.secret=your-secure-256-bit-secret-key-minimum-32-characters
```

3. **Compile the project**
```bash
./mvnw clean compile
```

4. **Run with development profile (H2)**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

5. **Run with MySQL profile (production)**
```bash
# Start MySQL with Docker
docker run --name tiquetera-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=tiqueteradb \
  -p 3306:3306 -d mysql:8.0

# Run application
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

6. **Verify it's running**
- Application: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console (dev only): http://localhost:8080/h2-console

---

## 📡 API Endpoints

### Authentication (`/auth`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | Authenticate user | No |
| POST | `/auth/refresh` | Refresh access token | No |

### Events (`/api/events`)

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/events` | List all events | Public |
| GET | `/api/events/{id}` | Get event by ID | Public |
| GET | `/api/events/venue/{venueId}` | List events by venue | Public |
| POST | `/api/events` | Create new event | USER, MODERATOR, ADMIN |
| PUT | `/api/events/{id}` | Update event | USER, MODERATOR, ADMIN |
| DELETE | `/api/events/{id}` | Delete event | ADMIN |

### Venues (`/api/venues`)

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/venues` | List all venues | Public |
| GET | `/api/venues/{id}` | Get venue by ID | Public |
| GET | `/api/venues/city/{city}` | List venues by city | Public |
| POST | `/api/venues` | Create new venue | MODERATOR, ADMIN |
| PUT | `/api/venues/{id}` | Update venue | MODERATOR, ADMIN |
| DELETE | `/api/venues/{id}` | Delete venue | ADMIN |

---

## 📚 Swagger Documentation

Access interactive documentation at: **http://localhost:8080/swagger-ui.html**

### JWT Authentication in Swagger

1. Register or login to get a JWT token
2. Click **"Authorize"** button (🔓)
3. Enter: `Bearer <your-token>`
4. Click **"Authorize"** to save
5. All subsequent requests will include the token

### Documentation Features
- ✅ Request/response examples
- ✅ JWT bearer authentication
- ✅ Detailed schemas with validation rules
- ✅ HTTP response codes
- ✅ Live testing ("Try it out")

---

## 🎯 Design Principles

### Hexagonal Architecture ✅

```
┌──────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐   │
│  │ Controllers │  │  Adapters   │  │ Security Filters    │   │
│  └──────┬──────┘  └──────┬──────┘  └──────────┬──────────┘   │
│         │                │                     │              │
│  ┌──────┴────────────────┴─────────────────────┴──────────┐  │
│  │                   APPLICATION LAYER                     │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │                   USE CASES                       │  │  │
│  │  │  RegistrarUsuario, AutenticarUsuario, CrearEvento │  │  │
│  │  └──────────────────────┬───────────────────────────┘  │  │
│  │                         │                               │  │
│  │  ┌──────────────────────┴───────────────────────────┐  │  │
│  │  │                  DOMAIN LAYER                     │  │  │
│  │  │  ┌─────────────────┐  ┌───────────────────────┐  │  │  │
│  │  │  │  Domain Models  │  │   Ports (Interfaces)  │  │  │  │
│  │  │  │  AuthResult     │  │   UserRepositoryPort  │  │  │  │
│  │  │  │  Evento, Venue  │  │   JwtPort, etc.       │  │  │  │
│  │  │  └─────────────────┘  └───────────────────────┘  │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  └─────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

### SOLID Principles ✅

| Principle | Implementation |
|-----------|----------------|
| **S**RP | Each Use Case has one responsibility (RegistrarUsuario only registers) |
| **O**CP | New authentication methods can be added without modifying existing code |
| **L**SP | Adapters are interchangeable (JwtAdapter, UserRepositoryAdapter) |
| **I**SP | Segregated ports (JwtPort, PasswordEncoderPort, UserRepositoryPort) |
| **D**IP | Use Cases depend on ports (interfaces), not implementations |

---

## 💡 Usage Examples

### 1. Register a User

**Request:**
```bash
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "userId": 1,
  "username": "john_1234",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### 2. Login

**Request:**
```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "userId": 1,
  "username": "john_1234",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### 3. Create Event (Protected)

**Request:**
```bash
POST http://localhost:8080/api/events
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
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

### 4. Access Denied (No Token)

**Request:**
```bash
POST http://localhost:8080/api/events
Content-Type: application/json

{
  "name": "Concert",
  ...
}
```

**Response (401 Unauthorized):**
```json
{
  "type": "about:blank",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Full authentication is required to access this resource",
  "instance": "/api/events"
}
```

### 5. Access Denied (Insufficient Role)

**Request (as ROLE_USER trying to delete):**
```bash
DELETE http://localhost:8080/api/events/1
Authorization: Bearer <user-token>
```

**Response (403 Forbidden):**
```json
{
  "type": "about:blank",
  "title": "Forbidden",
  "status": 403,
  "detail": "Access Denied",
  "instance": "/api/events/1"
}
```

---

## 🧪 Testing with cURL

```bash
# 1. Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@email.com","password":"Test123!"}'

# 2. Login and save token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"test@email.com","password":"Test123!"}' | jq -r '.accessToken')

# 3. Create event with token
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"Concert","description":"Show","eventDate":"2025-12-15T20:00:00","categoria":"Music","venueId":1,"capacity":500,"price":50000}'

# 4. List events (public)
curl http://localhost:8080/api/events
```

---

## 📊 Benefits of this Architecture

| Aspect | Benefit |
|--------|---------|
| **Maintainability** | Clear separation of concerns, easy to modify individual layers |
| **Testability** | Use Cases can be tested with mock ports |
| **Security** | Centralized authentication, role-based access control |
| **Flexibility** | Easy to swap JWT provider, database, or add new auth methods |
| **Scalability** | Stateless JWT allows horizontal scaling |
| **Traceability** | Structured logging with MDC context |

---

## 👥 Author

**Tiquetera Team**
- Repository: [AlvaroN-dev/events-academic](https://github.com/AlvaroN-dev/events-academic)
- Branch: `feat/USM5`

---

## 📄 License

This project is under the Apache 2.0 license - see the [LICENSE](LICENSE) file for details.

---

## 🔗 Useful Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://spring.io/projects/spring-security)
- [JWT Introduction](https://jwt.io/introduction)
- [Hexagonal Architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Flyway Migrations](https://flywaydb.org/)
- [OpenAPI Specification](https://swagger.io/specification/)

---

**Questions or suggestions?** Open an issue on GitHub or contact the development team.
