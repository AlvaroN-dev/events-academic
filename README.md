<<<<<<< HEAD
<<<<<<< HEAD
# events-academic
=======
# TiqueteraCatalogo
=======
# TiqueteraCatalogo - Event Management API
>>>>>>> 1194b46 (Update documentatio of the Realmed.md)
=======
# events-academic

# TiqueteraCatalogo
>>>>>>> 2c5d84c (Update README.md)
>>>>>>> 3a50946 (Update README.md)

A comprehensive RESTful API for managing events and venues, built with Spring Boot 3 and JPA persistence.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database](#database)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Validation](#validation)
- [Error Handling](#error-handling)
- [Examples](#examples)

---

## 🎯 Overview

TiqueteraCatalogo is a Spring Boot application that provides a complete CRUD API for managing events and venues. It features JPA persistence with H2 database, comprehensive validation, pagination with dynamic filters, and global error handling.

## ✨ Features

- ✅ **Full CRUD Operations** for Events and Venues
- ✅ **JPA Persistence** with H2 database (file-based for data persistence)
- ✅ **Comprehensive Validations** with Bean Validation (@Valid, @NotBlank, @Size, @Future, etc.)
- ✅ **Pagination & Filtering** with Spring Data Pageable and JPA Specifications
- ✅ **Global Error Handling** with @ControllerAdvice (400, 404, 409 errors)
- ✅ **OpenAPI/Swagger Documentation** with detailed examples
- ✅ **Entity Relationships** (ManyToOne between Event and Venue)
- ✅ **Unique Constraints** (Event names must be unique)
- ✅ **Audit Timestamps** (createdAt, updatedAt)

## 🛠 Tech Stack

- **Java**: 17
- **Spring Boot**: 3.5.7
- **Spring Data JPA**: For database operations
- **H2 Database**: File-based persistence
- **Bean Validation**: For input validation
- **Lombok**: To reduce boilerplate code
- **Swagger/OpenAPI**: API documentation
- **Maven**: Build tool

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven (included via wrapper)

### Running the Application

```powershell
# Clone the repository
cd c:\Users\anonimo\Videos\TiqueteraCatalogo

# Compile
./mvnw.cmd clean compile

# Run
./mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8080`

### Access Points

- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/tiqueteradb`
  - Username: `sa`
  - Password: (empty)

## 📚 API Documentation

Interactive API documentation is available via Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON specification:
```
http://localhost:8080/v3/api-docs
```

## 💾 Database

### Configuration

The application uses H2 database in file mode for persistence between restarts.

**Configuration** (`application.properties`):
```properties
spring.datasource.url=jdbc:h2:file:./data/tiqueteradb
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

### Schema

#### Venue Table
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(200) | NOT NULL |
| address | VARCHAR(300) | NOT NULL |
| city | VARCHAR(100) | NOT NULL |
| country | VARCHAR(100) | NOT NULL |
| capacity | INTEGER | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

#### Event Table
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(200) | NOT NULL, **UNIQUE** |
| description | VARCHAR(1000) | |
| event_date | TIMESTAMP | NOT NULL |
| categoria | VARCHAR(100) | NOT NULL |
| venue_id | BIGINT | NOT NULL, FK → venue(id) |
| capacity | INTEGER | NOT NULL |
| price | DOUBLE | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

## 📁 Project Structure

```
src/main/java/com/codeup/riwi/tiqueteracatalogo/
├── domain/
│   ├── entity/              # JPA Entities
│   │   ├── EventoEntity.java
│   │   └── VenueEntity.java
│   └── mapper/              # Entity-DTO Mappers
│       ├── EventoMapper.java
│       └── VenueMapper.java
├── repository/              # JPA Repositories
│   ├── EventoRepository.java
│   ├── VenueRepository.java
│   └── specification/       # JPA Specifications for dynamic filters
│       └── EventoSpecification.java
├── services/                # Business Logic
│   ├── IEventoService.java
│   ├── IVenueService.java
│   └── impl/
│       ├── EventoServiceImpl.java
│       └── VenueServiceImpl.java
├── web/
│   ├── controller/          # REST Controllers
│   │   ├── EventController.java
│   │   └── VenueController.java
│   ├── dto/                 # Data Transfer Objects
│   │   ├── EventoRequest.java
│   │   ├── EventoResponse.java
│   │   ├── VenueRequest.java
│   │   └── VenueResponse.java
│   └── advice/              # Global Exception Handling
│       ├── GlobalExceptionHandler.java
│       ├── ErrorResponse.java
│       └── ResourceNotFoundException.java
└── config/                  # Configuration
    └── OpenApiConfig.java
```

## 🔌 API Endpoints

### Events

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/events` | Get all events |
| GET | `/api/events/{id}` | Get event by ID |
| GET | `/api/events/venue/{venueId}` | Get events by venue |
| GET | `/api/events/paginated` | Get paginated events with filters |
| POST | `/api/events` | Create new event |
| PUT | `/api/events/{id}` | Update event |
| DELETE | `/api/events/{id}` | Delete event |

### Venues

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/venues` | Get all venues |
| GET | `/api/venues/{id}` | Get venue by ID |
| POST | `/api/venues` | Create new venue |
| PUT | `/api/venues/{id}` | Update venue |
| DELETE | `/api/venues/{id}` | Delete venue |

### Pagination & Filters

**Endpoint**: `GET /api/events/paginated`

**Query Parameters**:
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| ciudad | String | No | - | Filter by venue city |
| categoria | String | No | - | Filter by event category |
| fechaInicio | LocalDateTime | No | - | Filter events after this date |
| page | int | No | 0 | Page number (0-indexed) |
| size | int | No | 10 | Page size |
| sort | String | No | eventDate | Sort field |
| direction | String | No | desc | Sort direction (asc/desc) |

**Example**:
```bash
GET /api/events/paginated?ciudad=Bogotá&categoria=Concierto&page=0&size=10&sort=price&direction=asc
```

## ✅ Validation

### Event Validation Rules

| Field | Validations | Error Message |
|-------|-------------|---------------|
| name | @NotBlank, @Size(3-200) | "Event name is required" / "Name must be between 3 and 200 characters" |
| description | @Size(max=1000) | "Description cannot exceed 1000 characters" |
| eventDate | @NotNull, @Future | "Event date is required" / "Event date must be in the future" |
| categoria | @NotBlank, @Size(3-100) | "Category is required" |
| venueId | @NotNull, @Positive | "Venue ID is required" |
| capacity | @NotNull, @Positive, @Max(1000000) | "Capacity must be greater than 0" |
| price | @NotNull, @Positive, @DecimalMax | "Price must be greater than 0" |

### Venue Validation Rules

| Field | Validations | Error Message |
|-------|-------------|---------------|
| name | @NotBlank, @Size(3-200) | "Venue name is required" |
| address | @NotBlank, @Size(5-300) | "Address is required" |
| city | @NotBlank, @Size(2-100) | "City is required" |
| country | @NotBlank, @Size(2-100) | "Country is required" |
| capacity | @NotNull, @Positive, @Max(1000000) | "Capacity must be greater than 0" |

### Business Rules

- ✅ Event names must be unique
- ✅ Venue must exist before creating an event
- ✅ Event date must be in the future

## ⚠️ Error Handling

The API uses a global exception handler (`@RestControllerAdvice`) that returns consistent error responses.

### Error Response Format

```json
{
  "timestamp": "2025-11-24T18:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error in submitted data",
  "path": "/api/events",
  "details": [
    "name: Name must be between 3 and 200 characters",
    "eventDate: Event date must be in the future"
  ]
}
```

### HTTP Status Codes

| Code | Description | When |
|------|-------------|------|
| 200 | OK | Successful GET, PUT |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation errors, invalid data |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Unique constraint violation, FK violation |
| 500 | Internal Server Error | Unexpected errors |

## 📝 Examples

<<<<<<< HEAD
<<<<<<< HEAD
## Notas y mejoras futuras
- Persistencia real con JPA/Hibernate y base de datos.
- Paginación, ordenamiento y filtros.
- Tests unitarios e integración.
- Seguridad (Spring Security, JWT).
- Validaciones adicionales (rango de fechas, precio positivo con BigDecimal).
=======
=======
>>>>>>> 3a50946 (Update README.md)
### Create a Venue

```bash
curl -X POST http://localhost:8080/api/venues \
  -H "Content-Type: application/json" \
  -d '{
    "name": "National Theater",
    "address": "71st Street #10-25",
    "city": "Bogotá",
    "country": "Colombia",
    "capacity": 1500
  }'
```

**Response (201 Created)**:
```json
{
  "id": 1,
  "name": "National Theater",
  "address": "71st Street #10-25",
  "city": "Bogotá",
  "country": "Colombia",
  "capacity": 1500
}
```

### Create an Event

```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rock Concert 2025",
    "description": "Amazing rock show",
    "eventDate": "2025-12-15T20:00:00",
    "venueId": 1,
    "capacity": 1000,
    "price": 80000,
    "categoria": "Concert"
  }'
```

**Response (201 Created)**:
```json
{
  "id": 1,
  "name": "Rock Concert 2025",
  "description": "Amazing rock show",
  "eventDate": "2025-12-15T20:00:00",
  "venueId": 1,
  "capacity": 1000,
  "price": 80000.0,
  "categoria": "Concert"
}
```

### Get Paginated Events with Filters

```bash
curl "http://localhost:8080/api/events/paginated?ciudad=Bogotá&categoria=Concert&page=0&size=10&sort=eventDate&direction=desc"
```

**Response (200 OK)**:
```json
{
  "content": [
    {
      "id": 1,
      "name": "Rock Concert 2025",
      "description": "Amazing rock show",
      "eventDate": "2025-12-15T20:00:00",
      "venueId": 1,
      "capacity": 1000,
      "price": 80000.0,
      "categoria": "Concert"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Update an Event

```bash
curl -X PUT http://localhost:8080/api/events/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rock Concert 2025 - Updated",
    "description": "Updated description",
    "eventDate": "2025-12-15T21:00:00",
    "venueId": 1,
    "capacity": 1200,
    "price": 90000,
    "categoria": "Concert"
  }'
```

### Delete an Event

```bash
curl -X DELETE http://localhost:8080/api/events/1
```

**Response**: 204 No Content

### Error Example - Validation

```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "name": "AB",
    "eventDate": "2020-01-01T20:00:00",
    "venueId": 1,
    "capacity": -10,
    "price": 0
  }'
```

**Response (400 Bad Request)**:
```json
{
  "timestamp": "2025-11-24T18:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error in submitted data",
  "path": "/api/events",
  "details": [
    "name: Name must be between 3 and 200 characters",
    "eventDate: Event date must be in the future",
    "capacity: Capacity must be greater than 0",
    "price: Price must be greater than 0",
    "categoria: Category is required"
  ]
}
```

### Error Example - Duplicate Name

```bash
# Trying to create an event with an existing name
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rock Concert 2025",
    "eventDate": "2025-12-20T20:00:00",
    "venueId": 1,
    "capacity": 800,
    "price": 75000,
    "categoria": "Concert"
  }'
```

**Response (400 Bad Request)**:
```json
{
  "timestamp": "2025-11-24T18:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "An event with the name 'Rock Concert 2025' already exists",
  "path": "/api/events"
}
```

## 🧪 Testing with Swagger UI

1. Navigate to http://localhost:8080/swagger-ui.html
2. Expand an endpoint (e.g., POST /api/venues)
3. Click "Try it out"
4. Fill in the JSON example
5. Click "Execute"
6. View the response with HTTP status code

Swagger validates data before sending and displays clear errors.

## 📄 License

This project is licensed under the Apache License 2.0.


<<<<<<< HEAD
For support or questions, contact: soporte@tiquetera.com

---

**Built with ❤️ using Spring Boot**
>>>>>>> 1194b46 (Update documentatio of the Realmed.md)
=======
---
>>>>>>> 3a50946 (Update README.md)
