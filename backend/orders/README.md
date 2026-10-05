# Microservicio Orders - Gestión de Pedidos

Microservicio para la gestión completa del ciclo de vida de pedidos en Pedidos360.

## Funcionalidades

- ✅ CRUD completo de pedidos
- ✅ Gestión de estados con validación de flujo
- ✅ Filtrado por estado, fecha y cliente
- ✅ Estadísticas de pedidos
- ✅ Validación de transiciones de estado

## Estados del Pedido

```
CREADO → ACEPTADO → EN_PREPARACION → DESPACHADO → ENTREGADO
                                            ↓
                                       CANCELADO
```

**Reglas de negocio:**
- No se puede "despachar" sin "aceptar"
- Los estados finales (ENTREGADO, CANCELADO) no pueden cambiar
- En cualquier momento se puede CANCELAR (excepto si ya está ENTREGADO)

## Endpoints

### Crear Pedido
```http
POST /api/orders
Content-Type: application/json

{
  "customerId": "12345",
  "customerName": "Juan Pérez",
  "customerEmail": "juan@example.com",
  "deliveryAddress": "Av. Providencia 123",
  "items": [
    {
      "productId": 1,
      "productName": "Café Latte",
      "quantity": 2,
      "price": 3500.0,
      "notes": "Sin azúcar"
    }
  ],
  "notes": "Entregar en recepción"
}
```

### Obtener Pedido por ID
```http
GET /api/orders/{id}
```

### Cambiar Estado del Pedido
```http
PUT /api/orders/{id}/status
Content-Type: application/json

{
  "status": "ACEPTADO",
  "updatedBy": "operador@pedidos360.cl",
  "notes": "Pedido aceptado y en preparación"
}
```

### Filtrar Pedidos
```http
GET /api/orders?status=CREADO
GET /api/orders?from=2024-01-01T00:00:00&to=2024-12-31T23:59:59
GET /api/orders?status=DESPACHADO&from=2024-01-01T00:00:00&to=2024-12-31T23:59:59
GET /api/orders?customerId=12345
```

### Cancelar Pedido
```http
DELETE /api/orders/{id}?reason=Cliente solicitó cancelación
```

### Estadísticas
```http
GET /api/orders/stats
```

## Compilar y Ejecutar

```bash
# Compilar
mvn clean package

# Ejecutar
java -jar target/ms-pedidos360-orders-1.0.0.jar

# Con Docker
docker build -t pedidos360/orders:latest .
docker run -p 8081:8081 pedidos360/orders:latest
```

## Health Check

```http
GET http://localhost:8081/actuator/health
```

## Próximas Integraciones

- [ ] Publicar eventos a Kafka (orders.events)
- [ ] Enviar notificaciones a RabbitMQ
- [ ] Decrementar stock en Catalog al ACEPTAR
- [ ] Restaurar stock en Catalog al CANCELAR
