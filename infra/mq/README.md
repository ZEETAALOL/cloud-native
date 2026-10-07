# RabbitMQ - Mensajería Asíncrona Pedidos360

Configuración de RabbitMQ para el sistema de notificaciones del proyecto Pedidos360.

## 🚀 Inicio Rápido

### Levantar RabbitMQ

```powershell
cd infra/mq
docker compose up -d
```

### Verificar Estado

```powershell
docker compose ps
docker logs pedidos360-rabbitmq
```

### Acceder a Management UI

- **URL**: http://localhost:15672
- **Usuario**: `admin`
- **Contraseña**: `admin123`

## 🏗️ Arquitectura de Mensajería

### Exchanges

| Exchange | Tipo | Descripción |
|----------|------|-------------|
| `cmd.direct` | direct | Enrutamiento exacto por routing key |
| `cmd.topic` | topic | Enrutamiento por patrones (wildcards) |
| `cmd.dead.dlx` | direct | Dead Letter Exchange para mensajes fallidos |

### Colas Principales

| Cola | Propósito | DLQ | TTL |
|------|-----------|-----|-----|
| `q.cmd.email` | Notificaciones por email/push | `q.cmd.email.dlq` | 5 min |
| `q.cmd.kitchen` | Tickets de cocina | `q.cmd.kitchen.dlq` | 5 min |
| `q.cmd.sms` | Notificaciones SMS | `q.cmd.sms.dlq` | 5 min |

### Routing Keys

| Routing Key | Exchange | Destino | Uso |
|-------------|----------|---------|-----|
| `notify.email` | cmd.direct | q.cmd.email | Email directo |
| `notify.kitchen` | cmd.direct | q.cmd.kitchen | Ticket cocina |
| `notify.sms` | cmd.direct | q.cmd.sms | SMS directo |
| `notify.email.*` | cmd.topic | q.cmd.email | Email por patrón |
| `notify.kitchen.*` | cmd.topic | q.cmd.kitchen | Cocina por patrón |

## 📨 Formato de Mensajes

### Notificación Email

```json
{
  "type": "EMAIL",
  "recipient": "cliente@example.com",
  "subject": "Pedido Confirmado #1234",
  "body": "Su pedido ha sido confirmado y está en preparación",
  "orderId": "1234",
  "timestamp": "2026-09-20T22:30:00Z"
}
```

### Ticket Cocina

```json
{
  "type": "KITCHEN",
  "orderId": "1234",
  "items": [
    {
      "product": "Café Latte",
      "quantity": 2,
      "notes": "Sin azúcar"
    }
  ],
  "priority": "HIGH",
  "timestamp": "2026-09-20T22:30:00Z"
}
```

### Notificación SMS

```json
{
  "type": "SMS",
  "phoneNumber": "+56912345678",
  "message": "Tu pedido #1234 está listo para retirar",
  "orderId": "1234",
  "timestamp": "2026-09-20T22:30:00Z"
}
```

## 🔄 Flujo de Mensajes

```
Orders Service (Producer)
    ↓
    ├─→ cmd.direct (notify.email) → q.cmd.email → Notify Service
    ├─→ cmd.direct (notify.kitchen) → q.cmd.kitchen → Kitchen Printer
    └─→ cmd.direct (notify.sms) → q.cmd.sms → SMS Gateway

En caso de fallo (3 reintentos):
    ↓
cmd.dead.dlx → q.cmd.*.dlq (retención 24h)
```

## 🛠️ Configuración de Servicios

### Producer (Orders/BFF)

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin123
```

### Consumer (Notify Service)

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin123
    listener:
      simple:
        acknowledge-mode: auto
        prefetch: 1
        retry:
          enabled: true
          initial-interval: 2000
          max-attempts: 3
          multiplier: 2
```

## 📊 Monitoreo

### Métricas Importantes

- **Messages Ready**: Mensajes esperando ser procesados
- **Messages Unacked**: Mensajes siendo procesados
- **Message Rate**: Tasa de publicación/consumo
- **Consumer Count**: Consumidores activos

### Comandos Útiles

```powershell
# Ver estado de colas
docker exec pedidos360-rabbitmq rabbitmqctl list_queues name messages_ready messages_unacknowledged

# Ver conexiones activas
docker exec pedidos360-rabbitmq rabbitmqctl list_connections

# Ver consumers
docker exec pedidos360-rabbitmq rabbitmqctl list_consumers

# Purgar una cola (desarrollo)
docker exec pedidos360-rabbitmq rabbitmqctl purge_queue q.cmd.email
```

## 🧪 Testing

### Publicar Mensaje de Prueba (Management UI)

1. Ir a **Exchanges** → `cmd.direct`
2. Click en **Publish message**
3. Routing key: `notify.email`
4. Payload:
```json
{
  "type": "EMAIL",
  "recipient": "test@example.com",
  "subject": "Test",
  "body": "Test message",
  "timestamp": "2026-09-20T22:00:00Z"
}
```
5. Click **Publish message**

### Verificar en Cola

1. Ir a **Queues** → `q.cmd.email`
2. Ver **Messages** aumentar
3. Click en **Get messages** para inspeccionar

## 🔒 Seguridad

### Producción

Para entorno de producción, cambiar:

```yaml
environment:
  RABBITMQ_DEFAULT_USER: ${RABBITMQ_USER}
  RABBITMQ_DEFAULT_PASS: ${RABBITMQ_PASSWORD}
```

Y usar variables de entorno o secrets de Docker/Kubernetes.

### Credenciales por Defecto (Solo Desarrollo)

- Usuario: `admin`
- Contraseña: `admin123`

⚠️ **NUNCA usar estas credenciales en producción**

## 🐛 Troubleshooting

### RabbitMQ no inicia

```powershell
# Ver logs
docker logs pedidos360-rabbitmq

# Verificar puertos
netstat -an | Select-String "5672|15672"

# Recrear contenedor
docker compose down -v
docker compose up -d
```

### Mensajes en DLQ

Si hay mensajes en Dead Letter Queues, revisar:
1. Logs del consumer (Notify Service)
2. Formato del mensaje (debe ser JSON válido)
3. Configuración de reintentos

### Consumer no conecta

Verificar:
1. RabbitMQ está corriendo: `docker ps`
2. Network configurada correctamente
3. Credenciales correctas en consumer
4. Consumer tiene dependencia `spring-boot-starter-amqp`

## 📚 Referencias

- [RabbitMQ Official Docs](https://www.rabbitmq.com/documentation.html)
- [Spring AMQP](https://spring.io/projects/spring-amqp)
- [Dead Letter Exchanges](https://www.rabbitmq.com/dlx.html)

---

**Última actualización**: Septiembre 2026
