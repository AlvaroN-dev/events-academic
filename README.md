# TiqueteraCatalogo

Catálogo de Eventos y Venues (Spring Boot 3) con almacenamiento en memoria, documentación OpenAPI/Swagger y arquitectura en capas Controller → Service → Repository.

## Índice
- [Resumen](#resumen)
- [Arquitectura](#arquitectura)
- [Cómo ejecutar](#cómo-ejecutar)
- [Swagger / OpenAPI](#swagger--openapi)
- [Endpoints](#endpoints)
- [Manejo de errores](#manejo-de-errores)
- [Clases y código explicado línea por línea](#clases-y-código-explicado-línea-por-línea)
  - [TiqueteraCatalogoApplication](#tiqueteracatalogoapplication)
  - [OpenApiConfig](#openapiconfig)
  - [DTOs](#dtos)
    - [EventDTO](#eventdto)
    - [VenueDTO](#venuedto)
  - [Repositorios (simulados en memoria)](#repositorios-simulados-en-memoria)
    - [EventRepository](#eventrepository)
    - [VenueRepository](#venuerepository)
  - [Servicios](#servicios)
    - [EventService](#eventservice)
    - [VenueService](#venueservice)
  - [Controladores](#controladores)
    - [EventController](#eventcontroller)
    - [VenueController](#venuecontroller)
  - [Excepciones y errores](#excepciones-y-errores)
    - [ErrorResponse](#errorresponse)
    - [GlobalExceptionHandler](#globalexceptionhandler)
    - [ResourceNotFoundException](#resourcenotfoundexception)
- [Pruebas rápidas con cURL](#pruebas-rápidas-con-curl)
- [Notas y mejoras futuras](#notas-y-mejoras-futuras)

## Resumen
Proyecto Java 17 con Spring Boot 3.5.7. Implementa:
- CRUD completo para Eventos y Venues.
- Almacenamiento en memoria durante la ejecución (sin base de datos).
- Documentación OpenAPI/Swagger con ejemplos y schemas.
- Manejo global de errores (400/404/500) y validaciones (Bean Validation).
- Arquitectura en capas (Controller → Service → Repository).

## Arquitectura
```
controller/   → Recibe HTTP, valida, mapea códigos HTTP y documenta Swagger.
service/      → Lógica de negocio, orquesta repositorios.
repository/   → Simula persistencia en ArrayList, genera IDs (AtomicLong).
DTO/          → Modelos de transferencia validados y documentados.
exception/    → Excepciones, modelo de error y manejador global.
config/       → Configuración de OpenAPI/Swagger.
```

## Cómo ejecutar
Requisitos: Java 17. Desde PowerShell en Windows:

```powershell
# Compilar
cd c:\Users\anonimo\Videos\TiqueteraCatalogo\TiqueteraCatalogo
./mvnw.cmd clean compile

# Ejecutar
./mvnw.cmd spring-boot:run
```

- App: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

> Si PowerShell no reconoce `./mvnw.cmd`, usa:
> ```powershell
> & ".\mvnw.cmd" spring-boot:run
> ```

## Swagger / OpenAPI
La clase `config/OpenApiConfig.java` define metadatos (título, versión, contacto, licencia) y servidores. Los controladores usan anotaciones `@Operation`, `@ApiResponses`, `@Parameter` y ejemplos para request/response. Los DTOs tienen `@Schema` por campo.

## Endpoints
Eventos `/api/events` y Venues `/api/venues` con operaciones: GET (all, by id), POST, PUT, DELETE y búsqueda por relación (events by venue).

## Manejo de errores
- 400 Bad Request: validación y tipos incorrectos.
- 404 Not Found: recurso no encontrado.
- 500 Internal Server Error: genérico.

Formato unificado `ErrorResponse` con `timestamp`, `status`, `error`, `message`, `path` y opcional `details`.

---

## Clases y código explicado línea por línea

> Nota: Para brevedad, en imports se explica el bloque completo. El detalle línea a línea se centra en el cuerpo de las clases y métodos.

### TiqueteraCatalogoApplication
Archivo: `src/main/java/.../TiqueteraCatalogoApplication.java`
```java
@SpringBootApplication          // Habilita autoconfiguración, escaneo de componentes y configuración
public class TiqueteraCatalogoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiqueteraCatalogoApplication.class, args); // Arranca la app Spring Boot
    }
}
```

### OpenApiConfig
Archivo: `config/OpenApiConfig.java`
```java
@Configuration                       // Marca como clase de configuración Spring
@Bean                                // Expone un bean OpenAPI para springdoc
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("API Catálogo de Eventos y Venues") // Título mostrado en Swagger UI
            .version("1.0.0")                         // Versión del API
            .description("API REST para gestionar catálogo...") // Descripción general
            .contact(new Contact().name("Equipo Tiquetera").email("soporte@tiquetera.com"))
            .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html")))
        .servers(List.of(                              // Lista de servidores publicados en la doc
            new Server().url("http://localhost:8080").description("Servidor de Desarrollo"),
            new Server().url("http://localhost:8081").description("Servidor de Pruebas")));
}
```

### DTOs
#### EventDTO
Archivo: `DTO/EventDTO.java`
```java
@Schema(description = "Información del evento")                 // Documenta el schema del recurso
public class EventDTO {
    @Schema(..., accessMode = READ_ONLY) private Long id;        // ID autogenerado por repo (solo lectura)

    @NotBlank(message = "El nombre...")                          // Validación: obligatorio
    @Schema(description = "Nombre...", example = "Concierto Rock 2025", required = true)
    private String name;

    @Schema(description = "Descripción...")
    private String description;                                   // Texto opcional descriptivo

    @NotNull(message = "La fecha...")                           // Validación: obligatorio
    @Schema(description = "Fecha y hora...", example = "2025-12-15T20:00:00", required = true)
    private LocalDateTime eventDate;

    @NotNull(message = "El venue ID...")                        // Validación: obligatorio
    @Schema(description = "ID del venue...", example = "1", required = true)
    private Long venueId;

    @Positive(message = "La capacidad...")                      // Validación: > 0
    @Schema(description = "Capacidad máxima...", example = "1000", required = true)
    private Integer capacity;

    @Positive(message = "El precio...")                         // Validación: > 0
    @Schema(description = "Precio de la entrada", example = "80000.00", required = true)
    private Double price;

    // Getters/Setters estándar para serialización JSON y binding
}
```

#### VenueDTO
Archivo: `DTO/VenueDTO.java`
```java
@Schema(description = "Información del venue/lugar")
public class VenueDTO {
    @Schema(..., accessMode = READ_ONLY) private Long id;        // ID autogenerado por repo

    @NotBlank(message = "El nombre...")
    @Schema(description = "Nombre del venue", example = "Teatro Nacional", required = true)
    private String name;

    @NotBlank(message = "La dirección...")
    @Schema(description = "Dirección...", example = "Calle 71 #10-25", required = true)
    private String address;

    @NotBlank(message = "La ciudad...")
    @Schema(description = "Ciudad...", example = "Bogotá", required = true)
    private String city;

    @NotBlank(message = "El país...")
    @Schema(description = "País...", example = "Colombia", required = true)
    private String country;

    @Positive(message = "La capacidad...")
    @Schema(description = "Capacidad máxima...", example = "1500", required = true)
    private Integer capacity;
}
```

### Repositorios (simulados en memoria)
#### EventRepository
Archivo: `repository/EventRepository.java`
```java
@Repository                                        // Detectado como bean de acceso a datos
public class EventRepository {
    private final List<EventDTO> events = new ArrayList<>();     // Almacenamiento en memoria
    private final AtomicLong idGenerator = new AtomicLong(1);    // Secuencia de IDs thread-safe

    public List<EventDTO> findAll() {                            // Lee todo
        return new ArrayList<>(events);                          // Copia defensiva
    }

    public Optional<EventDTO> findById(Long id) {                // Búsqueda por ID
        return events.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public List<EventDTO> findByVenueId(Long venueId) {          // Consulta por relación
        return events.stream().filter(e -> e.getVenueId().equals(venueId)).toList();
    }

    public EventDTO save(EventDTO event) {                       // Inserción
        if (event.getId() == null) event.setId(idGenerator.getAndIncrement());
        events.add(event);
        return event;
    }

    public EventDTO update(EventDTO event) {                      // Actualización in-place
        return findById(event.getId()).map(db -> {
            db.setName(event.getName());
            db.setDescription(event.getDescription());
            db.setEventDate(event.getEventDate());
            db.setVenueId(event.getVenueId());
            db.setCapacity(event.getCapacity());
            db.setPrice(event.getPrice());
            return db;                                           // Devuelve referencia actualizada
        }).orElse(null);                                         // Convenio simple para este ejemplo
    }

    public boolean deleteById(Long id) {                          // Eliminación por ID
        return events.removeIf(e -> e.getId().equals(id));
    }

    public boolean existsById(Long id) {                          // Existencia
        return events.stream().anyMatch(e -> e.getId().equals(id));
    }

    public long count() { return events.size(); }                 // Conteo total
}
```

#### VenueRepository
Archivo: `repository/VenueRepository.java` (mismo patrón que `EventRepository`)

### Servicios
#### EventService
Archivo: `service/EventService.java`
```java
@Service                                         // Bean de servicio (lógica de negocio)
public class EventService {
    private final EventRepository eventRepository;               // Inyección por constructor

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;                  // Asigna dependencia
    }

    public List<EventDTO> getAllEvents() { return eventRepository.findAll(); }
    public Optional<EventDTO> getEventById(Long id) { return eventRepository.findById(id); }
    public EventDTO createEvent(EventDTO dto) { return eventRepository.save(dto); }

    public Optional<EventDTO> updateEvent(Long id, EventDTO dto) {
        if (!eventRepository.existsById(id)) return Optional.empty();
        dto.setId(id);                                           // Garantiza idempotencia por ID
        return Optional.ofNullable(eventRepository.update(dto)); // Puede ser null si no existe
    }

    public boolean deleteEvent(Long id) { return eventRepository.deleteById(id); }
    public List<EventDTO> getEventsByVenueId(Long venueId) { return eventRepository.findByVenueId(venueId); }
}
```

#### VenueService
Archivo: `service/VenueService.java` (idéntico patrón a `EventService`).

### Controladores
#### EventController
Archivo: `controller/EventController.java`
```java
@RestController                              // Marca controlador REST (JSON por defecto)
@RequestMapping("/api/events")               // Prefijo de todos los endpoints
@Tag(name = "Events", description = "API para gestión de eventos") // Agrupa en Swagger
public class EventController {
    private final EventService eventService; // Inyección del servicio

    public EventController(EventService eventService) { this.eventService = eventService; }

    @Operation(summary = "Obtener todos...", description = "Retorna lista completa...")
    @ApiResponses({ @ApiResponse(responseCode = "200", ... ) })
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @Operation(summary = "Obtener evento por ID", description = "...")
    @ApiResponses({
      @ApiResponse(responseCode = "200", ...),
      @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@Parameter(...) @PathVariable Long id) {
        return eventService.getEventById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("Evento", id)); // Lanza 404 si no existe
    }

    @Operation(summary = "Crear nuevo evento", description = "...")
    @ApiResponses({
      @ApiResponse(responseCode = "201", ...),
      @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventDTO eventDTO) {
        EventDTO created = eventService.createEvent(eventDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar evento", description = "...")
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @Valid @RequestBody EventDTO eventDTO) {
        return eventService.updateEvent(id, eventDTO)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }

    @Operation(summary = "Eliminar evento", description = "...")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Evento eliminado") })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (!eventService.deleteEvent(id)) throw new ResourceNotFoundException("Evento", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener eventos por venue", description = "...")
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<EventDTO>> getEventsByVenueId(@PathVariable Long venueId) {
        return ResponseEntity.ok(eventService.getEventsByVenueId(venueId));
    }
}
```

#### VenueController
Archivo: `controller/VenueController.java` (mismo patrón que `EventController`).

### Excepciones y errores
#### ErrorResponse
Archivo: `exception/ErrorResponse.java`
```java
@Schema(description = "Respuesta de error estándar de la API")
public class ErrorResponse {
    private LocalDateTime timestamp = LocalDateTime.now();  // Sello temporal al construir
    private int status;                                     // Código HTTP (400/404/500)
    private String error;                                   // Texto corto: Bad Request/Not Found/...
    private String message;                                 // Mensaje legible del problema
    private String path;                                    // Endpoint que falló
    private List<String> details;                           // Detalles (p.ej. campos inválidos)

    // Constructores + getters/setters estándar
}
```

#### GlobalExceptionHandler
Archivo: `exception/GlobalExceptionHandler.java`
```java
@RestControllerAdvice                             // Atrapador global de excepciones
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(...) {
        // Recorre FieldError y arma lista "campo: mensaje"
        // Devuelve 400 con ErrorResponse + details
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(...) {
        // Param tipo incorrecto en path/query → 400 con mensaje "parámetro X debe ser de tipo Y"
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(...) {
        // Devuelve 404 con mensaje de la excepción
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(...) {
        // Falla inesperada → 500 mensaje genérico
    }
}
```

#### ResourceNotFoundException
Archivo: `exception/ResourceNotFoundException.java`
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s con ID %d no encontrado", resource, id));
    }
}
```

---

## Pruebas rápidas con cURL

### 1. Crear un Venue primero (necesario para eventos)
```bash
curl -X POST http://localhost:8080/api/venues \
  -H "Content-Type: application/json" \
  -d '{"name":"Teatro Nacional","address":"Calle 71 #10-25","city":"Bogotá","country":"Colombia","capacity":1500}'
```
**Respuesta esperada (201 Created):**
```json
{"id":1,"name":"Teatro Nacional","address":"Calle 71 #10-25","city":"Bogotá","country":"Colombia","capacity":1500}
```

### 2. Crear un Evento (usa el venueId=1 del paso anterior)
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"name":"Concierto Rock","description":"Gran show de rock","eventDate":"2025-12-15T20:00:00","venueId":1,"capacity":1000,"price":80000}'
```
**Respuesta esperada (201 Created):**
```json
{"id":1,"name":"Concierto Rock","description":"Gran show de rock","eventDate":"2025-12-15T20:00:00","venueId":1,"capacity":1000,"price":80000.0}
```

### 3. Listar todos los recursos
```bash
# Listar eventos
curl http://localhost:8080/api/events

# Listar venues
curl http://localhost:8080/api/venues
```

### 4. Obtener por ID (ahora que existen)
```bash
curl http://localhost:8080/api/events/1
curl http://localhost:8080/api/venues/1
```

### 5. Actualizar un Venue (PUT con TODOS los campos)
```bash
curl -X PUT http://localhost:8080/api/venues/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Teatro Nacional Renovado","address":"Calle 71 #10-25","city":"Bogotá","country":"Colombia","capacity":2000}'
```
**⚠️ IMPORTANTE:** PUT requiere todos los campos obligatorios (`name`, `address`, `city`, `country`, `capacity`).

### 6. Actualizar un Evento
```bash
curl -X PUT http://localhost:8080/api/events/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Concierto Rock Actualizado","description":"Show renovado","eventDate":"2025-12-15T21:00:00","venueId":1,"capacity":1200,"price":90000}'
```

### 7. Eliminar recursos
```bash
curl -X DELETE http://localhost:8080/api/events/1
curl -X DELETE http://localhost:8080/api/venues/1
```
**Respuesta esperada:** 204 No Content (sin body)

### 8. Buscar eventos por venue
```bash
curl http://localhost:8080/api/events/venue/1
```

## ❌ Errores comunes y cómo evitarlos

### Error 404: "Evento con ID X no encontrado"
**Causa:** Intentas acceder/actualizar/eliminar un recurso que no existe.
```bash
# ❌ MAL - ID 999 no existe
curl http://localhost:8080/api/events/999
```
**Solución:** Verifica que el recurso existe primero con GET /api/events

### Error 400: Validación fallida
**Causa 1:** Faltan campos obligatorios en PUT
```bash
# ❌ MAL - Faltan campos obligatorios
curl -X PUT http://localhost:8080/api/venues/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Solo nombre"}'
```
**Solución:** Envía TODOS los campos requeridos.

**Causa 2:** Valores inválidos
```bash
# ❌ MAL - capacity negativa
curl -X POST http://localhost:8080/api/venues \
  -H "Content-Type: application/json" \
  -d '{"name":"Teatro","address":"Calle 1","city":"Bogotá","country":"Colombia","capacity":-100}'
```
**Solución:** Usa valores positivos para `capacity` y `price`.

**Causa 3:** Campos vacíos
```bash
# ❌ MAL - name vacío
curl -X POST http://localhost:8080/api/venues \
  -H "Content-Type: application/json" \
  -d '{"name":"","address":"Calle 1","city":"Bogotá","country":"Colombia","capacity":500}'
```
**Solución:** No envíes strings vacíos en campos `@NotBlank`.

## 🔍 Ver respuestas de error

### Ejemplo de error 404:
```json
{
  "timestamp": "2025-10-28T11:04:57",
  "status": 404,
  "error": "Not Found",
  "message": "Evento con ID 1 no encontrado",
  "path": "/api/events/1"
}
```

### Ejemplo de error 400 (validación):
```json
{
  "timestamp": "2025-10-28T11:11:26",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los datos enviados",
  "path": "/api/venues/1",
  "details": [
    "address: La dirección es obligatoria",
    "country: El país es obligatorio",
    "city: La ciudad es obligatoria"
  ]
}
```

## 💡 Recomendación: Usa Swagger UI

En lugar de cURL, usa Swagger UI para probar más fácilmente:
1. Ve a http://localhost:8080/swagger-ui.html
2. Expande un endpoint (ej: POST /api/venues)
3. Click en "Try it out"
4. Llena el JSON de ejemplo
5. Click en "Execute"
6. Ve la respuesta inmediatamente con el código HTTP

Swagger valida los datos antes de enviar y muestra errores claros.

## Notas y mejoras futuras
- Persistencia real con JPA/Hibernate y base de datos.
- Paginación, ordenamiento y filtros.
- Tests unitarios e integración.
- Seguridad (Spring Security, JWT).
- Validaciones adicionales (rango de fechas, precio positivo con BigDecimal)