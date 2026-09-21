# ms-pedidos360-audit

Microservicio de auditoría y trazabilidad.

## Responsabilidad
- Consumir eventos de Kafka
- Almacenar timeline completo de eventos de negocio
- Proveer endpoints de consulta para auditoría
- Trazabilidad: quién / qué / cuándo / desde dónde

## Stack Tecnológico
- Java 17
- Spring Boot 3.x
- Spring Kafka
- Oracle Database

## Tópicos Kafka que consume
- `audit.timeline` - Eventos de auditoría con metadatos completos

## Endpoints (read-only)
- `GET /api/audit/timeline?pedidoId={id}` - Timeline de un pedido
- `GET /api/audit/events?userId={id}` - Eventos de un usuario
- `GET /api/audit/events?from={date}&to={date}` - Eventos en rango de fechas

## Estructura de Evento de Auditoría
```json
{
  "eventId": "uuid",
  "timestamp": "2026-09-20T10:30:00Z",
  "actor": "user@example.com",
  "action": "ORDER_CREATED",
  "entityType": "Order",
  "entityId": "123",
  "details": {...},
  "ipAddress": "192.168.1.1",
  "userAgent": "Mozilla/5.0..."
}
```

## Implementación Fase 3

**Versión simplificada** (sin Kafka ni BD, solo REST API con datos en memoria)

### Funcionalidades implementadas:
- ✅ Registro de eventos de auditoría
- ✅ Consulta de eventos por usuario
- ✅ Consulta de eventos por acción
- ✅ Almacenamiento en memoria (para demo)

### Endpoints REST:
- `GET /api/audit` - Obtener todos los eventos
- `GET /api/audit/user/{userId}` - Eventos de un usuario
- `GET /api/audit/action/{action}` - Eventos por tipo de acción
- `GET /api/audit/{id}` - Obtener evento por ID
- `POST /api/audit` - Crear nuevo evento

### Ejecutar localmente:
```bash
cd backend/audit
mvn spring-boot:run
```

Puerto: **8083**

## Estado
🟢 **Implementado (versión básica para demo)**
