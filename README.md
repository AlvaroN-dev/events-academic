# TiqueteraCatalogo - Sistema de Gestión de Eventos

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen)](https://spring.io/projects/spring-boot)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue)](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))
[![SOLID](https://img.shields.io/badge/Principles-SOLID-purple)](https://en.wikipedia.org/wiki/SOLID)

Sistema de gestión de eventos y venues implementado con **Arquitectura Hexagonal** y **principios SOLID**, utilizando Spring Boot 3, JPA/Hibernate y H2 Database.

---

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Instalación y Ejecución](#-instalación-y-ejecución)
- [API Endpoints](#-api-endpoints)
- [Documentación Swagger](#-documentación-swagger)
- [Principios de Diseño](#-principios-de-diseño)
- [Ejemplos de Uso](#-ejemplos-de-uso)

---

## ✨ Características

- ✅ **CRUD completo** para Eventos y Venues
- ✅ **Arquitectura Hexagonal** (Ports & Adapters)
- ✅ **Principios SOLID** aplicados en todo el código
- ✅ **Base de datos H2** en memoria
- ✅ **Documentación OpenAPI/Swagger** completa
- ✅ **Validaciones** con Bean Validation
- ✅ **Manejo de errores** centralizado y seguro
- ✅ **DTOs** para request/response
- ✅ **Mappers** para conversión entre capas
- ✅ **Services** para orquestación de casos de uso

---

## 🏗️ Arquitectura

Este proyecto implementa **Arquitectura Hexagonal** (también conocida como Ports and Adapters), que separa la lógica de negocio de los detalles de implementación.

### Capas Principales

```
src/main/java/com/codeup/riwi/tiqueteracatalogo/
│
├── 📦 dominio/                    # CAPA DE DOMINIO
│   ├── models/                    # Modelos de dominio puros (sin frameworks)
│   │   ├── Evento.java
│   │   └── Venue.java
│   ├── ports/                     # Interfaces (contratos)
│   │   ├── in/                    # Puertos de entrada (futuros)
│   │   └── out/                   # Puertos de salida
│   │       ├── EventoRepositoryPort.java
│   │       └── VenueRepositoryPort.java
│   └── excepcion/                 # Excepciones de dominio
│       └── RecursoNoEncontradoException.java
│
├── 📦 aplicacion/                 # CAPA DE APLICACIÓN
│   ├── usecases/                  # Casos de uso (lógica de negocio)
│   │   ├── evento/
│   │   │   ├── CrearEventoUseCase.java
│   │   │   ├── ObtenerEventoUseCase.java
│   │   │   ├── ListarEventosUseCase.java
│   │   │   ├── ActualizarEventoUseCase.java
│   │   │   └── EliminarEventoUseCase.java
│   │   └── venue/
│   │       └── ... (mismos casos de uso)
│   ├── services/                  # Services (orquestación)
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
└── 📦 infraestructura/            # CAPA DE INFRAESTRUCTURA
    ├── controllers/               # Controladores REST
    │   ├── EventController.java
    │   ├── VenueController.java
    │   └── advice/                # Exception handlers
    │       ├── GlobalExceptionHandler.java
    │       └── ErrorResponse.java
    ├── adapters/                  # Adaptadores (implementan puertos)
    │   ├── EventoRepositoryAdapter.java
    │   └── VenueRepositoryAdapter.java
    ├── repositories/              # Repositorios JPA
    │   ├── EventoJpaRepository.java
    │   └── VenueJpaRepository.java
    ├── entities/                  # Entidades JPA
    │   ├── EventoJpaEntity.java
    │   └── VenueJpaEntity.java
    └── config/                    # Configuración
        ├── OpenApiConfig.java
        └── UseCaseConfiguration.java
```

### Flujo de Datos

```
HTTP Request
     ↓
[Controller] ← Adaptador de Entrada
     ↓
[Use Case] ← Lógica de Negocio (usa Puertos)
     ↓
[Repository Port] ← Interface (Puerto de Salida)
     ↓
[Repository Adapter] ← Implementación del Puerto
     ↓
[JPA Repository] ← Persistencia
     ↓
[H2 Database]
```

---

## 🛠️ Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 17 | Lenguaje de programación |
| Spring Boot | 3.5.7 | Framework principal |
| Spring Data JPA | 3.5.7 | Persistencia de datos |
| H2 Database | 2.3.232 | Base de datos en memoria |
| Hibernate | 6.6.33 | ORM |
| Springdoc OpenAPI | 2.7.0 | Documentación Swagger |
| Lombok | 1.18.36 | Reducción de boilerplate |
| Bean Validation | 3.0 | Validaciones |
| Maven | 3.9+ | Gestión de dependencias |

---

## 📁 Estructura del Proyecto

### Dominio (Núcleo del Negocio)

**Características:**
- ✅ Sin dependencias de frameworks
- ✅ Modelos puros (POJOs)
- ✅ Define interfaces (puertos)
- ✅ Contiene excepciones de negocio

**Ejemplo:**
```java
// Modelo de dominio puro
public class Evento {
    private Long id;
    private String name;
    private LocalDateTime eventDate;
    // ... sin anotaciones de JPA
}

// Puerto (interface)
public interface EventoRepositoryPort {
    Evento save(Evento evento);
    Optional<Evento> findById(Long id);
    // ...
}
```

### Aplicación (Casos de Uso)

**Características:**
- ✅ Contiene la lógica de negocio
- ✅ Depende solo de puertos (interfaces)
- ✅ Independiente de frameworks
- ✅ Services orquestan use cases

**Ejemplo:**
```java
public class CrearEventoUseCase {
    private final EventoRepositoryPort eventoRepository;
    private final VenueRepositoryPort venueRepository;
    
    public Evento ejecutar(Evento evento) {
        // Validación de negocio
        if (!venueRepository.existsById(evento.getVenueId())) {
            throw new IllegalArgumentException("El venue no existe");
        }
        return eventoRepository.save(evento);
    }
}
```

### Infraestructura (Detalles de Implementación)

**Características:**
- ✅ Implementa los puertos
- ✅ Contiene detalles técnicos (JPA, REST, etc.)
- ✅ Adaptadores intercambiables
- ✅ Controllers, Repositories, Entities

**Ejemplo:**
```java
@Component
public class EventoRepositoryAdapter implements EventoRepositoryPort {
    private final EventoJpaRepository jpaRepository;
    
    @Override
    public Evento save(Evento evento) {
        EventoJpaEntity entity = toEntity(evento);
        EventoJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }
}
```

---

## 🚀 Instalación y Ejecución

### Requisitos Previos

- Java 17 o superior
- Maven 3.9+ (incluido en el proyecto como `mvnw`)

### Pasos

1. **Clonar el repositorio**
```bash
git clone https://github.com/tu-usuario/TiqueteraCatalogo.git
cd TiqueteraCatalogo
```

2. **Compilar el proyecto**
```bash
./mvnw clean compile
```

3. **Ejecutar la aplicación**
```bash
./mvnw spring-boot:run
```

4. **Verificar que está corriendo**
- Aplicación: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

---

## 📡 API Endpoints

### Eventos (`/api/events`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/events` | Listar todos los eventos |
| GET | `/api/events/{id}` | Obtener evento por ID |
| GET | `/api/events/venue/{venueId}` | Listar eventos por venue |
| POST | `/api/events` | Crear nuevo evento |
| PUT | `/api/events/{id}` | Actualizar evento |
| DELETE | `/api/events/{id}` | Eliminar evento |

### Venues (`/api/venues`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/venues` | Listar todos los venues |
| GET | `/api/venues/{id}` | Obtener venue por ID |
| POST | `/api/venues` | Crear nuevo venue |
| PUT | `/api/venues/{id}` | Actualizar venue |
| DELETE | `/api/venues/{id}` | Eliminar venue |

---

## 📚 Documentación Swagger

Accede a la documentación interactiva en: **http://localhost:8080/swagger-ui.html**

Características de la documentación:
- ✅ Ejemplos de request/response
- ✅ Schemas detallados
- ✅ Códigos de respuesta HTTP
- ✅ Validaciones documentadas
- ✅ Pruebas en vivo ("Try it out")

---

## 🎯 Principios de Diseño

### Arquitectura Hexagonal ✅

1. **Dominio en el centro**: La lógica de negocio no depende de frameworks
2. **Puertos**: Interfaces que definen contratos
3. **Adaptadores**: Implementaciones intercambiables
4. **Inversión de dependencias**: Infraestructura depende del dominio

### Principios SOLID ✅

#### 1. Single Responsibility Principle (SRP)
Cada clase tiene una única responsabilidad:
- `CrearEventoUseCase`: Solo crear eventos
- `EventoRepositoryAdapter`: Solo adaptar persistencia
- `EventController`: Solo manejar HTTP

#### 2. Open/Closed Principle (OCP)
Abierto para extensión, cerrado para modificación:
```java
// Podemos agregar nuevos adaptadores sin modificar casos de uso
public class EventoMongoAdapter implements EventoRepositoryPort { }
public class EventoRedisAdapter implements EventoRepositoryPort { }
```

#### 3. Liskov Substitution Principle (LSP)
Los adaptadores son intercambiables:
```java
EventoRepositoryPort repo = new EventoRepositoryAdapter();  // JPA
EventoRepositoryPort repo = new EventoMongoAdapter();       // MongoDB
// El caso de uso funciona con cualquiera
```

#### 4. Interface Segregation Principle (ISP)
Interfaces específicas y cohesivas:
```java
public interface EventoRepositoryPort { /* solo métodos de eventos */ }
public interface VenueRepositoryPort { /* solo métodos de venues */ }
```

#### 5. Dependency Inversion Principle (DIP)
Dependencias en abstracciones:
```java
public class CrearEventoUseCase {
    private final EventoRepositoryPort repository;  // ✅ Interface
    // NO: private final EventoRepositoryAdapter repository;  // ❌ Implementación
}
```

---

## 💡 Ejemplos de Uso

### 1. Crear un Venue

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

### 2. Crear un Evento

**Request:**
```bash
POST http://localhost:8080/api/events
Content-Type: application/json

{
  "name": "Concierto Rock",
  "description": "Gran concierto de rock",
  "eventDate": "2025-12-15T20:00:00",
  "categoria": "Música",
  "venueId": 1,
  "capacity": 1000,
  "price": 80000.0
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Concierto Rock",
  "description": "Gran concierto de rock",
  "eventDate": "2025-12-15T20:00:00",
  "categoria": "Música",
  "venueId": 1,
  "capacity": 1000,
  "price": 80000.0
}
```

### 3. Listar Eventos por Venue

**Request:**
```bash
GET http://localhost:8080/api/events/venue/1
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Concierto Rock",
    "eventDate": "2025-12-15T20:00:00",
    "venueId": 1,
    ...
  }
]
```

### 4. Manejo de Errores

**Error 404 - Recurso no encontrado:**
```json
{
  "timestamp": "2025-11-25T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Evento con ID 999 no encontrado",
  "path": "/api/events/999"
}
```

**Error 400 - Validación:**
```json
{
  "timestamp": "2025-11-25T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/events",
  "details": {
    "name": "El nombre del evento es obligatorio",
    "eventDate": "La fecha del evento es obligatoria"
  }
}
```

**Error 500 - Error interno (mensaje genérico por seguridad):**
```json
{
  "timestamp": "2025-11-25T10:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Ha ocurrido un error interno. Por favor contacte al administrador.",
  "path": "/api/events"
}
```

---

## 🔒 Seguridad

- ✅ **Mensajes de error genéricos**: No se exponen detalles SQL ni stack traces
- ✅ **Validaciones**: Bean Validation en todos los DTOs
- ✅ **Exception handling centralizado**: `GlobalExceptionHandler`

---

## 📝 Configuración

### Base de Datos H2

La aplicación usa H2 en memoria. Configuración en `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:tiqueteradb
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Acceder a H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:tiqueteradb`
- User: `sa`
- Password: (vacío)

---

## 🧪 Testing

### Probar con cURL

```bash
# Crear venue
curl -X POST http://localhost:8080/api/venues \
  -H "Content-Type: application/json" \
  -d '{"name":"Teatro","address":"Calle 1","city":"Bogotá","country":"Colombia","capacity":500}'

# Crear evento
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"name":"Concierto","description":"Show","eventDate":"2025-12-15T20:00:00","categoria":"Música","venueId":1,"capacity":500,"price":50000}'

# Listar eventos
curl http://localhost:8080/api/events
```

### Probar con Swagger UI

1. Ir a http://localhost:8080/swagger-ui.html
2. Seleccionar un endpoint
3. Click en "Try it out"
4. Completar el JSON de ejemplo
5. Click en "Execute"

---

## 📊 Beneficios de esta Arquitectura

### Mantenibilidad
- Código organizado y fácil de entender
- Responsabilidades claras
- Cambios localizados

### Testabilidad
- Fácil crear tests unitarios con mocks
- Casos de uso independientes
- Puertos permiten inyectar implementaciones fake

### Flexibilidad
- Fácil cambiar de JPA a MongoDB
- Fácil agregar nuevos adaptadores
- Lógica de negocio protegida

### Escalabilidad
- Componentes desacoplados
- Fácil agregar nuevas funcionalidades
- Arquitectura preparada para microservicios

---

## 👥 Autor

**Equipo Tiquetera**
- Email: soporte@tiquetera.com

---

## 📄 Licencia

Este proyecto está bajo la licencia Apache 2.0 - ver el archivo [LICENSE](LICENSE) para más detalles.

---

## 🔗 Enlaces Útiles

- [Documentación Spring Boot](https://spring.io/projects/spring-boot)
- [Arquitectura Hexagonal](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))
- [Principios SOLID](https://en.wikipedia.org/wiki/SOLID)
- [OpenAPI Specification](https://swagger.io/specification/)

---

**¿Preguntas o sugerencias?** Abre un issue en GitHub o contacta al equipo de desarrollo.
