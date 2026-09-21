# ms-pedidos360-notify

Microservicio de notificaciones asíncronas.

## Responsabilidad
- Consumir mensajes de RabbitMQ
- Enviar emails a clientes
- Enviar notificaciones web push
- Generar tickets de cocina

## Stack Tecnológico
- Java 17
- Spring Boot 3.x
- Spring AMQP (RabbitMQ)
- Sin base de datos (stateless)

## Colas RabbitMQ que consume
- `q.cmd.email` - Mensajes de correo
- `q.cmd.kitchen` - Tickets de cocina
- `q.cmd.invoice` - Generación de facturas

## Flujo
1. Orders/Catalog publican evento en RabbitMQ
2. Notify consume el mensaje
3. Procesa y envía notificación correspondiente
4. En caso de error → mensaje va a DLQ

## Implementación Fase 3

**Versión simplificada** (sin RabbitMQ, solo REST API con logging simulado)

### Funcionalidades implementadas:
- ✅ Envío de notificaciones simuladas (EMAIL, SMS, PUSH)
- ✅ Registro en logs de notificaciones enviadas
- ✅ Historial de notificaciones en memoria
- ✅ Consulta por destinatario y canal
- ✅ Datos de ejemplo precargados

### Endpoints REST:
- `GET /api/notifications` - Obtener todas las notificaciones
- `GET /api/notifications/{id}` - Obtener notificación por ID
- `GET /api/notifications/recipient/{recipient}` - Por destinatario
- `GET /api/notifications/channel/{channel}` - Por canal (EMAIL/SMS/PUSH)
- `POST /api/notifications/send` - Enviar nueva notificación

### Ejecutar localmente:
```bash
cd backend/notify
mvn spring-boot:run
```

Puerto: **8086**

## Estado
🟢 **Implementado (versión básica para demo)**
