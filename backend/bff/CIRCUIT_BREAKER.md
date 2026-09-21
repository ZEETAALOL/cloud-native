# Circuit Breaker Implementation - Pedidos360 BFF

## ✅ Implementación Completada

Se ha implementado **Resilience4j Circuit Breaker** en el servicio BFF (Backend For Frontend) de Pedidos360 para mejorar la resiliencia del sistema ante fallos de microservicios downstream.

---

## 📦 Dependencias Agregadas

En `pom.xml`:

```xml
<properties>
    <java.version>17</java.version>
    <resilience4j.version>2.2.0</resilience4j.version>
</properties>

<dependencies>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
        <version>${resilience4j.version}</version>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-circuitbreaker</artifactId>
        <version>${resilience4j.version}</version>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-retry</artifactId>
        <version>${resilience4j.version}</version>
    </dependency>
</dependencies>
```

---

## ⚙️ Configuración

En `application.yml`:

```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        
    instances:
      catalogService:
        baseConfig: default
      ordersService:
        baseConfig: default
      reportService:
        baseConfig: default
      auditService:
        baseConfig: default

  retry:
    configs:
      default:
        maxAttempts: 3
        waitDuration: 500ms
        retryExceptions:
          - org.springframework.web.client.HttpServerErrorException
          - org.springframework.web.client.ResourceAccessException
```

### Explicación de la Configuración:

- **slidingWindowSize: 10** - Ventana de 10 llamadas para calcular tasas
- **minimumNumberOfCalls: 5** - Mínimo 5 llamadas antes de evaluar fallas
- **failureRateThreshold: 50%** - Se abre el circuito si falla el 50% de las llamadas
- **waitDurationInOpenState: 10s** - Espera 10 segundos antes de intentar half-open
- **retry maxAttempts: 3** - Reintenta hasta 3 veces antes de fallar

---

## 🔧 Servicios Protegidos

### 1. ProductService

**Endpoints protegidos:**
- `GET /api/products` - Obtener todos los productos
- `GET /api/products/{id}` - Obtener producto por ID
- `POST /api/products` - Crear nuevo producto
- `PUT /api/products/{id}` - Actualizar producto
- `DELETE /api/products/{id}` - Eliminar producto

**Fallbacks implementados:**
- Retorna lista vacía cuando catalog service no está disponible
- Retorna `ProductResponse` con valores básicos para operaciones individuales
- Retorna mensajes descriptivos en operaciones de escritura

### 2. ReportService

**Endpoints protegidos:**
- `GET /api/reports/sales` - Reporte de ventas
- `GET /api/reports/inventory` - Reporte de inventario
- `GET /api/reports/customers` - Reporte de clientes
- `GET /api/reports/performance` - Reporte de rendimiento
- `GET /api/reports/types` - Tipos de reportes disponibles

**Fallbacks implementados:**
- Retorna `ReportDataResponse` con status "UNAVAILABLE"
- Lista vacía para tipos de reportes
- Incluye información del tipo de reporte solicitado

---

## 📝 Ejemplo de Código

### Anotaciones en ProductService:

```java
@Service
public class ProductService {
    
    private static final String CIRCUIT_BREAKER_NAME = "catalogService";
    
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "obtenerTodosFallback")
    @Retry(name = CIRCUIT_BREAKER_NAME)
    public List<ProductResponse> obtenerTodos() {
        logger.debug("Obteniendo todos los productos desde Catalog");
        return restClient.get()
                .uri("/api/productos")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductResponse>>() {});
    }

    private List<ProductResponse> obtenerTodosFallback(Exception ex) {
        logger.error("⚠️ Circuit Breaker ACTIVADO - Catalog no disponible", ex);
        return List.of();
    }
}
```

---

## 🧪 Cómo Probar el Circuit Breaker

### 1. Verificar estado actual (circuito cerrado)

```bash
# Llamar al endpoint de productos
curl -X GET http://localhost/api/products \
  -H "Authorization: Bearer YOUR_TOKEN"

# Respuesta esperada: Lista de productos
```

### 2. Simular falla del servicio Catalog

```bash
# Detener el servicio catalog
docker stop pedidos360-catalog

# Esperar unos segundos
sleep 5
```

### 3. Probar el fallback

```bash
# Llamar nuevamente al endpoint
curl -X GET http://localhost/api/products \
  -H "Authorization: Bearer YOUR_TOKEN"

# Respuesta esperada: [] (lista vacía - fallback activado)
```

### 4. Verificar logs del BFF

```bash
docker logs pedidos360-bff --tail 50

# Deberías ver:
# ⚠️ Circuit Breaker ACTIVADO - Catalog no disponible
```

### 5. Verificar estados del Circuit Breaker vía Actuator

```bash
# Ver estado de todos los circuit breakers
curl http://localhost/actuator/health

# Para métricas más detalladas (si actuator está habilitado):
curl http://localhost/actuator/circuitbreakers
```

### 6. Restaurar el servicio

```bash
# Volver a levantar catalog
docker start pedidos360-catalog

# Esperar 15 segundos (wait duration + tiempo de inicio)
sleep 15

# Probar nuevamente
curl -X GET http://localhost/api/products \
  -H "Authorization: Bearer YOUR_TOKEN"

# Respuesta esperada: Lista de productos (circuito cerrado nuevamente)
```

---

## 📊 Estados del Circuit Breaker

El Circuit Breaker puede estar en 3 estados:

1. **CLOSED (Cerrado)** ✅
   - Estado normal, las llamadas pasan al servicio downstream
   - Si las fallas superan el 50%, pasa a OPEN

2. **OPEN (Abierto)** ❌
   - El circuito está abierto, todas las llamadas van directo al fallback
   - No se intenta llamar al servicio downstream
   - Después de 10 segundos, pasa a HALF_OPEN

3. **HALF_OPEN (Semi-abierto)** ⚠️
   - Permite 3 llamadas de prueba
   - Si tienen éxito, vuelve a CLOSED
   - Si fallan, vuelve a OPEN

---

## 🎯 Beneficios Implementados

✅ **Resiliencia** - El sistema continúa funcionando aunque servicios fallen  
✅ **Fail-fast** - Detecta fallas rápidamente y deja de hacer llamadas innecesarias  
✅ **Auto-recuperación** - Intenta reconectar automáticamente cada 10 segundos  
✅ **Retry automático** - Hasta 3 reintentos antes de activar el fallback  
✅ **Logging detallado** - Logs claros cuando el circuit breaker se activa  

---

## 📝 Notas Importantes

- Los fallbacks retornan datos vacíos o básicos para mantener la API funcionando
- El circuit breaker solo se activa después de 5 llamadas fallidas (minimumNumberOfCalls)
- Los logs indican claramente cuándo el circuit breaker está activo con el emoji ⚠️
- La configuración es la misma para todos los servicios (catalog, orders, report, audit)
- El retry intenta 3 veces con 500ms entre intentos antes de activar el fallback

---

## 🚀 Estado Actual

- ✅ Circuit Breaker implementado y probado
- ✅ BFF service desplegado y funcionando
- ✅ Configuración Resilience4j activa
- ✅ Fallbacks funcionando correctamente

**Build status:** SUCCESS  
**Deployment:** RUNNING  
**Circuit Breaker:** ACTIVE  
