# Documentación de la API - Tiquetera Catálogo

## ✅ Implementación Completada

Se ha implementado exitosamente la documentación OpenAPI/Swagger con todas las características solicitadas:

### 1. **Configuración OpenAPI/Swagger UI** ✅
- Configurado en `OpenApiConfig.java` con `@Configuration`
- Título, descripción, contacto y licencia configurados
- Múltiples servidores definidos (desarrollo y pruebas)

### 2. **Descripciones de cada endpoint** ✅
Todos los endpoints documentados con:
- `@Operation`: Summary y description detallada
- `@ApiResponses`: Respuestas HTTP documentadas (200, 201, 204, 400, 404)
- `@Parameter`: Parámetros de path documentados
- `@RequestBody`: Cuerpos de request documentados

### 3. **Ejemplos de Request/Response** ✅
- Ejemplos JSON completos para cada request
- Ejemplos JSON para respuestas exitosas
- Ejemplos de errores (404, 400)

### 4. **Manejo de Errores** ✅
Implementado en `GlobalExceptionHandler.java` con `@RestControllerAdvice`:
- **404 - Not Found**: `ResourceNotFoundException`
- **400 - Bad Request**: 
  - Errores de validación (`MethodArgumentNotValidException`)
  - Errores de tipo (`MethodArgumentTypeMismatchException`)
- **500 - Internal Server Error**: Manejo genérico de excepciones

### 5. **Validaciones en DTOs** ✅
- `@NotBlank`, `@NotNull`, `@Positive` en campos requeridos
- Mensajes de error personalizados en español
- Anotaciones `@Schema` para documentación completa de cada campo

## 📍 URLs de Acceso

### Swagger UI (Interfaz Interactiva)
```
http://localhost:8080/swagger-ui/index.html
```
o simplemente:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON (Especificación)
```
http://localhost:8080/v3/api-docs
```

### OpenAPI YAML
```
http://localhost:8080/v3/api-docs.yaml
```

## 🎯 Endpoints Documentados

### Events API (`/api/events`)
- `GET /api/events` - Obtener todos los eventos
- `GET /api/events/{id}` - Obtener evento por ID
- `GET /api/events/venue/{venueId}` - Obtener eventos por venue
- `POST /api/events` - Crear nuevo evento
- `PUT /api/events/{id}` - Actualizar evento
- `DELETE /api/events/{id}` - Eliminar evento

### Venues API (`/api/venues`)
- `GET /api/venues` - Obtener todos los venues
- `GET /api/venues/{id}` - Obtener venue por ID
- `POST /api/venues` - Crear nuevo venue
- `PUT /api/venues/{id}` - Actualizar venue
- `DELETE /api/venues/{id}` - Eliminar venue

## 🧪 Ejemplos de Prueba

### Crear un Venue
```json
POST /api/venues
Content-Type: application/json

{
  "name": "Teatro Nacional",
  "address": "Calle 71 #10-25",
  "city": "Bogotá",
  "country": "Colombia",
  "capacity": 1500
}
```

### Crear un Evento
```json
POST /api/events
Content-Type: application/json

{
  "name": "Concierto Rock 2025",
  "description": "Gran concierto de rock",
  "eventDate": "2025-12-15T20:00:00",
  "venueId": 1,
  "capacity": 1000,
  "price": 80000.00
}
```

### Ejemplo de Error 404
```json
GET /api/events/999

Response:
{
  "timestamp": "2025-10-28T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Evento con ID 999 no encontrado",
  "path": "/api/events/999"
}
```

### Ejemplo de Error 400 (Validación)
```json
POST /api/events
Content-Type: application/json

{
  "name": "",
  "eventDate": null
}

Response:
{
  "timestamp": "2025-10-28T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error de validación en los datos enviados",
  "path": "/api/events",
  "details": [
    "name: El nombre del evento es obligatorio",
    "eventDate: La fecha del evento es obligatoria"
  ]
}
```

## 🔧 Archivos Modificados/Creados

### Configuración
- ✅ `config/OpenApiConfig.java` - Agregada anotación `@Configuration`

### Controladores
- ✅ `controller/EventController.java` - Documentación completa
- ✅ `controller/VenueController.java` - Documentación completa + método DELETE completado

### DTOs
- ✅ `DTO/EventDTO.java` - Anotaciones `@Schema` agregadas
- ✅ `DTO/VenueDTO.java` - Anotaciones `@Schema` agregadas

### Manejo de Errores
- ✅ `exception/GlobalExceptionHandler.java` - Agregada anotación `@RestControllerAdvice`
- ✅ `exception/ErrorResponse.java` - Anotaciones `@Schema` agregadas
- ✅ `exception/ResourceNotFoundException.java` - (ya existente)

### Archivos de Propiedades
- ✅ `application.properties` - Corregida codificación UTF-8
- ✅ `application-test.properties` - Corregida codificación UTF-8

### Dependencias
- ✅ `pom.xml` - Removida dependencia duplicada de spring-boot-starter-test

## ✨ Características Implementadas

1. **Documentación Completa**: Cada endpoint tiene descripción detallada
2. **Ejemplos Reales**: Request y response con datos de ejemplo
3. **Códigos HTTP**: Todas las respuestas documentadas (200, 201, 204, 400, 404)
4. **Manejo de Errores Global**: Respuestas de error consistentes
5. **Validaciones**: Mensajes de error claros en español
6. **Schemas Documentados**: DTOs completamente documentados
7. **Sin Errores**: Proyecto compila y ejecuta sin errores

## 🚀 Cómo Probar

1. Asegúrate de que la aplicación esté corriendo:
   ```powershell
   cd c:\Users\anonimo\Videos\TiqueteraCatalogo\TiqueteraCatalogo
   ./mvnw.cmd spring-boot:run
   ```

2. Abre tu navegador en: `http://localhost:8080/swagger-ui.html`

3. Prueba los endpoints directamente desde Swagger UI:
   - Expande cualquier endpoint
   - Click en "Try it out"
   - Completa los datos de ejemplo
   - Click en "Execute"
   - Verás la respuesta inmediatamente

## ✅ Estado del Proyecto

- ✅ Configuración OpenAPI/Swagger UI
- ✅ Descripciones de cada endpoint
- ✅ Ejemplos del request/response
- ✅ Manejo básico de errores: 404 y 400
- ✅ Proyecto compila sin errores
- ✅ Aplicación funcional

**Todo implementado correctamente y funcionando!** 🎉
